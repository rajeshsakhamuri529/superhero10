package com.blobcity.activity

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.support.annotation.NonNull
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.blobcity.R
import com.blobcity.adapter.GradeAdapter
import com.blobcity.adapter.RevisionAdapter
import com.blobcity.database.QuizGameDataBase
import com.blobcity.interfaces.GradeClickListener
import com.blobcity.model.GradeResponseModel
import com.blobcity.model.RevisionModel
import com.blobcity.utils.*
import com.blobcity.utils.ConstantPath.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_grade.*
import kotlinx.android.synthetic.main.revision_layout.*
import org.apache.commons.io.FileUtils
import java.io.*
import java.lang.ref.WeakReference
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.collections.ArrayList

class GradeActivity : BaseActivity(), GradeClickListener, PermissionListener  {

   /* override fun onUpdateNeeded(updateUrl: String) {
        val dialog = AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("Please, update app to new version to continue reposting.")
            .setPositiveButton("Update",DialogInterface.OnClickListener { dialog, which ->
                redirectStore(updateUrl);

            })
            .setNegativeButton("No ",DialogInterface.OnClickListener { dialog, which ->
                finish();
            }).create()

        dialog.show();
    }*/

    fun redirectStore(updateUrl : String) {
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
        finish()
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    val storage = FirebaseStorage.getInstance()
    var gradeResponseModelList: ArrayList<GradeResponseModel>?= null
    var gradeVersion: Long?= null
    private val sharedPrefs = SharedPrefs()
    var isBtnIconDownloaded: Boolean = false
    var isIconDownloaded: Boolean = false
    private var listJson: String?= null
    private var auth: FirebaseAuth?= null
    private var mSnackBar: Snackbar? = null
    private var revisionItemList:ArrayList<RevisionModel>?=null
    var revisionModel:RevisionModel? = null

    var databaseHandler: QuizGameDataBase?= null
    var version : String = ""
    var url : String = ""
    override var layoutID: Int = R.layout.activity_splash

    private val PERMISSIONS = arrayOf<String>(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private fun hasPermissions(context: Context, vararg permissions:String):Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (permission in permissions)
            {
                if (ActivityCompat.checkSelfPermission(context, permission) !== PackageManager.PERMISSION_GRANTED)
                {
                    return false
                }
            }
        }
        return true
    }

    override fun initView() {

        databaseHandler = QuizGameDataBase(this);
        auth = FirebaseAuth.getInstance()

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        db.firestoreSettings = settings
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorbottomnav));
        }

        TedPermission.with(this)
            .setPermissionListener(this)
            .setDeniedMessage("If you reject permission,you can not use this service\n"
                    + "\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.INTERNET)
            .check()
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("grade actvity", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token
                Log.e("grade activity","token...."+token);

            })

    }


    override fun onPermissionGranted() {
        signin(sharedPrefs)
    }

    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {

    }

    override fun onResume() {
        super.onResume()
        listJson = sharedPrefs.getPrefVal(this, GRADE_LIST)
        gradeVersion = sharedPrefs.getLongPrefVal(this, GRADE_VERSION)
    }

    private fun downloadFile(url:String, outputFile:File) {
        try
        {
            val u = URL(url)
            val conn = u.openConnection()
            val contentLength = conn.getContentLength()
            val stream = DataInputStream(u.openStream())
            val buffer = ByteArray(contentLength)
            stream.readFully(buffer)
            stream.close()
            val fos = DataOutputStream(FileOutputStream(outputFile))
            fos.write(buffer)
            fos.flush()
            fos.close()
        }
        catch (e: FileNotFoundException) {
            return // swallow a 404
        }
        catch (e: IOException) {
            return // swallow a 404
        }
    }



    companion object {
        class MyAsyncTask internal constructor(context: GradeActivity) : AsyncTask<String, String, String?>() {

            private var resp: String? = null
            private val activityReference: WeakReference<GradeActivity> = WeakReference(context)
            lateinit var activity: GradeActivity
            override fun onPreExecute() {
                activity = activityReference.get()!!
                if (activity == null || activity.isFinishing) return
                //activity.progressBar.visibility = View.VISIBLE
            }

            override fun doInBackground(vararg params: String?): String? {
                /*publishProgress("Sleeping Started") // Calls onProgressUpdate()
                try {
                    val time = params[0]?.times(1000)
                    time?.toLong()?.let { Thread.sleep(it / 2) }
                    publishProgress("Half Time") // Calls onProgressUpdate()
                    time?.toLong()?.let { Thread.sleep(it / 2) }
                    publishProgress("Sleeping Over") // Calls onProgressUpdate()
                    resp = "Android was sleeping for " + params[0] + " seconds"
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    resp = e.message
                } catch (e: Exception) {
                    e.printStackTrace()
                    resp = e.message
                }*/
                try
                {
                    resp = params[0]
                    val dirFile = File(activity.getExternalFilesDir(null), "testcontent.rar")
                    //dirFile.mkdirs()
                    dirFile.createNewFile()
                    val u = URL(params[0])
                    val conn = u.openConnection()
                    val contentLength = conn.getContentLength()
                    val stream = DataInputStream(u.openStream())
                    val buffer = ByteArray(contentLength)
                    stream.readFully(buffer)
                    stream.close()
                    //var file =  File(dirFile, "testcontent.rar");
                    val fos = DataOutputStream(FileOutputStream(dirFile))
                    Log.e("grade activity","buffer......"+buffer.size)
                    fos.write(buffer)
                    fos.flush()
                    fos.close()
                }
                catch (e: FileNotFoundException) {
                    Log.e("grade activity","file not found...."+e);
                    return "" // swallow a 404
                }
                catch (e: IOException) {
                    Log.e("grade activity","IOException...."+e);
                    return "" // swallow a 404
                }

                return resp
            }


            override fun onPostExecute(result: String?) {

                val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return


                if(activity.sharedPrefs.getBooleanPrefVal(activity, ConstantPath.IS_FIRST_TIME)){
                    val dirFile = File((activity.getExternalFilesDir(null)).absolutePath)
                    //dirFile.mkdirs()
                    //var file =  File(dirFile, "testcontent.rar");
                    var iszip = Utils.unpackZip(dirFile.absolutePath,"/testcontent.rar")
                    if(iszip){
                        val dirFile = File(activity.getExternalFilesDir(null),"testcontent.rar")
                        dirFile.delete()
                        activity.sharedPrefs.setBooleanPrefVal(activity, "iscontentdownload", true)
                        activity.navigateToIntro()

                    }else{
                        Log.e("garde activity","zip not done")
                    }

                }else{
                    val dirFile = File(activity.getExternalFilesDir(null),"test")
                    Log.e("garde activity","dir file directory......."+dirFile)
                    FileUtils.deleteDirectory(dirFile);

                    val dirFile1 = File((activity.getExternalFilesDir(null)).absolutePath)
                    //dirFile.mkdirs()
                    //var file =  File(dirFile, "testcontent.rar");
                    var iszip = Utils.unpackZip(dirFile1.absolutePath,"/testcontent.rar")
                    if(iszip){
                        val dirFile2 = File(activity.getExternalFilesDir(null),"testcontent.rar")
                        dirFile2.delete()
                        activity.updateversion()
                        activity.navigateToDashboard("GRADE 6")

                    }else{
                        Log.e("garde activity","zip not done")
                    }



                }




                /*activity.progressBar.visibility = View.GONE
                activity.textView.text = result.let { it }
                activity.myVariable = 100*/
            }

            override fun onProgressUpdate(vararg text: String?) {

                /*val activity = activityReference.get()
                if (activity == null || activity.isFinishing) return

                Toast.makeText(activity, text.firstOrNull(), Toast.LENGTH_SHORT).show()*/

            }
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 112 ) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if(Utils.isOnline(this)){
                    var url = databaseHandler!!.gettesttopicurl()
                    Log.e("grade actvity","is first time........url....."+url);
                    val task = MyAsyncTask(this)
                    task.execute(url)
                }else{
                    Toast.makeText(this,"Internet is required!",Toast.LENGTH_LONG).show();
                }

            }else {
                Toast.makeText(this,"Permissions are required to view the file!",Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun updateversion(){
        databaseHandler!!.updatetestcontentversion(version)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun signin(sharedPrefs: SharedPrefs) {
        if (sharedPrefs.getBooleanPrefVal(this, ConstantPath.IS_LOGGED_IN)) {
            val uid : String = sharedPrefs.getPrefVal(this, ConstantPath.UID)!!

            Log.d("signin","true")

            if(sharedPrefs.getBooleanPrefVal(this, ConstantPath.IS_FIRST_TIME)){

                if(hasPermissions(this, *PERMISSIONS)){

                    if(sharedPrefs.getBooleanPrefVal(this, "iscontentdownload")){
                        navigateToIntro()
                    }else{
                        if(Utils.isOnline(this)){
                            var url = databaseHandler!!.gettesttopicurl()

                            val task = MyAsyncTask(this)
                            task.execute(url)
                        }else{
                            Toast.makeText(this,"Internet is required!",Toast.LENGTH_LONG).show();
                        }
                    }

                }else{

                    requestPermissions(PERMISSIONS, 112)
                }


                //navigateToIntro()
            }else{

                Log.e("grade activity","sharedPrefs.getBooleanPrefVal(this, ISNOTLOGIN)...."+sharedPrefs.getBooleanPrefVal(this, ISNOTLOGIN))

                if(!sharedPrefs.getBooleanPrefVal(this, ISNOTLOGIN)){
                    //this block is for not sign in users
                    if(Utils.isOnline(this@GradeActivity)){

                        val docRef = db.collection("testcontentdownload").document("gVBcBjqHQBLjvrUGwkos")
                        docRef.get().addOnSuccessListener { document ->
                            if (document != null) {
                                Log.e("grade activity", "DocumentSnapshot data: ${document.data}")
                                version = document.data!!.get("TestContentVersion").toString()
                                url = document.data!!.get("TestContentUrl").toString()

                                Log.e("grade activity","version......."+version)
                                Log.e("grade activity","url......."+url)

                                databaseHandler!!.insertTESTCONTENTDOWNLOAD(version,url)
                                sharedPrefs.setBooleanPrefVal(this@GradeActivity, ConstantPath.IS_FIRST_TIME, true)
                                if(hasPermissions(this@GradeActivity, *PERMISSIONS)){
                                    // var url = databaseHandler!!.gettesttopicurl()
                                    if(Utils.isOnline(this@GradeActivity)){
                                        val task = MyAsyncTask(this@GradeActivity)
                                        task.execute(url)
                                    }else{
                                        Toast.makeText(this@GradeActivity,"Internet is required!",Toast.LENGTH_LONG).show();
                                    }

                                }else{

                                    requestPermissions(PERMISSIONS, 112)
                                }


                            } else {
                                Log.e("grade activity", "No such document")
                            }
                        }
                            .addOnFailureListener { exception ->
                                Log.e("grade activity", "get failed with ", exception)

                            }
                        /*val rootRef = FirebaseFirestore.getInstance()
                        val attractionsRef = rootRef.collection("testcontentdownload")
                        attractionsRef.get().addOnCompleteListener(object: OnCompleteListener<QuerySnapshot> {
                            override fun onComplete(@NonNull task: Task<QuerySnapshot>) {
                                if (task.isSuccessful()) {

                                    for (document in task.getResult()!!) {
                                        version = document.data.get("TestContentVersion").toString()
                                        url = document.data.get("TestContentUrl").toString()
                                    }
                                    databaseHandler!!.insertTESTCONTENTDOWNLOAD(version,url)
                                    sharedPrefs.setBooleanPrefVal(this@GradeActivity, ConstantPath.IS_FIRST_TIME, true)
                                    if(hasPermissions(this@GradeActivity, *PERMISSIONS)){
                                        // var url = databaseHandler!!.gettesttopicurl()
                                        if(Utils.isOnline(this@GradeActivity)){
                                            val task = MyAsyncTask(this@GradeActivity)
                                            task.execute(url)
                                        }else{
                                            Toast.makeText(this@GradeActivity,"Internet is required!",Toast.LENGTH_LONG).show();
                                        }

                                    }else{

                                        requestPermissions(PERMISSIONS, 112)
                                    }


                                }
                            }
                        })*/

                    }else{
                        Toast.makeText(this@GradeActivity,"Internet is required!",Toast.LENGTH_LONG).show();
                    }



                }else{

                    //already login user block
                    var dbversion = databaseHandler!!.gettesttopicversion()
                    if(dbversion == null){
                        //this block is for already logged user but content is not downloded
                        if(Utils.isOnline(this@GradeActivity)){

                            val docRef = db.collection("testcontentdownload").document("gVBcBjqHQBLjvrUGwkos")
                            docRef.get().addOnSuccessListener { document ->
                                if (document != null) {
                                    Log.e("grade activity", "DocumentSnapshot data: ${document.data}")
                                    version = document.data!!.get("TestContentVersion").toString()
                                    url = document.data!!.get("TestContentUrl").toString()

                                    Log.e("grade activity","version......."+version)
                                    Log.e("grade activity","url......."+url)

                                    databaseHandler!!.insertTESTCONTENTDOWNLOAD(version,url)
                                    //sharedPrefs.setBooleanPrefVal(this@GradeActivity, ConstantPath.IS_FIRST_TIME, true)
                                    if(hasPermissions(this@GradeActivity, *PERMISSIONS)){
                                        // var url = databaseHandler!!.gettesttopicurl()
                                        if(Utils.isOnline(this@GradeActivity)){
                                            val task = MyAsyncTask(this@GradeActivity)
                                            task.execute(url)
                                        }else{
                                            Toast.makeText(this@GradeActivity,"Internet is required!",Toast.LENGTH_LONG).show();
                                        }

                                    }else{

                                        requestPermissions(PERMISSIONS, 112)
                                    }


                                } else {
                                    Log.e("grade activity", "No such document")
                                }
                            }
                                .addOnFailureListener { exception ->
                                    Log.e("grade activity", "get failed with ", exception)

                                }
                            /*val rootRef = FirebaseFirestore.getInstance()
                            val attractionsRef = rootRef.collection("testcontentdownload")
                            attractionsRef.get().addOnCompleteListener(object: OnCompleteListener<QuerySnapshot> {
                                override fun onComplete(@NonNull task: Task<QuerySnapshot>) {
                                    if (task.isSuccessful()) {

                                        for (document in task.getResult()!!) {
                                            version = document.data.get("TestContentVersion").toString()
                                            url = document.data.get("TestContentUrl").toString()
                                        }
                                        databaseHandler!!.insertTESTCONTENTDOWNLOAD(version,url)
                                        //sharedPrefs.setBooleanPrefVal(this@GradeActivity, ConstantPath.IS_FIRST_TIME, true)
                                        if(hasPermissions(this@GradeActivity, *PERMISSIONS)){
                                            // var url = databaseHandler!!.gettesttopicurl()
                                            if(Utils.isOnline(this@GradeActivity)){
                                                val task = MyAsyncTask(this@GradeActivity)
                                                task.execute(url)
                                            }else{
                                                Toast.makeText(this@GradeActivity,"Internet is required!",Toast.LENGTH_LONG).show();
                                            }

                                        }else{

                                            requestPermissions(PERMISSIONS, 112)
                                        }


                                    }
                                }
                            })*/

                        }else{
                            Toast.makeText(this@GradeActivity,"Internet is required!",Toast.LENGTH_LONG).show();
                        }

                    }else{
                       //this block is for already logged user but content is downloaded
                            if(Utils.isOnline(this@GradeActivity)){

                                val docRef = db.collection("testcontentdownload").document("gVBcBjqHQBLjvrUGwkos")
                                docRef.get().addOnSuccessListener { document ->
                                        if (document != null) {
                                            Log.e("grade activity", "DocumentSnapshot data: ${document.data}")
                                            version = document.data!!.get("TestContentVersion").toString()
                                            url = document.data!!.get("TestContentUrl").toString()

                                            Log.e("grade activity","version......."+version)
                                            Log.e("grade activity","url......."+url)

                                            var dbversion = databaseHandler!!.gettesttopicversion()
                                            if(dbversion != version) {
                                                if (hasPermissions(this@GradeActivity, *PERMISSIONS)) {
                                                    // var url = databaseHandler!!.gettesttopicurl()
                                                    //if(Utils.isOnline(this@GradeActivity)){
                                                    val task = MyAsyncTask(this@GradeActivity)
                                                    task.execute(url)

                                                } else {

                                                    requestPermissions(PERMISSIONS, 112)
                                                }
                                            }else{

                                                navigateToDashboard("GRADE 6")
                                            }

                                        } else {
                                            Log.e("grade activity", "No such document")
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("grade activity", "get failed with ", exception)

                                    }

                                /*val rootRef = FirebaseFirestore.getInstance()
                                val attractionsRef = rootRef.collection("testcontentdownload")
                                attractionsRef.get().addOnCompleteListener(object: OnCompleteListener<QuerySnapshot> {
                                    override fun onComplete(@NonNull task: Task<QuerySnapshot>) {
                                        if (task.isSuccessful()) {

                                            for (document in task.getResult()!!) {
                                                version = document.data.get("TestContentVersion").toString()
                                                url = document.data.get("TestContentUrl").toString()
                                            }
                                            var dbversion = databaseHandler!!.gettesttopicversion()
                                            if(dbversion != version){
                                                if(hasPermissions(this@GradeActivity, *PERMISSIONS)){
                                                    // var url = databaseHandler!!.gettesttopicurl()
                                                    //if(Utils.isOnline(this@GradeActivity)){
                                                    val task = MyAsyncTask(this@GradeActivity)
                                                    task.execute(url)


                                            }else{

                                                requestPermissions(PERMISSIONS, 112)
                                            }
                                        }else{
                                            navigateToDashboard("GRADE 6")
                                        }

                                    }else{
                                        navigateToDashboard("GRADE 6")
                                    }
                                }
                            })*/


                        }else {
                                Log.e("grade activity","user offline..........")
                                navigateToDashboard("GRADE 6")
                            }



                    }


                }









            }


            // GRADE SCREEN
           /* val gson = Gson()
            if (!TextUtils.isEmpty(listJson)){
                if (isNetworkConnected()) {
                    remoteConfig.fetch().addOnCompleteListener(object : OnCompleteListener<Void> {
                        override fun onComplete(task: Task<Void>) {
                            if (task.isSuccessful) {
                                if (gradeVersion != remoteConfig.getLong("gradesVer")) {
                                    getdataFromFirestore()
                                }else{
                                    setLocalData(gson)
                                }
                            }
                        }
                    })
                }
                else{
                    setLocalData(gson)
                }
            }else{
                if (isNetworkConnected()){
                    val root = Environment.getExternalStorageDirectory().toString()
                    Utils.makeDir("$root/blobcity/images")
                    getdataFromFirestore()
                }
            }*/


            /*val storage = FirebaseStorage.getInstance()
            val storageRef = storage.getReference().child("astra-quiz-v.1.0.zip");

            val imageFile = File.createTempFile("test", "zip");

            Log.d(TAG,"Temp file : " + imageFile.getAbsolutePath());

            storageRef.getFile(imageFile)
                .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot> {
                    Toast.makeText(applicationContext, "file created", Toast.LENGTH_SHORT).show()
                    Log.d("file","created :  "+it.toString()+"!"+it.totalByteCount);

                    //startApp()
                }).addOnFailureListener(OnFailureListener {
                    Log.d("file","not created : "+it.toString());
                    Toast.makeText(applicationContext, "An error accoured", Toast.LENGTH_SHORT).show()
                })

            val user = auth.currentUser
            TedPermission.with(this)
                .setPermissionListener(this)
                .setDeniedMessage("If you reject permission,you can not use this service\n"
                        + "\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()*/

            //TODO: encryption
            /* Dexter.withActivity(this)
                 .withPermissions(*arrayOf(
                     Manifest.permission.WRITE_EXTERNAL_STORAGE,
                     Manifest.permission.READ_EXTERNAL_STORAGE
                 ))
                 .withListener(object : MultiplePermissionsListener{

                     override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                         Log.d(TAG,"onPermmissionChecked");
                         Toast.makeText(this@DashBoardActivity,"You should ",Toast.LENGTH_LONG).show()
                     }

                     override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                         Log.d(TAG,"onPermissionRationaleShouldBeShown");
                         Toast.makeText(this@DashBoardActivity,"You should accept permission",Toast.LENGTH_LONG).show()
                     }

                 })
                 .check()

             val root = Environment.getExternalStorageDirectory().toString()
             myDir = File("$root/saved_images")
             if(!myDir.exists()){
                 myDir.mkdirs()

                 val drawable = ContextCompat.getDrawable(this,R.drawable.rectangle_tab)
                 val bitmapDrawable = drawable as BitmapDrawable
                 val bitmap = bitmapDrawable.bitmap
                 val stream = ByteArrayOutputStream()
                 bitmap.compress(Bitmap.CompressFormat.PNG,100,stream)
                 val input = ByteArrayInputStream(stream.toByteArray())

                 val outputFileEnc = File(myDir, ENC)

                 try{
                     MyEncrypter.encryptToFile(key, specString,input,FileOutputStream(outputFileEnc))
                     Toast.makeText(this,"ENCRYPTED",Toast.LENGTH_LONG).show()
                 }catch (e:Exception)
                 {
                     Log.d("EXCEPTION : ",e.toString()+"!")
                 }
             }*/

        } else {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnected == true
            Log.d("isConnected",isConnected.toString()+"!")
            if(isNetworkConnected()) {
                auth!!.signInAnonymously()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            Log.e("grade activity", "task is successful.............")
                            val docRef = db.collection("testcontentdownload").document("gVBcBjqHQBLjvrUGwkos")
                            docRef.get()
                                .addOnSuccessListener { document ->
                                    if (document != null) {
                                        Log.e("grade activity", "DocumentSnapshot data: ${document.data}")
                                        var version = document.data!!.get("TestContentVersion").toString()
                                        var url = document.data!!.get("TestContentUrl").toString()

                                        Log.e("grade activity","version......."+version)
                                        Log.e("grade activity","url......."+url)

                                        databaseHandler!!.insertTESTCONTENTDOWNLOAD(version,url)

                                        // Sign in success, update UI with the signed-in user's information
                                        val user = auth!!.currentUser
                                        sharedPrefs.setBooleanPrefVal(this@GradeActivity, ConstantPath.IS_LOGGED_IN, true)
                                        sharedPrefs.setBooleanPrefVal(this@GradeActivity, ConstantPath.IS_FIRST_TIME, true)
                                        sharedPrefs.setPrefVal(this@GradeActivity, ConstantPath.UID, user!!.uid)

                                        Log.d("anonymous auth done","true")
                                        TedPermission.with(this@GradeActivity)
                                            .setPermissionListener(this@GradeActivity)
                                            .setDeniedMessage(
                                                "If you reject permission,you can not use this service\n"
                                                        + "\nPlease turn on permissions at [Setting] > [Permission]"
                                            )
                                            .setPermissions(Manifest.permission.INTERNET)
                                            .check()



                                    } else {
                                        Log.e("grade activity", "No such document")
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("grade activity", "get failed with ", exception)
                                    Toast.makeText(this@GradeActivity,"Internet is required!",Toast.LENGTH_LONG).show();

                                }

                            /*val rootRef = FirebaseFirestore.getInstance()
                            val attractionsRef = rootRef.collection("testcontentdownload").document("gVBcBjqHQBLjvrUGwkos")
                            attractionsRef.get().addOnCompleteListener(object: OnCompleteListener<DocumentSnapshot> {
                                override fun onComplete(@NonNull task:Task<DocumentSnapshot>) {
                                    if (task.isSuccessful())
                                    {
                                        Log.e("grade activity","task......."+task.result)
                                        Log.e("grade activity","task...isempty...."+task.result!!.isEmpty)
                                        for (document in task.getResult()!!)
                                        {
                                            var version = document.data.get("TestContentVersion").toString()
                                            var url = document.data.get("TestContentUrl").toString()

                                            Log.e("grade activity","version......."+version)
                                            Log.e("grade activity","url......."+url)

                                            databaseHandler!!.insertTESTCONTENTDOWNLOAD(version,url)
                                        }



                                        // Sign in success, update UI with the signed-in user's information
                                        val user = auth!!.currentUser
                                        sharedPrefs.setBooleanPrefVal(this@GradeActivity, ConstantPath.IS_LOGGED_IN, true)
                                        sharedPrefs.setBooleanPrefVal(this@GradeActivity, ConstantPath.IS_FIRST_TIME, true)
                                        sharedPrefs.setPrefVal(this@GradeActivity, ConstantPath.UID, user!!.uid)

                                        Log.d("anonymous auth done","true")
                                        TedPermission.with(this@GradeActivity)
                                            .setPermissionListener(this@GradeActivity)
                                            .setDeniedMessage(
                                                "If you reject permission,you can not use this service\n"
                                                        + "\nPlease turn on permissions at [Setting] > [Permission]"
                                            )
                                            .setPermissions(Manifest.permission.INTERNET)
                                            .check()
                                    }
                                }
                            })*/


                        } else {
                            // If sign in fails, display a message to the user.
                            //.makeText(baseContext, "Authentication failed. Check Internet Connection", Toast.LENGTH_SHORT).show()
                            mSnackBar = Snackbar.make(
                                findViewById(R.id.splash_cl),
                                "Auth Failed :(",
                                Snackbar.LENGTH_LONG
                            ) //Assume "rootLayout" as the root layout of every activity.
                            mSnackBar?.duration = Snackbar.LENGTH_INDEFINITE
                            mSnackBar?.setAction("Retry", { signin(sharedPrefs) })
                            mSnackBar?.show()
                        }
                    }
            }else{
                mSnackBar = Snackbar.make(
                    findViewById(R.id.splash_cl),
                    "No Internet Connection",
                    Snackbar.LENGTH_LONG
                ) //Assume "rootLayout" as the root layout of every activity.
                mSnackBar?.duration = Snackbar.LENGTH_INDEFINITE
                mSnackBar?.setAction("Retry", { signin(sharedPrefs) })
                mSnackBar?.show()
            }
        }
    }

    private fun setLocalData(gson: Gson){
        /*val pathStringList: ArrayList<String> = ArrayList()
        for (imagePath in Utils.getListOfFilesFromFolder(ConstantPath.loaclAstraCardPath)){
            if (imagePath.contains("png")){
                pathStringList.add(imagePath)
            }
        }
        *//*Collections.sort(pathStringList)*//*
        reverseListString(pathStringList)
        pathStringList.forEachIndexed { index, s ->
            Log.e("imagename: ", s)
        }*/
        val type = object : TypeToken<List<GradeResponseModel>>() {}.type
        gradeResponseModelList = gson.fromJson(listJson, type)
        rcv_grade.adapter = GradeAdapter(this,
            this, gradeResponseModelList!!)
    }

    private fun getdataFromFirestore(){
        Log.d("getDataFromFirestore","YES");
        remoteConfig.fetch().addOnCompleteListener(object : OnCompleteListener<Void>{
            override fun onComplete(task: Task<Void>) {
                if (task.isSuccessful){
                    remoteConfig.activateFetched()
                    gradeVersion = remoteConfig.getValue("gradesVer").asLong()
                    Log.d("getDataFromFirestore",gradeVersion.toString()+"!");

                }
            }

        })
        progress_bar.visibility = View.VISIBLE
        db.collection("courses").get()
            .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot>{
                override fun onComplete(task: Task<QuerySnapshot>) {
                    if (task.isSuccessful){
                        gradeResponseModelList = ArrayList()
                        for (document in task.getResult()!!){
                            val gradeMap = document.data
                            val gradeResponseModel = GradeResponseModel()
                            gradeResponseModel.active = gradeMap.get("active") as Boolean?
                            gradeResponseModel.disPos = gradeMap.get("disPos") as Long?
                            gradeResponseModel.btnicon = gradeMap.get("btnicon") as Map<String, String>?
                            gradeResponseModel.icon = gradeMap.get("icon") as Map<String, String>?
                            gradeResponseModel.link = gradeMap.get("link") as String?
                            gradeResponseModel.title = gradeMap.get("title") as String?
                            gradeResponseModel.version = gradeMap.get("version") as Long?

                            gradeResponseModelList!!.add(gradeResponseModel)
                        }
                        reverseList(gradeResponseModelList!!)

                        gradeResponseModelList!!.forEachIndexed { index, grade ->
                            val btnStorageRef = storage
                                .getReferenceFromUrl(grade.btnicon!!.get("mdpi")!!)
                            val iconStorageRef = storage
                                .getReferenceFromUrl(grade.icon!!.get("mdpi")!!)

                            val root = Environment.getExternalStorageDirectory().toString()
                            val btnName = "${grade.title}btn.png"
                            val iconName = "${grade.title}icon.png"
                            val btnPath = "$root/blobcity/images/$btnName"
                            val iconPath = "$root/blobcity/images/$iconName"

                            val btnFile = File(btnPath)
                            val iconFile = File(iconPath)

                            downloadImages(btnStorageRef, btnFile, index,
                                false, true)
                            downloadImages(iconStorageRef, iconFile, index,
                                true, false)
                            grade.iconPath = iconPath
                            grade.btnPath = btnPath
                        }

                    }else{
                        Log.e("data failure: ", task.exception.toString())
                    }
                }
            })
    }

    private fun downloadImages(storageRefs: StorageReference,
                               file: File,
                               index: Int,
                               isIconDownload: Boolean,
                               isBtnDownload: Boolean){
        storageRefs.getFile(file)
            .addOnCompleteListener(object : OnCompleteListener<FileDownloadTask.TaskSnapshot>{
                override fun onComplete(task: Task<FileDownloadTask.TaskSnapshot>) {
                    if (task.isSuccessful){
                        if (isIconDownload == true) {
                            if ((gradeResponseModelList!!.size - 1) == index) {
                                isIconDownloaded = true
                                setAdapter()
                            }
                        }
                        if (isBtnDownload == true) {
                            if ((gradeResponseModelList!!.size - 1) == index) {
                                isBtnIconDownloaded = true
                                setAdapter()
                            }
                        }
                    }
                    else{
                        isBtnIconDownloaded = false
                        isIconDownloaded = false
                        progress_bar.visibility = View.INVISIBLE
                        Log.e("data failure: ", task.exception.toString())
                    }
                }
            })
    }

    private fun setAdapter(){
        if (isBtnIconDownloaded && isIconDownloaded) {
            progress_bar.visibility = View.INVISIBLE
            sharedPrefs.setLongPrefVal(this, GRADE_VERSION, gradeVersion!!)
            val gson = Gson()
            val listJson = gson.toJson(gradeResponseModelList!!)
            sharedPrefs.setPrefVal(this, GRADE_LIST, listJson)
            rcv_grade.adapter = GradeAdapter(this, this, gradeResponseModelList!!)
        }
    }

    private fun reverseListString(pathStrings: List<String>): List<String> {
        Collections.sort(pathStrings) { pathStringList, t1 ->
            val nextPos = t1
            val currentPos = pathStringList
            currentPos!!.compareTo(nextPos!!)
        }
        return pathStrings
    }

    private fun reverseList(gradeResponseModelList: List<GradeResponseModel>)
            : List<GradeResponseModel> {
        Collections.sort(gradeResponseModelList) { gradeModel, t1 ->
            val nextPos = t1.disPos
            val currentPos = gradeModel.disPos
            currentPos!!.compareTo(nextPos!!)
        }
        return gradeResponseModelList
    }

    override fun click(link: String, title: String) {
        val gradeSharedPrefs = sharedPrefs.getPrefVal(this, title)
        if (!TextUtils.isEmpty(gradeSharedPrefs)) {
            if (isNetworkConnected()){
                remoteConfig.fetch().addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(task: Task<Void>) {
                        if (task.isSuccessful) {
                            if (gradeVersion != remoteConfig.getLong("gradesVer")) {
                                downloadFolder(link, title)
                            }else{
                                navigateToDashboard(title)
                            }
                        }
                    }
                })
            }else{
                navigateToDashboard(title)
            }
        }else{
            if (isNetworkConnected()) {
                downloadFolder(link, title)
            }
        }

        /*val connManager : ConnectivityManager= getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val mWifi : NetworkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            val network : NetworkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

            if (mWifi.isConnected()) {
                // Do whatever
                Log.e("wifi", "connected")
            }else{
                Log.e("wifi", "not connected")
            }
            if (network.isConnected()) {
                // Do whatever
                Log.e("network", "connected")
            }else{
                Log.e("network", "not connected")
            }*/
    }

    private fun downloadFolder(link: String, title: String){

        val folderStorageRef = storage
            .getReferenceFromUrl(link)
        val root = Environment.getExternalStorageDirectory().toString()
        val fileName = link.substring(link.lastIndexOf('/') + 1)
        val filePath = "$root/blobcity/$fileName"
        Log.e("fileName:", fileName)
        val file = File(filePath)
        progress_bar.visibility = View.VISIBLE
        folderStorageRef.getFile(file)
            .addOnCompleteListener(object : OnCompleteListener<FileDownloadTask.TaskSnapshot> {
                override fun onComplete(task: Task<FileDownloadTask.TaskSnapshot>) {
                    if (task.isSuccessful) {
                        Utils.unpackZip("$root/blobcity/", fileName)
                        progress_bar.visibility = View.INVISIBLE
                        sharedPrefs.setPrefVal(this@GradeActivity, title, title)
                        navigateToDashboard(title)
                    }
                }
            })
    }

    fun navigateToSignIn(){
        val intent = Intent(
            this@GradeActivity,
            SignInActivity::class.java
        )
        intent.putExtra(TITLE_TOPIC, title)
        intent.putExtra(FIRST_TIME, "first time")
        startActivity(intent)
    }
    fun navigateToIntro(){
        val intent = Intent(
            this@GradeActivity,
            SignInActivity::class.java
        )
        intent.putExtra(TITLE_TOPIC, title)
        intent.putExtra(FIRST_TIME, "first time")
        startActivity(intent)
        finish()
    }
    fun navigateToDashboard(title: String){
        val intent = Intent(
            this@GradeActivity,
            DashBoardActivity::class.java
        )
        intent.putExtra(TITLE_TOPIC, title)
        startActivity(intent)
        finish()
    }
}
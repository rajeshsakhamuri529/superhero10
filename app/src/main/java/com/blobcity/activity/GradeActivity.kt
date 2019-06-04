package com.blobcity.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Environment
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.blobcity.R
import com.blobcity.adapter.GradeAdapter
import com.blobcity.interfaces.GradeClickListener
import com.blobcity.model.GradeResponseModel
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.SharedPrefs
import com.blobcity.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_grade.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class GradeActivity : BaseActivity(), GradeClickListener, PermissionListener {

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

    override fun setLayout(): Int {
        return R.layout.activity_grade
    }

    override fun initView() {

        TedPermission.with(this)
            .setPermissionListener(this)
            .setDeniedMessage("If you reject permission,you can not use this service\n"
                    + "\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
        auth = FirebaseAuth.getInstance()
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

    private fun signin(sharedPrefs: SharedPrefs) {
        if (sharedPrefs.getBooleanPrefVal(this, ConstantPath.IS_LOGGED_IN)) {
            val uid : String = sharedPrefs.getPrefVal(this, ConstantPath.UID)!!
            Toast.makeText(baseContext, "UID "+uid, Toast.LENGTH_SHORT).show()

            val gson = Gson()
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
            }
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
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(baseContext, "Logged In", Toast.LENGTH_SHORT).show()
                            val user = auth!!.currentUser
                            sharedPrefs.setBooleanPrefVal(this, ConstantPath.IS_LOGGED_IN, true)
                            sharedPrefs.setPrefVal(this, ConstantPath.UID, user!!.uid)

                            TedPermission.with(this)
                                .setPermissionListener(this)
                                .setDeniedMessage(
                                    "If you reject permission,you can not use this service\n"
                                            + "\nPlease turn on permissions at [Setting] > [Permission]"
                                )
                                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .check()
                        } else {
                            // If sign in fails, display a message to the user.
                            //.makeText(baseContext, "Authentication failed. Check Internet Connection", Toast.LENGTH_SHORT).show()
                            mSnackBar = Snackbar.make(
                                findViewById(com.blobcity.R.id.rl_dashboard),
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
                    findViewById(R.id.rl_dashboard),
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
        remoteConfig.fetch().addOnCompleteListener(object : OnCompleteListener<Void>{
            override fun onComplete(task: Task<Void>) {
                if (task.isSuccessful){
                    remoteConfig.activateFetched()
                    gradeVersion = remoteConfig.getValue("gradesVer").asLong()
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

    fun navigateToDashboard(title: String){
        val intent = Intent(
            this@GradeActivity,
            DashBoardActivity::class.java
        )
        intent.putExtra(TITLE_TOPIC, title)
        startActivity(intent)
    }
}
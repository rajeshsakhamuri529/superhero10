package com.blobcity.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.util.Log
import android.view.MenuItem
import com.blobcity.R
import com.blobcity.fragment.AstraCardFragment
import com.blobcity.fragment.ChapterFragment
import com.blobcity.fragment.SettingFragment
import com.blobcity.utils.ConstantPath.TITLE_TOPIC
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.io.File

class DashBoardActivity : BaseActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {

    private var fragment: Fragment? = null
    lateinit var gradeTitle: String

    override var layoutID: Int = R.layout.activity_dashboard

    override fun initView() {
        gradeTitle = "GRADE 6"
        /*gradeTitle = intent.getStringExtra(TITLE_TOPIC)*/
        loadFragment(ChapterFragment())
        navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.nav_chapter -> {
                fragment = ChapterFragment()
            }

            com.blobcity.R.id.nav_astra_cards -> {
                fragment = AstraCardFragment()
            }

            com.blobcity.R.id.nav_settings -> {
                fragment = SettingFragment()
            }
        }

        return loadFragment(fragment!!)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        //switching fragment
        if (fragment != null) {
            val bundle = Bundle()
            bundle.putString(TITLE_TOPIC, gradeTitle)
            fragment.arguments = bundle
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()

            return true
        }
        return false
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        postponeEnterTransition()
        if (resultCode == Activity.RESULT_OK) {
            (fragment as AstraCardFragment).activityReenter(data!!)
        }
        Log.d("Activity reenter",resultCode.toString()+"!"+data.toString())
    }

    lateinit var myDir: File

    companion object {
        private val ENC = "enc"
        private val DEC = "dec.png"

        private val key = "PDY8o0tPHNYz1FG7"
        private val specString = "yoe6Nd84MOZCzbb0"
    }

    /*private fun signin(sharedPrefs: SharedPrefs) {
        if (sharedPrefs.getBooleanPrefVal(this, IS_LOGGED_IN)) {
            Log.d(TAG, "signInAnonymously:Already Logged In")
            val uid : String = sharedPrefs.getPrefVal(this, ConstantPath.UID)!!
            Log.d(TAG,uid)
            Toast.makeText(baseContext, "UID "+uid, Toast.LENGTH_SHORT).show()

            *//*val storage = FirebaseStorage.getInstance()
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
                .check()*//*

            //TODO: encryption
           *//* Dexter.withActivity(this)
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
            }*//*

        } else {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnected == true
            Log.d("isConnected",isConnected.toString()+"!")
            if(isNetworkConnected()) {
                auth.signInAnonymously()
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(baseContext, "Logged In", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "signInAnonymously:success")
                            val user = auth.currentUser
                            sharedPrefs.setBooleanPrefVal(this, IS_LOGGED_IN, true)
                            sharedPrefs.setPrefVal(this, UID, user!!.uid)

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
                            Log.w(TAG, "signInAnonymously:failure", task.exception)
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
    }*/

    /*override fun onPermissionGranted() {
        ;

        *//*databaseRefrence!!.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                topicStatusModelList = ArrayList()
                for (postSnapshot in dataSnapshot.children) {
                    Log.e("snap", postSnapshot.value.toString())
                    val topicStatusModel: TopicStatusModel = postSnapshot!!.getValue(TopicStatusModel::class.java)!!
                    topicStatusModelList!!.add(topicStatusModel)
                    if (topicStatusModelList != null){
                        if (topicStatusModelList!!.size > 0){
                            for (branchItem in branchesItemList!!) {
                                val branchId = branchItem.id
                                branchItem.basic = 0
                                branchItem.intermediate = 0
                                branchItem.advance = 0
                                for (topicStatusModels in topicStatusModelList!!) {
                                    val id = topicStatusModels.topicId
                                    val level = topicStatusModels.topicLevel

                                    if (id!!.contains(branchId)) {
                                        if (level!!.contains("basic")) {
                                            branchItem.basic = 1
                                        }
                                        if (level.contains("intermediate")) {
                                            branchItem.intermediate = 1
                                        }
                                        if (level.contains("advance")) {
                                            branchItem.advance = 1
                                        }
                                    }

                                }
                            }
                        }
                    }
                    adapter!!.notifyDataSetChanged()
                    Log.e("topicStatusDb", topicStatusModel.topicLevel)
                }
            }
        })*//*

    }

    override fun onPermissionDenied(deniedPermissions: List<String>) {

    }*/
}
package com.blobcity.activity

import android.content.Intent
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.blobcity.adapter.GradeAdapter
import com.blobcity.interfaces.GradeClickListener
import com.blobcity.model.GradeResponseModel
import com.blobcity.utils.ConstantPath.GRADE_LIST
import com.blobcity.utils.ConstantPath.GRADE_VERSION
import com.blobcity.utils.SharedPrefs
import com.blobcity.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_grade.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class GradeActivity : BaseActivity(), GradeClickListener {

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    val storage = FirebaseStorage.getInstance()
    var gradeResponseModelList: ArrayList<GradeResponseModel>?= null
    var gradeVersion: Long?= null
    val sharedPrefs = SharedPrefs()
    var isBtnIconDownloaded: Boolean = false
    var isIconDownloaded: Boolean = false
    var listJson: String?= null

    override fun setLayout(): Int {
        return com.blobcity.R.layout.activity_grade
    }

    override fun initView() {
        listJson = sharedPrefs.getPrefVal(this, GRADE_LIST)
        gradeVersion = sharedPrefs.getLongPrefVal(this, GRADE_VERSION)
        val gson = Gson()
        if (!TextUtils.isEmpty(listJson)){
            if (isNetworkConnected()) {
                remoteConfig.fetch().addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(task: Task<Void>) {
                        if (task.isSuccessful) {
                            if (gradeVersion != remoteConfig.getLong("gradesVer")) {
                                getdataFromFirestore()
                            }else{
                                val type = object : TypeToken<List<GradeResponseModel>>() {}.type
                                gradeResponseModelList = gson.fromJson(listJson, type)
                                rcv_grade.adapter = GradeAdapter(this@GradeActivity,
                                    this@GradeActivity, gradeResponseModelList!!)
                            }
                        }
                    }
                })
            }
            else{
                val type = object : TypeToken<List<GradeResponseModel>>() {}.type
                gradeResponseModelList = gson.fromJson(listJson, type)
                rcv_grade.adapter = GradeAdapter(this, this, gradeResponseModelList!!)

            }
        }else{
            if (isNetworkConnected()){
                val root = Environment.getExternalStorageDirectory().toString()
                Utils.makeDir("$root/blobcity/images")
                getdataFromFirestore()
            }
        }
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

    private fun reverseList(gradeResponseModelList: List<GradeResponseModel>)
            : List<GradeResponseModel> {
        Collections.sort(gradeResponseModelList) { gradeModel, t1 ->
            val nextPos = t1.disPos
            val currentPos = gradeModel.disPos
            currentPos!!.compareTo(nextPos!!)
        }
        return gradeResponseModelList
    }

    override fun click(link: String) {
        if (!TextUtils.isEmpty(listJson)) {
            if (isNetworkConnected()){
                remoteConfig.fetch().addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(task: Task<Void>) {
                        if (task.isSuccessful) {
                            if (gradeVersion != remoteConfig.getLong("gradesVer")) {
                                downloadFolder(link)
                            }else{
                                val intent = Intent(
                                    this@GradeActivity,
                                    DashBoardActivity::class.java
                                )
                                startActivity(intent)
                            }
                        }
                    }
                })
            }else{
                val intent = Intent(
                    this@GradeActivity,
                    DashBoardActivity::class.java
                )
                startActivity(intent)
            }
        }else{
            if (isNetworkConnected()) {
                downloadFolder(link)
            }
        }
    }

    private fun downloadFolder(link: String){
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
                        val intent = Intent(
                            this@GradeActivity,
                            DashBoardActivity::class.java
                        )
                        startActivity(intent)

                    }
                }
            })
    }
}
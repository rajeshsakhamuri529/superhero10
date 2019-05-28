package com.blobcity.activity

import android.content.Intent
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.blobcity.R
import com.blobcity.adapter.GradeAdapter
import com.blobcity.interfaces.GradeClickListener
import com.blobcity.model.GradeResponseModel
import com.blobcity.utils.ConstantPath.GRADE_LIST
import com.blobcity.utils.ConstantPath.GRADE_VERSION
import com.blobcity.utils.SharedPrefs
import com.blobcity.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_grade.*
import java.io.File
import java.util.*

class GradeActivity : BaseActivity(), GradeClickListener {

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    val storage = FirebaseStorage.getInstance()
    var gradeResponseModelList: ArrayList<GradeResponseModel>?= null
    var gradeVersion: Long?= null
    val sharedPrefs = SharedPrefs()

    override fun setLayout(): Int {
        return R.layout.activity_grade
    }

    override fun initView() {
        val listJson = sharedPrefs.getPrefVal(this, GRADE_LIST)
        gradeVersion = sharedPrefs.getLongPrefVal(this, GRADE_VERSION)
        val gson = Gson()
        if (!TextUtils.isEmpty(listJson)){
            if(isNetworkConnected()) {
                remoteConfig.fetch().addOnCompleteListener(object : OnCompleteListener<Void>{
                    override fun onComplete(task: Task<Void>) {
                        if (task.isSuccessful){
                            if (gradeVersion == remoteConfig.getLong("gradesVer")){
                                getdataFromFirestore()
                            }
                        }
                    }
                })
                //isnetworkConnected && version is changed
            }else{
                //show local
                gradeResponseModelList = gson.fromJson<ArrayList<GradeResponseModel>>(listJson,
                    GradeResponseModel::class.java)
            }
        }else{
            if (isNetworkConnected()){
                val root = Environment.getExternalStorageDirectory().toString()
                Utils.makeDir("$root/blobcity/images")
                getdataFromFirestore()
            }
        }
        rcv_grade.adapter = GradeAdapter(this, this)

    }

    private fun getdataFromFirestore(){
        remoteConfig.fetch().addOnCompleteListener(object : OnCompleteListener<Void>{
            override fun onComplete(task: Task<Void>) {
                if (task.isSuccessful){
                    remoteConfig.activateFetched()
                    gradeVersion = remoteConfig.getValue("gradesVer").asLong()
                    /*Log.e("gradeVer", gradeVersion.toString())
                    Log.e("minApp", remoteConfig.getValue("minAppVer").asDouble().toString())
                    Log.e("updateTimeStamp", remoteConfig.getValue("updateTimeStamp").asString())*/
                }
            }

        })

        /*sharedPrefs.setLongPrefVal(this, GRADE_VERSION, gradeVersion!!)*/
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
                        for (grade in gradeResponseModelList!!){
                            /*Log.e("gradeModel", grade.btnicon!!.get("mdpi")!!)*/
                            val storageRef = storage
                                .getReferenceFromUrl(grade.btnicon!!.get("mdpi")!!)
                            val root = Environment.getExternalStorageDirectory().toString()
                            val name = "${grade.title}btn.png"
                            val file = File("$root/blobcity/images/$name")
                            Log.e("Temp file : ", file.path)
                            storageRef.getFile(file)
                                .addOnCompleteListener(object : OnCompleteListener<FileDownloadTask.TaskSnapshot>{
                                    override fun onComplete(task: Task<FileDownloadTask.TaskSnapshot>) {
                                        if (task.isSuccessful){
                                            Log.e("after_download", file.path)
                                        }
                                        else{
                                            Log.e("data failure: ", task.exception.toString())
                                        }
                                    }

                                })
                        }
                    }else{
                        Log.e("data failure: ", task.exception.toString())
                    }
                }
            })
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

    override fun click() {
        val intent = Intent(this,
            DashBoardActivity::class.java)
        startActivity(intent)
    }
}
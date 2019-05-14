package com.blobcity.activity

import android.Manifest
import android.content.Intent
import android.util.Log
import android.view.View
import com.blobcity.R
import com.blobcity.adapter.ChaptersAdapter
import com.blobcity.interfaces.TopicClickListener
import com.blobcity.model.*
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.UniqueUUid
import com.blobcity.utils.Utils.loadJSONFromAsset
import com.google.firebase.database.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashBoardActivity : BaseActivity(), PermissionListener,
    View.OnClickListener, TopicClickListener{


    private var branchesItemList:List<BranchesItem>?=null
    var courseId: String?=""
    var courseName: String?=""
    var databaseRefrence: DatabaseReference?= null
    var topicStatusModelList: ArrayList<TopicStatusModel>?=null
    var adapter: ChaptersAdapter?= null

    override fun setLayout(): Int {
        return R.layout.activity_dashboard
    }

    override fun initView() {
        /*databaseRefrence = FirebaseDatabase.getInstance()
            .getReference("topic_status/"+UniqueUUid.id(this))
        databaseRefrence!!.keepSynced(true)*/

        TedPermission.with(this)
            .setPermissionListener(this)
            .setDeniedMessage("If you reject permission,you can not use this service\n"
                    + "\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    override fun onPermissionGranted() {
        /*databaseRefrence!!.addValueEventListener(object : ValueEventListener{
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
        })*/
        readFileLocally()
    }

    override fun onPermissionDenied(deniedPermissions: List<String>) {

    }

    private fun readFileLocally() {
        val courseJsonString = loadJSONFromAsset(this, assetOutputPath+"Courses.json")
        val jsonString = loadJSONFromAsset(this, assetTestCoursePath+"topic.json")
        /*val jsonString = readFromFile("$localTestCoursePath/topic.json")*/
        val gsonFile = Gson()
        val topicResponseModel = gsonFile.fromJson(jsonString, TopicResponseModel::class.java)
        val courseResponseModel = gsonFile.fromJson(courseJsonString, CoursesResponseModel::class.java)
        courseId = courseResponseModel.courses[0].id
        courseName = courseResponseModel.courses[0].syllabus.title
        branchesItemList = topicResponseModel.branches

        /*val branchesItemList2 = ArrayList<BranchesItem>()
        val index1 = branchesItemList!![0].topic.index.toString()
        tv_topic_number1.text = index1
        tv_topic_name1.text = branchesItemList!![0].topic.title

        val index2 = branchesItemList!![1].topic.index.toString()
        tv_topic_number2.text = index2
        tv_topic_name2.text = branchesItemList!![1].topic.title
        branchesItemList!!.forEachIndexed { index, branchesItem ->
            if (index>1){
                branchesItemList2.add(branchesItem)
            }
        }*/

        adapter = ChaptersAdapter(this, branchesItemList!!, this)

        rcv_dashboard.adapter = adapter
        /*rl_chapter_one.setOnClickListener(this)
        rl_chapter_two.setOnClickListener(this)*/
    }

    override fun onClick(v: View?) {
        /*when(v?.id){
            R.id.rl_chapter_one ->{
                callIntent(branchesItemList!!.get(0).topic.folderName, branchesItemList!!.get(0).id)
            }

            R.id.rl_chapter_two ->{
                callIntent(branchesItemList!!.get(1).topic.folderName, branchesItemList!!.get(1).id)
            }
        }*/
    }

    private fun callIntent(topic: Topic, topicId: String){
        val intent = Intent(this, QuizLevelActivity::class.java)
        intent.putExtra(TOPIC, topic)
        intent.putExtra(COURSE_ID, courseId)
        intent.putExtra(COURSE_NAME, courseName)
        intent.putExtra(TOPIC_ID, topicId)
        startActivity(intent)
    }

    override fun onClick(topic: Topic, topicId: String) {
        callIntent(topic, topicId)
    }
}
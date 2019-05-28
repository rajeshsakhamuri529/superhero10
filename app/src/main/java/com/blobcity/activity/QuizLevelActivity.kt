package com.blobcity.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.blobcity.R
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.model.Topic
import com.blobcity.utils.ConstantPath.*
import com.blobcity.viewmodel.TopicStatusVM
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_quiz_level.*

class QuizLevelActivity : BaseActivity(), View.OnClickListener {

    var jsonStringBasic: String? =""
    var jsonStringIntermediate: String? =""
    var jsonStringAdvanced: String? =""
    var courseId: String? =""
    var courseName: String?=""
    var topicId: String? =""
    var topicName: String?=""
    var position: Int ?= null
    var databaseRefrence: DatabaseReference?= null
    var topicStatusModelList: ArrayList<TopicStatusEntity>?=null
    var isBasicCompleted: Boolean = false
    var isIntermediateCompleted: Boolean = false
    var isAdvancedCompleted: Boolean = false
    var context: Context ?= null
    var topicStatusVM: TopicStatusVM?= null

    override fun setLayout(): Int {
        return R.layout.activity_quiz_level
    }

    override fun initView() {
        context = this
        val topic: Topic = intent.getSerializableExtra(TOPIC) as Topic
        val folderName = topic.folderName
        courseId = intent.getStringExtra(COURSE_ID)
        topicId = intent.getStringExtra(TOPIC_ID)
        topicName = topic.title
        position = intent.getIntExtra(TOPIC_POSITION, -1)
        val index = topic.index
        courseName = intent.getStringExtra(COURSE_NAME)
        /*databaseRefrence = FirebaseDatabase.getInstance()
            .getReference("topic_status/"+UniqueUUid.id(this))
        databaseRefrence!!.keepSynced(true)*/

        val title = "$index $topicName"
        tv_title.text = title
        val folderPath = assetTestCoursePath+folderName

        jsonStringBasic = loadJSONFromAsset("$folderPath/basic.json")
        jsonStringIntermediate = loadJSONFromAsset("$folderPath/intermediate.json")
        jsonStringAdvanced = loadJSONFromAsset("$folderPath/advanced.json")

        ivBack.setOnClickListener(this)
        btn_quiz1.setOnClickListener(this)
        btn_quiz2.setOnClickListener(this)
        btn_quiz3.setOnClickListener(this)
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        topicStatusVM!!.getSingleTopicStatus(topicId!!).observe(this,
            object : Observer<List<TopicStatusEntity>>{
                override fun onChanged(t: List<TopicStatusEntity>?) {
                    topicStatusModelList = ArrayList()
                    Glide.with(this@QuizLevelActivity)
                        .load(R.drawable.inactive_button)
                        .into(iv_level1)
                    Glide.with(this@QuizLevelActivity)
                        .load(R.drawable.inactive_button)
                        .into(iv_level2)
                    Glide.with(this@QuizLevelActivity)
                        .load(R.drawable.inactive_button)
                        .into(iv_level3)
                    topicStatusModelList!!.addAll(t!!)
                    if (topicStatusModelList != null) {
                        if (topicStatusModelList!!.size > 0) {
                            val branchId = topicId
                            for (topicStatusModels in topicStatusModelList!!) {
                                val id = topicStatusModels.topicId
                                val level = topicStatusModels.topicLevel

                                if (id!!.contains(branchId!!)) {
                                    if (level!!.contains("basic")) {
                                        Glide.with(this@QuizLevelActivity)
                                            .load(R.drawable.green_tick)
                                            .into(iv_level1)
                                        isBasicCompleted = true
                                    }
                                    if (level.contains("intermediate")) {
                                        Glide.with(this@QuizLevelActivity)
                                            .load(R.drawable.green_tick)
                                            .into(iv_level2)
                                        isIntermediateCompleted = true
                                    }
                                    if (level.contains("advance")) {
                                        Glide.with(this@QuizLevelActivity)
                                            .load(R.drawable.green_tick)
                                            .into(iv_level3)
                                        isAdvancedCompleted = true
                                    }
                                }
                            }
                            if (isBasicCompleted && isIntermediateCompleted){
                                btn_quiz3.setBackgroundResource(R.drawable.button_bg)
                                btn_quiz3.setTextColor(resources.getColor(R.color.white))
                            }
                        }
                    }
                }
            })
        /*databaseRefrence!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (context != null) {
                    topicStatusModelList = ArrayList()
                    Glide.with(this@QuizLevelActivity)
                        .load(R.drawable.progress_icon_grey)
                        .into(iv_level1)
                    Glide.with(this@QuizLevelActivity)
                        .load(R.drawable.progress_icon_grey)
                        .into(iv_level2)
                    Glide.with(this@QuizLevelActivity)
                        .load(R.drawable.progress_icon_grey)
                        .into(iv_level3)
                    for (postSnapshot in dataSnapshot.children) {
                        Log.e("snap", postSnapshot.value.toString())
                        if (postSnapshot != null) {
                            val topicStatusModel: TopicStatusModel =
                                postSnapshot.getValue(TopicStatusModel::class.java)!!
                            topicStatusModelList!!.add(topicStatusModel)
                            if (topicStatusModelList != null) {
                                if (topicStatusModelList!!.size > 0) {
                                    val branchId = topicId
                                    for (topicStatusModels in topicStatusModelList!!) {
                                        val id = topicStatusModels.topicId
                                        val level = topicStatusModels.topicLevel

                                        if (id!!.contains(branchId!!)) {
                                            if (level!!.contains("basic")) {
                                                Glide.with(this@QuizLevelActivity)
                                                    .load(R.drawable.green_tick)
                                                    .into(iv_level1)
                                                isBasicCompleted = true
                                            }
                                            if (level.contains("intermediate")) {
                                                Glide.with(this@QuizLevelActivity)
                                                    .load(R.drawable.green_tick)
                                                    .into(iv_level2)
                                                isIntermediateCompleted = true
                                            }
                                            if (level.contains("advance")) {
                                                Glide.with(this@QuizLevelActivity)
                                                    .load(R.drawable.green_tick)
                                                    .into(iv_level3)
                                                isAdvancedCompleted = true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })*/
    }

    override fun onClick(v: View?) {
        var complete = ""
        when(v?.id){
            R.id.ivBack ->{
                finish()
            }

            R.id.btn_quiz1 ->{
                if (isBasicCompleted){
                    complete = "basic_completed"
                }
                callIntent(jsonStringBasic!!, "basic", complete)
            }

            R.id.btn_quiz2 ->{
                if (isIntermediateCompleted){
                    complete = "Intermediate_completed"
                }
                callIntent(jsonStringIntermediate!!, "intermediate", complete)
            }

            R.id.btn_quiz3 ->{
                if (isAdvancedCompleted){
                    complete = "Advanced_completed"
                }
                if (!isBasicCompleted){
                    Toast.makeText(this, "Please Complete Quiz1.", Toast.LENGTH_SHORT).show()
                    return
                }
                if (!isIntermediateCompleted){
                    Toast.makeText(this, "Please Complete Quiz2.", Toast.LENGTH_SHORT).show()
                    return
                }
                callIntent(jsonStringAdvanced!!, "advanced", complete)
            }
        }
    }

    private fun callIntent(path: String, level: String, complete: String){
        val intent = Intent(this, StartQuizActivity::class.java)
        intent.putExtra(DYNAMIC_PATH, path)
        intent.putExtra(COURSE_ID, courseId)
        intent.putExtra(TOPIC_ID, topicId)
        intent.putExtra(COURSE_NAME, courseName)
        intent.putExtra(TOPIC_NAME, topicName)
        intent.putExtra(TOPIC_LEVEL, level)
        intent.putExtra(LEVEL_COMPLETED, complete)
        intent.putExtra(TOPIC_POSITION, position)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (context != null){
            context = null
        }
    }
}
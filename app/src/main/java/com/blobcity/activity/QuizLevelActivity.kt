package com.blobcity.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.blobcity.R
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.model.BranchesItem
import com.blobcity.model.CoursesResponseModel
import com.blobcity.model.Topic
import com.blobcity.model.TopicResponseModel
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils.readFromFile
import com.blobcity.viewmodel.TopicStatusVM
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    var topicStatusModelList: ArrayList<TopicStatusEntity>?=null
    var isBasicCompleted: Boolean = false
    var isIntermediateCompleted: Boolean = false
    var isAdvancedCompleted: Boolean = false
    var context: Context ?= null
    var topicStatusVM: TopicStatusVM?= null
    var paths: String?= null
    var folderName: String?=null
    var gradeTitle: String?= null
    private var branchesItemList:List<BranchesItem>?=null

    override var layoutID: Int = R.layout.activity_quiz_level

    override fun initView() {
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)

        val topic: Topic = intent.getSerializableExtra(TOPIC) as Topic
        folderName = topic.folderName
        courseId = intent.getStringExtra(COURSE_ID)
        topicId = intent.getStringExtra(TOPIC_ID)
        topicName = topic.title
        position = intent.getIntExtra(TOPIC_POSITION, -1)
        val index = topic.index

        courseName = intent.getStringExtra(COURSE_NAME)
        gradeTitle = intent.getStringExtra(TITLE_TOPIC)

        if(index<10)
        {
            val title = "0$index $topicName"
            tv_title.text = title
            Log.d("quizLevel",title+"!")

        }else{
            val title = "$index $topicName"
            tv_title.text = title
        }


        paths = intent.getStringExtra(FOLDER_PATH)
        val folderPath = paths+folderName

        jsonStringBasic = loadJSONFromAsset("$folderPath/basic.json")
        Log.d("jsonStringBasic",jsonStringBasic);
        jsonStringIntermediate = loadJSONFromAsset("$folderPath/intermediate.json")
        Log.d("jsonStringIntermediate",jsonStringIntermediate);
        jsonStringAdvanced = loadJSONFromAsset("$folderPath/advanced.json")
        Log.d("jsonStringIntermediate",jsonStringIntermediate);

        loadDataFromDb()

        //ivBack.setOnClickListener(this)
        btn_quiz1.setOnClickListener(this)
        btn_quiz2.setOnClickListener(this)
        btn_quiz3.setOnClickListener(this)
        //tvBack.setOnClickListener(this)
        llBack.setOnClickListener(this)
        Log.d("position",position.toString()+"!")
    }

    private fun loadDataFromDb() {
        topicStatusVM!!.getSingleTopicStatus(topicId!!, gradeTitle!!).observe(this,
            object : Observer<List<TopicStatusEntity>>{
                override fun onChanged(t: List<TopicStatusEntity>?) {
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
                    topicStatusModelList!!.addAll(t!!)
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
                            btn_quiz3.setBackgroundResource(R.drawable.quiz_level_bg)
                            btn_quiz3.setTextColor(resources.getColor(R.color.white))
                        }
                    }else {
                        isBasicCompleted = false
                        isIntermediateCompleted = false
                        isAdvancedCompleted = false
                        if (!isBasicCompleted && !isIntermediateCompleted) {
                            btn_quiz3.setBackgroundResource(R.drawable.super_quiz_inactive)
                            btn_quiz3.setTextColor(resources.getColor(R.color.white))
                        }
                    }
                }
            })
    }

    override fun onNewIntent(intent: Intent?) {
        /*super.onNewIntent(intent)*/
        Log.e("asf", "onNewIntent")

        position = intent!!.getIntExtra(TOPIC_POSITION, -1)
        courseId = intent.getStringExtra(COURSE_ID)
        courseName = intent.getStringExtra(COURSE_NAME)
        gradeTitle = intent.getStringExtra(TITLE_TOPIC)

        val courseJsonString = loadJSONFromAsset("${localBlobcityPath}Courses.json")
        /*val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( assetTestCoursePath + "topic.json")*/
        val gsonFile = Gson()
        Log.d("newIntent",courseJsonString+"!")
        val courseType = object : TypeToken<List<CoursesResponseModel>>() {}.type
        val courseResponseModel: ArrayList<CoursesResponseModel> = gsonFile
            .fromJson(courseJsonString, courseType)
        courseId = courseResponseModel[0].id
        courseName = courseResponseModel[0].syllabus.title
        //localPath = "$localBlobcityPath$courseName/"
        val jsonString = loadJSONFromAsset("${localBlobcityPath}$courseName/topic.json")

        val topicType = object : TypeToken<TopicResponseModel>() {}.type
        val topicResponseModel: TopicResponseModel = gsonFile.fromJson(jsonString, topicType )

        branchesItemList = topicResponseModel.branches

        val topic: Topic = branchesItemList!!.get(position!!).topic
        folderName = topic.folderName
        topicId=branchesItemList!!.get(position!!).id
        topicName = topic.title
        val index = topic.index
        val title = "$index $topicName"
        tv_title.text = title

        paths = intent.getStringExtra(FOLDER_PATH)
        val folderPath = paths+folderName

        jsonStringBasic = loadJSONFromAsset("$folderPath/basic.json")
        jsonStringIntermediate = loadJSONFromAsset("$folderPath/intermediate.json")
        jsonStringAdvanced = loadJSONFromAsset("$folderPath/advanced.json")

        loadDataFromDb()
    }

    override fun onClick(v: View?) {
        var complete = ""
        when(v?.id){
           /* R.id.ivBack ->{
                finish()
            }

            R.id.tvBack ->{
                finish()
            }*/

            R.id.llBack ->{
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

                if (!isBasicCompleted){
                    //Toast.makeText(this, "Please Complete Quiz1.", Toast.LENGTH_SHORT).show()
                    lastTopicDialog()
                    return
                }
                if (!isIntermediateCompleted){
                    //Toast.makeText(this, "Please Complete Quiz2.", Toast.LENGTH_SHORT).show()
                    lastTopicDialog()
                    return
                }
                if (isAdvancedCompleted){
                    complete = "Advanced_completed"
                }
                callIntent(jsonStringAdvanced!!, "advanced", complete)
            }
        }
    }

    private fun lastTopicDialog() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.mytheme)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.last_topic_dialog_layout, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        val tv_ok = dialogView.findViewById(R.id._last_topic_btn_ok) as Button
        val tv_msg1 = dialogView.findViewById(R.id._last_topic_tv_msg1) as TextView
        val tv_msg2 = dialogView.findViewById(R.id._last_topic_tv_msg2) as TextView

        tv_msg1.text = "Complete Quiz I and Quiz II"
        tv_msg2.text = "successfully to unlock Super Quiz"
        val alertDialog = dialogBuilder.create()
        //val tv_return = dialogView.findViewById(R.id.tv_return) as TextView
        tv_ok.setOnClickListener {

            alertDialog.dismiss()
        }

        //alertDialog.getWindow().setBackgroundDrawable(draw);
        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()
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
        intent.putExtra(FOLDER_PATH, paths)
        intent.putExtra(FOLDER_NAME, folderName)
        intent.putExtra(TITLE_TOPIC, gradeTitle!!)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (context != null){
            context = null
        }
    }
}
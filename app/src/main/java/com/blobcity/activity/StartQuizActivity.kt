package com.blobcity.activity

import android.content.Intent
import android.view.View
import com.blobcity.R
import com.blobcity.model.TopicOneBasicResponseModel
import com.blobcity.utils.ConstantPath
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_start_quiz.*

class StartQuizActivity : BaseActivity() {

    override fun setLayout(): Int {
        return R.layout.activity_start_quiz
    }

    override fun initView() {
        val path = intent.getStringExtra(ConstantPath.DYNAMIC_PATH)
        val courseId = intent.getStringExtra(ConstantPath.COURSE_ID)
        val topicId = intent.getStringExtra(ConstantPath.TOPIC_ID)
        val courseName = intent.getStringExtra(ConstantPath.COURSE_NAME)
        val topicLevel = intent.getStringExtra(ConstantPath.TOPIC_LEVEL)
        val topicName = intent.getStringExtra(ConstantPath.TOPIC_NAME)
        val complete = intent.getStringExtra(ConstantPath.LEVEL_COMPLETED)
        var level = ""

        val gsonFile = Gson()
        val questionResponseModel = gsonFile.fromJson(path, TopicOneBasicResponseModel::class.java)

        if (topicLevel.equals("basic")){
            level = "Quiz I"
        }

        if (topicLevel.equals("intermediate")){
            level = "Quiz II"
        }

        if (topicLevel.equals("advanced")){
            level = "Astra Quiz"
        }

        tv_quiz_count.text = questionResponseModel.questionCount.toString()
        tv_chapter_title.text = topicName
        tv_quiz_level.text = level

        if (questionResponseModel.questionCount <= 4){
            iv_life3.visibility = View.GONE
        }

        btn_start.setOnClickListener {
            val intent = Intent(this, TestQuestionActivity::class.java)
            intent.putExtra(ConstantPath.DYNAMIC_PATH, path)
            intent.putExtra(ConstantPath.COURSE_ID, courseId)
            intent.putExtra(ConstantPath.TOPIC_ID, topicId)
            intent.putExtra(ConstantPath.COURSE_NAME, courseName)
            intent.putExtra(ConstantPath.TOPIC_NAME, topicName)
            intent.putExtra(ConstantPath.TOPIC_LEVEL, topicLevel)
            intent.putExtra(ConstantPath.LEVEL_COMPLETED, complete)
            startActivity(intent)
            finish()
        }
    }
}
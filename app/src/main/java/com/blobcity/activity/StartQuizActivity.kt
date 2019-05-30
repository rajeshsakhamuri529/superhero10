package com.blobcity.activity

import android.content.Intent
import android.util.Log
import android.view.View
import com.blobcity.R
import com.blobcity.model.TopicOneBasicResponseModel
import com.blobcity.utils.ConstantPath.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_start_quiz.*

class StartQuizActivity : BaseActivity(),View.OnClickListener {

    override fun setLayout(): Int {
        return R.layout.activity_start_quiz
    }

    override fun initView() {
        val path = intent.getStringExtra(DYNAMIC_PATH)
        val courseId = intent.getStringExtra(COURSE_ID)
        val topicId = intent.getStringExtra(TOPIC_ID)
        val courseName = intent.getStringExtra(COURSE_NAME)
        val topicLevel = intent.getStringExtra(TOPIC_LEVEL)
        val topicName = intent.getStringExtra(TOPIC_NAME)
        val complete = intent.getStringExtra(LEVEL_COMPLETED)
        val position: Int = intent.getIntExtra(TOPIC_POSITION, -1)
        val paths: String = intent.getStringExtra(FOLDER_PATH)
        val folderName: String = intent.getStringExtra(FOLDER_NAME)
        var level = ""

        Log.e("path", path)
        val gsonFile = Gson()
        val questionResponseModel = gsonFile.fromJson(path, TopicOneBasicResponseModel::class.java)

        if (topicLevel.equals("basic")){
            level = "Quiz I"
        }

        if (topicLevel.equals("intermediate")){
            level = "Quiz II"
        }

        if (topicLevel.equals("advanced")){
            iv_lock_card.visibility = View.VISIBLE
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
            intent.putExtra(DYNAMIC_PATH, path)
            intent.putExtra(COURSE_ID, courseId)
            intent.putExtra(TOPIC_ID, topicId)
            intent.putExtra(COURSE_NAME, courseName)
            intent.putExtra(TOPIC_NAME, topicName)
            intent.putExtra(TOPIC_LEVEL, topicLevel)
            intent.putExtra(LEVEL_COMPLETED, complete)
            intent.putExtra(TOPIC_POSITION, position)
            intent.putExtra(FOLDER_PATH, paths)
            intent.putExtra(FOLDER_NAME, folderName)
            startActivity(intent)
            finish()
        }
        iv_cancel.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.iv_cancel -> {
                Log.d("startQuiz","1")
                finish()
            }
        }
    }
}
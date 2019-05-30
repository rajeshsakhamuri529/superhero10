package com.blobcity.activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.blobcity.R
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.model.ReviewModel
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils
import com.blobcity.utils.Utils.readFromFile
import com.blobcity.viewmodel.TopicStatusVM
import kotlinx.android.synthetic.main.activity_quiz_summary.*

class QuizSummaryActivity : BaseActivity() {

    var reviewModelList: ArrayList<ReviewModel>?= null
    var topicLevel: String? = ""
    var topicName: String?=""
    var topicId: String? =""
    private var totalQuestion: Int?=null
    var topicStatusVM: TopicStatusVM?= null
    var topicStatusModelList: ArrayList<TopicStatusEntity>?=null
    var isBasicCompleted: Boolean = false
    var isIntermediateCompleted: Boolean = false
    var isAdvancedCompleted: Boolean = false
    private var mSetRightOut: AnimatorSet? = null
    private var mSetLeftIn: AnimatorSet? = null
    private var zoomOutAnimation: Animation?= null
    private var zoominAnimation: Animation?= null
    val handler = Handler()
    var complete: String?= null
    var position: Int?= null
    var dPath: String?= null
    var paths: String?= null
    var courseId: String?= null
    var courseName: String?= null
    var folderName : String?= null
    var level_status: Boolean?= null

    override fun setLayout(): Int {
        return R.layout.activity_quiz_summary
    }

    override fun initView() {
        reviewModelList = intent.getSerializableExtra(REVIEW_MODEL) as ArrayList<ReviewModel>?
        topicLevel = intent.getStringExtra(TOPIC_LEVEL)
        topicName = intent.getStringExtra(TOPIC_NAME)
        topicId = intent.getStringExtra(TOPIC_ID)
        totalQuestion = intent.getIntExtra(QUIZ_COUNT, 0)
        complete = intent.getStringExtra(LEVEL_COMPLETED)
        position = intent.getIntExtra(TOPIC_POSITION, -1)
        dPath = intent.getStringExtra(DYNAMIC_PATH)
        paths = intent.getStringExtra(FOLDER_PATH)
        courseId = intent.getStringExtra(COURSE_ID)
        courseName = intent.getStringExtra(COURSE_NAME)
        folderName = intent.getStringExtra(FOLDER_NAME)
        level_status = intent.getBooleanExtra(IS_LEVEL_COMPLETE, false)
        tv_chapter_title.text = topicName
        changeCameraDistance()
        loadAnimations()
        if (topicLevel!!.contains("basic")) {
            tv_quiz_level.text = "Quiz I"
        }
        if (topicLevel!!.contains("intermediate")) {
            tv_quiz_level.text = "Quiz II"
        }
        if (topicLevel!!.contains("advanced")) {
            iv_card_back.visibility = View.VISIBLE
            iv_card_front.visibility = View.VISIBLE
            tv_quiz_level.text = "Astra Quiz"
        }
        val size = reviewModelList!!.size
        val answer_status = "$size / $totalQuestion"
        tv_answer_status.text = answer_status
        if (level_status!!){
            /*if (TextUtils.isEmpty(complete)) {
                if (topicLevel!!.contains("basic")) {
                    complete = "basic_completed"
                }
                if (topicLevel!!.contains("intermediate")) {
                    complete = "Intermediate_completed"
                }
                if (topicLevel!!.contains("advanced")) {
                    complete = "Advanced_completed"
                }
            }*/
            tv_completion_status.text = "Level Completed"
            tv_completion_status.setTextColor(resources.getColor(R.color.green_right_answer))
        }else{
            tv_completion_status.text = "Level Failed"
            tv_completion_status.setTextColor(resources.getColor(R.color.orange_level_failed))
        }
        btn_review.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra(ConstantPath.REVIEW_MODEL, reviewModelList)
            startActivity(intent)
        }

        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        topicStatusVM!!.getSingleTopicStatus(topicId!!).observe(this,
            object : Observer<List<TopicStatusEntity>> {
                override fun onChanged(t: List<TopicStatusEntity>?) {
                    topicStatusModelList = ArrayList()
                    topicStatusModelList!!.addAll(t!!)
                    if (topicStatusModelList != null) {
                        if (topicStatusModelList!!.size > 0) {
                            val branchId = topicId
                            for (topicStatusModels in topicStatusModelList!!) {
                                val id = topicStatusModels.topicId
                                val level = topicStatusModels.topicLevel

                                if (id!!.contains(branchId!!)) {
                                    if (level!!.contains("basic")) {
                                        isBasicCompleted = true
                                    }
                                    if (level.contains("intermediate")) {
                                        isIntermediateCompleted = true
                                    }
                                    if (level.contains("advance")) {
                                        isAdvancedCompleted = true
                                        if (topicLevel!!.contains("advanced")) {
                                            handler.postDelayed(object : Runnable {
                                                override fun run() {
                                                    iv_card_front.startAnimation(zoomOutAnimation)
                                                    mSetRightOut!!.setTarget(iv_card_front)
                                                    mSetLeftIn!!.setTarget(iv_card_back)
                                                    iv_card_back.startAnimation(zoominAnimation)
                                                    mSetRightOut!!.start()
                                                    mSetLeftIn!!.start()
                                                }
                                            }, 1500)
                                        }
                                    }
                                }
                            }

                            if (!isBasicCompleted && isIntermediateCompleted){
                                btn_next_quiz.text = "Quiz I"
                                return
                            }

                            if (isBasicCompleted && !isIntermediateCompleted){
                                btn_next_quiz.text = "Quiz II"
                                return
                            }

                            if (isBasicCompleted && isIntermediateCompleted && !isAdvancedCompleted){
                                btn_next_quiz.text = "Astra Quiz"
                                return
                            }

                            if (isBasicCompleted && isIntermediateCompleted && isAdvancedCompleted){
                                btn_next_quiz.visibility = View.INVISIBLE
                            }
                        }
                        if (!isBasicCompleted && !isIntermediateCompleted){
                            if (topicLevel!!.contains("basic")) {
                                btn_next_quiz.text = "Quiz II"
                                return
                            }
                            if (topicLevel!!.contains("intermediate")) {
                                btn_next_quiz.text = "Quiz I"
                                return
                            }
                        }
                    }
                }
            })

        iv_cancel_quiz_summary.setOnClickListener {
            onBackPressed()
        }

        btn_play_again.setOnClickListener {
            val intent = Intent(this, StartQuizActivity::class.java)
            intent.putExtra(DYNAMIC_PATH, dPath)
            intent.putExtra(COURSE_ID, courseId)
            intent.putExtra(TOPIC_ID, topicId)
            intent.putExtra(COURSE_NAME, courseName)
            intent.putExtra(TOPIC_NAME, topicName)
            intent.putExtra(TOPIC_LEVEL, topicLevel)
            intent.putExtra(LEVEL_COMPLETED, complete)
            intent.putExtra(TOPIC_POSITION, position!!)
            intent.putExtra(FOLDER_PATH, paths)
            intent.putExtra(FOLDER_NAME, folderName)
            startActivity(intent)
            finish()
        }

        btn_next_quiz.setOnClickListener {
            topicLevel = btn_next_quiz.text.toString()

            val folderPath = paths+folderName

            if (level_status!!){
                complete = ""
            }

            if (topicLevel!!.equals("Quiz I")){
                topicLevel = "basic"
                dPath = readFromFile("$folderPath/basic.json")
            }

            if (topicLevel!!.equals("Quiz II")){
                topicLevel = "intermediate"
                dPath = readFromFile("$folderPath/intermediate.json")
            }

            if (topicLevel!!.equals("Astra Quiz")){
                topicLevel = "advanced"
                dPath = readFromFile("$folderPath/advanced.json")
            }
            val intent = Intent(this, StartQuizActivity::class.java)
            intent.putExtra(DYNAMIC_PATH, dPath)
            intent.putExtra(COURSE_ID, courseId)
            intent.putExtra(TOPIC_ID, topicId)
            intent.putExtra(COURSE_NAME, courseName)
            intent.putExtra(TOPIC_NAME, topicName)
            intent.putExtra(TOPIC_LEVEL, topicLevel)
            intent.putExtra(LEVEL_COMPLETED, complete)
            intent.putExtra(TOPIC_POSITION, position!!)
            intent.putExtra(FOLDER_NAME, folderName)
            intent.putExtra(FOLDER_PATH, paths)
            startActivity(intent)
            finish()
        }

    }

    private fun changeCameraDistance() {
        val distance = 7000
        val scale = resources.displayMetrics.density * distance
        iv_card_front!!.setCameraDistance(scale)
        iv_card_back!!.setCameraDistance(scale)
    }

    @SuppressLint("ResourceType")
    private fun loadAnimations() {
        zoominAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        zoomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        mSetRightOut = AnimatorInflater.loadAnimator(this, R.anim.out_animation) as AnimatorSet
        mSetLeftIn = AnimatorInflater.loadAnimator(this, R.anim.in_animation) as AnimatorSet
    }
}
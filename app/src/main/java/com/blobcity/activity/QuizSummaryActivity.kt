package com.blobcity.activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.blobcity.R
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.model.ReviewModel
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils.*
import com.blobcity.viewmodel.TopicStatusVM
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_quiz_summary.*

class QuizSummaryActivity : BaseActivity(), View.OnClickListener {

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
    var gradeTitle: String?= null

    override var layoutID: Int = R.layout.activity_quiz_summary

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
        gradeTitle = intent.getStringExtra(TITLE_TOPIC)
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
            tv_completion_status.text = "Level Completed"
            tv_completion_status.setTextColor(resources.getColor(R.color.green_right_answer))
        }else{
            tv_completion_status.text = "Level Failed"
            tv_completion_status.setTextColor(resources.getColor(R.color.orange_level_failed))
        }

        btn_review.setOnClickListener(this)
        iv_cancel_quiz_summary.setOnClickListener(this)
        btn_play_again.setOnClickListener (this)
        btn_next_quiz.setOnClickListener(this)
        btn_next_level.setOnClickListener(this)

        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        loadDataFromDb()

    }

    override fun onClick(v: View?) {
        val intent: Intent
        when (v!!.id){

            R.id.btn_next_level->{
                intent = Intent(this, QuizLevelActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                intent.putExtra(TOPIC_POSITION, (position!!+1))
                intent.putExtra(COURSE_ID, courseId)
                intent.putExtra(COURSE_NAME, courseName)
                intent.putExtra(FOLDER_PATH, paths)
                intent.putExtra(TITLE_TOPIC, gradeTitle!!)
                finish()
                startActivity(intent)
            }

            R.id.iv_cancel_quiz_summary ->{
                finish()
            }

            R.id.btn_review ->{
                intent = Intent(this, ReviewActivity::class.java)
                intent.putExtra(ConstantPath.REVIEW_MODEL, reviewModelList)
                startActivity(intent)
            }

            R.id.btn_next_quiz ->{
                topicLevel = btn_next_quiz.text.toString()
                val folderPath = paths+folderName

                if (level_status!!){
                    complete = ""
                }

                if (topicLevel!!.equals("Quiz I")){
                    topicLevel = "basic"
                    dPath = loadJSONFromAsset("$folderPath/basic.json")
                }
                if (topicLevel!!.equals("Quiz II")){
                    topicLevel = "intermediate"
                    dPath = loadJSONFromAsset("$folderPath/intermediate.json")
                }
                if (topicLevel!!.equals("Astra Quiz")){
                    topicLevel = "advanced"
                    dPath = loadJSONFromAsset("$folderPath/advanced.json")
                }
                navigateToStartQuiz()
            }

            R.id.btn_play_again ->{
                navigateToStartQuiz()
            }
        }
    }

    private fun navigateToStartQuiz(){
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
        intent.putExtra(TITLE_TOPIC, gradeTitle!!)
        startActivity(intent)
        finish()
    }

    private fun loadDataFromDb(){
        topicStatusVM!!.getSingleTopicStatus(topicId!!, gradeTitle!!).observe(this,
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
                                        val pathStringList: ArrayList<String> = ArrayList()
                                        for (imagePath in listAssetFiles(loaclAstraCardPath,applicationContext)){
                                            if (imagePath.contains("png")){
                                                pathStringList.add(imagePath)
                                            }
                                        }
                                        /*Collections.sort(pathStringList)*/
                                        var imagepath = ""
                                        for (path in pathStringList){
                                            if (position!! == 0) {
                                                if (path.equals("1.png")) {
                                                    imagepath = loaclAstraCardPath+path
                                                }
                                            }
                                            if (position!! == 1) {
                                                if (path.equals("2.png")) {
                                                    imagepath = loaclAstraCardPath+path
                                                }
                                            }
                                            if (position!! == 2) {
                                                if (path.equals("3.png")) {
                                                    imagepath = loaclAstraCardPath+path
                                                }
                                            }
                                            if (position!! == 3) {
                                                if (path.equals("4.png")) {
                                                    imagepath = loaclAstraCardPath+path
                                                }
                                            }
                                            if (position!! == 4) {
                                                if (path.equals("5.png")) {
                                                    imagepath = loaclAstraCardPath+path
                                                }
                                            }
                                            if (position!! == 5) {
                                                if (path.equals("6.png")) {
                                                    imagepath = loaclAstraCardPath+path
                                                }
                                            }
                                            if (position!! == 6) {
                                                if (path.equals("7.png")) {
                                                    imagepath = loaclAstraCardPath+path
                                                }
                                            }
                                            if (position!! == 7) {
                                                if (path.equals("8.png")) {
                                                    imagepath = loaclAstraCardPath+path
                                                }
                                            }
                                            if (position!! == 8) {
                                                if (path.equals("9.png")) {
                                                    imagepath = loaclAstraCardPath+path
                                                }
                                            }
                                            if (position!! == 9) {
                                                if (path.equals("10.png")) {
                                                    imagepath = loaclAstraCardPath+path
                                                }
                                            }
                                        }
                                        Glide.with(this@QuizSummaryActivity)
                                            .load(imagepath)
                                            .into(iv_card_back)
                                        Log.e("position: ", position.toString())
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
                                btn_next_quiz.visibility = View.GONE
                                btn_next_level.visibility = View.VISIBLE
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
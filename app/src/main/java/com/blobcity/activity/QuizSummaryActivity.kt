package com.blobcity.activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.blobcity.R
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.model.ReviewModel
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.ConstantPath.*
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

    override fun setLayout(): Int {
        return R.layout.activity_quiz_summary
    }

    override fun initView() {
        reviewModelList = intent.getSerializableExtra(REVIEW_MODEL) as ArrayList<ReviewModel>?
        topicLevel = intent.getStringExtra(TOPIC_LEVEL)
        topicName = intent.getStringExtra(TOPIC_NAME)
        topicId = intent.getStringExtra(TOPIC_ID)
        totalQuestion = intent.getIntExtra(QUIZ_COUNT, 0)
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
        if (size == totalQuestion){
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
                                        handler.postDelayed(object : Runnable{
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

                            if (!isBasicCompleted && isIntermediateCompleted){
                                btn_quiz.text = "Quiz I"
                                return
                            }

                            if (isBasicCompleted && !isIntermediateCompleted){
                                btn_quiz.text = "Quiz II"
                                return
                            }

                            if (isBasicCompleted && isIntermediateCompleted && !isAdvancedCompleted){
                                btn_quiz.text = "Astra Quiz"
                                return
                            }

                            if (isBasicCompleted && isIntermediateCompleted && isAdvancedCompleted){
                                btn_quiz.visibility = View.INVISIBLE
                            }
                        }
                    }
                }
            })

        iv_cancel_quiz_summary.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })

    }

    private fun changeCameraDistance() {
        val distance = 6000
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
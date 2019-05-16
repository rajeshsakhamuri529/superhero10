package com.blobcity.activity

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.view.View
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
        if (topicLevel!!.contains("basic")) {
            tv_quiz_level.text = "Quiz I"
        }
        if (topicLevel!!.contains("intermediate")) {
            tv_quiz_level.text = "Quiz II"
        }
        if (topicLevel!!.contains("advanced")) {
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
    }
}
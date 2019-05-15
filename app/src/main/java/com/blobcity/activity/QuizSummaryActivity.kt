package com.blobcity.activity

import android.content.Intent
import com.blobcity.R
import com.blobcity.model.ReviewModel
import com.blobcity.utils.ConstantPath
import kotlinx.android.synthetic.main.activity_quiz_summary.*

class QuizSummaryActivity : BaseActivity() {

    var reviewModelList: ArrayList<ReviewModel>?= null

    override fun setLayout(): Int {
        return R.layout.activity_quiz_summary
    }

    override fun initView() {
        reviewModelList = intent.getSerializableExtra(ConstantPath.REVIEW_MODEL) as ArrayList<ReviewModel>?
        btn_review.setOnClickListener {
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtra(ConstantPath.REVIEW_MODEL, reviewModelList)
            startActivity(intent)
        }
    }
}
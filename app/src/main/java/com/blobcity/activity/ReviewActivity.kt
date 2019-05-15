package com.blobcity.activity

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.blobcity.adapter.ReviewAdapter
import com.blobcity.model.ReviewModel
import com.blobcity.utils.ConstantPath.REVIEW_MODEL
import kotlinx.android.synthetic.main.activity_review.*


class ReviewActivity : BaseActivity() {
    var res: String = ""
    var item: Int ?= null
    var review_position: String = ""
    var reviewModelList: ArrayList<ReviewModel>?= null

    override fun setLayout(): Int {
        return com.blobcity.R.layout.activity_review
    }

    override fun initView() {
        reviewModelList = intent.getSerializableExtra(REVIEW_MODEL) as ArrayList<ReviewModel>?
        val mLayoutManager = LinearLayoutManager(applicationContext,
            LinearLayoutManager.HORIZONTAL, false)
        rcv_review.setLayoutManager(mLayoutManager)
        val adapter = ReviewAdapter(reviewModelList!!, this)
        rcv_review.adapter = adapter
        val startSnapHelper = PagerSnapHelper()
        startSnapHelper.attachToRecyclerView(rcv_review)
        val listSize = reviewModelList!!.size
        res = "$item of $listSize"
        //  res = String.valueOf(firstVisibleItem);
        tv_count.text = res
        rcv_review.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    Log.d("TAGA", "onScrollStateChanged if")
                    //Dragging
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    review_position = mLayoutManager.findFirstCompletelyVisibleItemPosition().toString()
                    // Log.d("TAGA", "onScrollStateChanged elseif");

                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val firstVisibleItem = mLayoutManager.findLastVisibleItemPosition()
                item = firstVisibleItem
                val listSize = reviewModelList!!.size
                res = "$firstVisibleItem of $listSize"
                //  res = String.valueOf(firstVisibleItem);
                tv_count.text = res
                Log.d("TAGA", firstVisibleItem.toString())
            }
        })
    }
}
package com.blobcity.activity

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import com.blobcity.R
import com.blobcity.adapter.ReviewAdapter
import com.blobcity.model.ReviewModel
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.RecyclerViewPositionHelper
import com.blobcity.utils.Utils.getListOfFilesFromFolder
import com.blobcity.utils.Utils.listAssetFiles
import kotlinx.android.synthetic.main.activity_quiz_summary.*
import kotlinx.android.synthetic.main.activity_review.*


class ReviewActivity : BaseActivity() {
    var res: String = ""
    var item: Int ?= null
    var review_position: String = ""
    var reviewModelList: ArrayList<ReviewModel>?= null
    var mRecyclerViewHelper: RecyclerViewPositionHelper?= null
    var hintPath = ""
    var context: Context ? = null
    var adapter : ReviewAdapter? = null
    var topicLevel: String? = ""

    override var layoutID: Int = R.layout.activity_review

    override fun initView() {
        context = this
        reviewModelList = intent.getSerializableExtra(REVIEW_MODEL) as ArrayList<ReviewModel>?
        val mLayoutManager = LinearLayoutManager(applicationContext,
            LinearLayoutManager.HORIZONTAL, false)
        rcv_review.setLayoutManager(mLayoutManager)
        adapter = ReviewAdapter(reviewModelList!!, this)
        rcv_review.adapter = adapter
        val startSnapHelper = PagerSnapHelper()
        startSnapHelper.attachToRecyclerView(rcv_review)
        val listSize = reviewModelList!!.size
        /*res = "$item of $listSize"
        //  res = String.valueOf(firstVisibleItem);
        tv_count.text = res*/
        topicLevel = intent.getStringExtra(TOPIC_LEVEL)
        if (topicLevel!!.contains("basic")) {
            tv_quiz_review_text.text = "Quiz I"
        }
        if (topicLevel!!.contains("intermediate")) {
            tv_quiz_review_text.text = "Quiz II"
        }
        if (topicLevel!!.contains("advanced")) {
            tv_quiz_review_text.text = "Super Quiz"
        }
        review_tv_count1.text = "$item"
        review_tv_count2.text = "$listSize"

        btn_hint.setOnClickListener { hintAlertDialog() }
        btn_close_review.setOnClickListener { onBackPressed() }
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
                mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView)
                val totalItemCount = mRecyclerViewHelper!!.getItemCount()
                val count = totalItemCount - 1

                if (firstVisibleItem == count) {
                    item = mRecyclerViewHelper!!.findLastCompletelyVisibleItemPosition() + 1
                    res = item.toString() + " of " + totalItemCount
                    if (item != 0) {
                        for (filename in listAssetFiles(localBlobcityPath +
                                reviewModelList!!.get(item!! - 1).questionsItem!!.id,applicationContext)!!) {
                            if (filename.contains("hint")) {
                                hintPath = ConstantPath.WEBVIEW_PATH + assetOutputPath +
                                        reviewModelList!!.get(item!! - 1).questionsItem!!.id + "/" + filename
                            }
                        }
                        /*tv_count.setText(res)*/
                        review_tv_count1.text = item.toString()
                        review_tv_count2.text = totalItemCount.toString()

                    }
                } else {
                    item = firstVisibleItem
                    res = item.toString() + " of " + totalItemCount
                    for (filename in listAssetFiles(
                        localBlobcityPath +
                                reviewModelList!!.get(item!! - 1).questionsItem!!.id,applicationContext)!!) {
                        if (filename.contains("hint")) {
                            hintPath = ConstantPath.WEBVIEW_PATH + assetOutputPath +
                                    reviewModelList!!.get(item!! - 1).questionsItem!!.id + "/" + filename
                        }
                    }
                    /*tv_count.setText(res)*/
                    review_tv_count1.text = item.toString()
                    review_tv_count2.text = totalItemCount.toString()
                }
                Log.d("TAGA", firstVisibleItem.toString())
            }
        })
    }

    private fun hintAlertDialog(){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.hint_dialog_layout, null)
        dialogBuilder.setView(dialogView)

        val webview = dialogView.findViewById(R.id.webview_hint) as WebView
        val btn_gotIt = dialogView.findViewById(R.id.btn_gotIt) as Button

        webview.settings.javaScriptEnabled = true
        val hint = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("onPageFinished", url + "!")
                adapter!!.injectCSS(view, "Hint")
                // view!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#ffffff}</style>';")
            }
        }
        webview.webViewClient = hint
        webview.loadUrl(hintPath)
        webview.setBackgroundColor(0)


        val alertDialog = dialogBuilder.create()
        btn_gotIt.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()
    }
}
package com.blobcity.adapter

import android.app.Activity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.RelativeLayout
import com.blobcity.R
import com.blobcity.activity.ReviewActivity
import com.blobcity.model.ReviewModel
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils

class ReviewAdapter(val reviewModelList: ArrayList<ReviewModel>,
                    val context: Activity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listOfOptions : ArrayList<String>?=null
    private var opt1Path: String?=""
    private var opt2Path: String?=""
    private var opt3Path: String?=""
    private var opt4Path: String?=""
    private var path: String?=""
    private var questionPath: String?=""

    override fun onCreateViewHolder(p0: ViewGroup, type: Int): RecyclerView.ViewHolder {
        val view: View?
        var viewHolder: RecyclerView.ViewHolder?=null
        when(type){
            type2100 ->{
                view = LayoutInflater.from(context)
                    .inflate(com.blobcity.R.layout.review_2100_layout, p0, false)
                viewHolder = Layout2100ViewHolder(view)
                return viewHolder
            }

            type4100 ->{
                view = LayoutInflater.from(context)
                    .inflate(com.blobcity.R.layout.review_4100_layout, p0, false)
                viewHolder = Layout4100ViewHolder(view)
                return viewHolder
            }

            type2210 ->{
                view = LayoutInflater.from(context)
                    .inflate(com.blobcity.R.layout.review_2210_layout, p0, false)
                viewHolder = Layout2210ViewHolder(view)
                return viewHolder
            }

            type2201 ->{
                view = LayoutInflater.from(context)
                    .inflate(com.blobcity.R.layout.review_2201_layout, p0, false)
                viewHolder = Layout2201ViewHolder(view)
                return viewHolder
            }
        }
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return reviewModelList.size
    }

    override fun getItemViewType(position: Int): Int {
        when(reviewModelList.get(position).questionsItem!!.type){
            type2100 ->{
                return type2100
            }

            type4100 ->{
                return type4100
            }

            type2210 ->{
                return type2210
            }

            type2201 ->{
                return type2201
            }
        }
        return -1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        listOfOptions = reviewModelList.get(position).listOfOptions
        path = localBlobcityPath + reviewModelList.get(position).questionsItem!!.id
        Log.e("path", path)
        for (filename in Utils.listAssetFiles(path!!,context)!!){
            if (filename.contains("question")){
                questionPath = WEBVIEW_PATH+path+"/"+filename
            }
        }

        var webView_option1: WebView ?= null
        var webView_option2: WebView ?= null
        var webView_option3: WebView ?= null
        var webView_option4: WebView ?= null
        var webView_question: WebView ?= null

        val width: Int = context.resources.getDimension(R.dimen._250sdp).toInt()
        val height: Int = context.resources.getDimension(R.dimen._360sdp).toInt()
        val params = RelativeLayout.LayoutParams(width, height)
        val top: Int = context.resources.getDimension(R.dimen._6sdp).toInt()
        val bottom: Int = context.resources.getDimension(R.dimen._6sdp).toInt()

        when(reviewModelList.get(position).questionsItem!!.type){

            type2100 ->{
                webView_option1 = (holder as Layout2100ViewHolder).webView_option1!!
                if (reviewModelList.size > 1) {
                    if (position == 0) {
                        val left: Int = context.resources.getDimension(R.dimen._20sdp).toInt()
                        val right: Int = context.resources.getDimension(R.dimen._6sdp).toInt()

                        params.setMargins(left, top, right, bottom)
                        holder.cardView!!.setLayoutParams(params)

                    }
                    if (position == reviewModelList.size - 1){
                        val left: Int = context.resources.getDimension(R.dimen._6sdp).toInt()
                        val right: Int = context.resources.getDimension(R.dimen._20sdp).toInt()

                        params.setMargins(left, top, right, bottom)
                        holder.cardView!!.setLayoutParams(params)
                    }
                }
                webView_option2 = holder.webView_option2!!
                webView_question = holder.webView_question!!
                setWebViewBGDefault(webView_question, webView_option1, webView_option2, position)
                loadDataWebview(webView_question, webView_option1, webView_option2)
            }

            type4100 ->{
                webView_option1 = (holder as Layout4100ViewHolder).webView_option1!!
                if (reviewModelList.size > 1) {

                    if (position == 0) {
                        val left: Int = context.resources.getDimension(R.dimen._20sdp).toInt()
                        val right: Int = context.resources.getDimension(R.dimen._6sdp).toInt()

                        params.setMargins(left, top, right, bottom)
                        holder.cardView!!.setLayoutParams(params)

                    }
                    if (position == reviewModelList.size - 1){
                        val left: Int = context.resources.getDimension(R.dimen._6sdp).toInt()
                        val right: Int = context.resources.getDimension(R.dimen._20sdp).toInt()

                        params.setMargins(left, top, right, bottom)
                        holder.cardView!!.setLayoutParams(params)
                    }
                }
                webView_option2 = holder.webView_option2!!
                webView_question = holder.webView_question!!
                webView_option3 = holder.webView_option3!!
                webView_option4=holder.webView_option4!!
                setWebViewBGDefault(webView_question, webView_option1,
                    webView_option2, webView_option3, webView_option4, position)
                loadDataWebview(webView_question, webView_option1,
                    webView_option2, webView_option3, webView_option4)
            }

            type2210 ->{
                webView_option1 = (holder as Layout2210ViewHolder).webView_option1!!
                if (reviewModelList.size > 1) {

                    if (position == 0) {
                        val left: Int = context.resources.getDimension(R.dimen._20sdp).toInt()
                        val right: Int = context.resources.getDimension(R.dimen._6sdp).toInt()

                        params.setMargins(left, top, right, bottom)
                        holder.cardView!!.setLayoutParams(params)

                    }
                    if (position == reviewModelList.size - 1){
                        val left: Int = context.resources.getDimension(R.dimen._6sdp).toInt()
                        val right: Int = context.resources.getDimension(R.dimen._20sdp).toInt()

                        params.setMargins(left, top, right, bottom)
                        holder.cardView!!.setLayoutParams(params)
                    }
                }
                webView_option2 = holder.webView_option2!!
                webView_question = holder.webView_question!!
                webView_option3 = holder.webView_option3!!
                webView_option4=holder.webView_option4!!
                setWebViewBGDefault(webView_question, webView_option1,
                    webView_option2, webView_option3, webView_option4, position)
                loadDataWebview(webView_question, webView_option1,
                    webView_option2, webView_option3, webView_option4)
            }

            type2201 ->{
                webView_option1 = (holder as Layout2201ViewHolder).webView_option1!!
                if (reviewModelList.size > 1) {

                    if (position == 0) {
                        val left: Int = context.resources.getDimension(R.dimen._20sdp).toInt()
                        val right: Int = context.resources.getDimension(R.dimen._6sdp).toInt()

                        params.setMargins(left, top, right, bottom)
                        holder.cardView!!.setLayoutParams(params)

                    }
                    if (position == reviewModelList.size - 1){
                        val left: Int = context.resources.getDimension(R.dimen._6sdp).toInt()
                        val right: Int = context.resources.getDimension(R.dimen._20sdp).toInt()

                        params.setMargins(left, top, right, bottom)
                        holder.cardView!!.setLayoutParams(params)
                    }
                }
                webView_option2 = holder.webView_option2!!
                webView_question = holder.webView_question!!
                webView_option3 = holder.webView_option3!!
                webView_option4=holder.webView_option4!!
                setWebViewBGDefault(webView_question, webView_option1,
                    webView_option2, webView_option3, webView_option4, position)
                loadDataWebview(webView_question, webView_option1,
                    webView_option2, webView_option3, webView_option4)
            }
        }
    }

    private fun loadDataWebview(webView_question: WebView,
                                webView_option1: WebView,
                                webView_option2: WebView,
                                webView_option3: WebView,
                                webView_option4: WebView){

        webView_question.loadUrl(questionPath)
        opt1Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(0)
        opt2Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(1)
        opt3Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(2)
        opt4Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(3)
        webView_option1.loadUrl(opt1Path)
        webView_option2.loadUrl(opt2Path)
        webView_option3.loadUrl(opt3Path)
        webView_option4.loadUrl(opt4Path)
    }

    private fun loadDataWebview(webView_question: WebView,
                                webView_option1: WebView,
                                webView_option2: WebView){

        webView_question.loadUrl(questionPath)
        opt1Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(0)
        opt2Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(1)
        webView_option1.loadUrl(opt2Path)
        webView_option2.loadUrl(opt1Path)
    }

    class Layout4100ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var webView_question: WebView?=  null
        var webView_option1: WebView?=  null
        var webView_option2: WebView?=  null
        var webView_option3: WebView?=  null
        var webView_option4: WebView?=  null
        var cardView: CardView?=  null

        init {
            webView_question = itemView.findViewById(com.blobcity.R.id.webView_question)
            webView_option1 = itemView.findViewById(com.blobcity.R.id.webView_option1)
            webView_option2 = itemView.findViewById(com.blobcity.R.id.webView_option2)
            webView_option3 = itemView.findViewById(com.blobcity.R.id.webView_option3)
            webView_option4 = itemView.findViewById(com.blobcity.R.id.webView_option4)
            cardView = itemView.findViewById(com.blobcity.R.id.cardView)
        }

    }

    class Layout2100ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var webView_question: WebView?=  null
        var webView_option1: WebView?=  null
        var webView_option2: WebView?=  null
        var cardView: CardView?=  null
        init {
            webView_question = itemView.findViewById(com.blobcity.R.id.webView_question)
            webView_option1 = itemView.findViewById(com.blobcity.R.id.webView_option1)
            webView_option2 = itemView.findViewById(com.blobcity.R.id.webView_option2)
            cardView = itemView.findViewById(com.blobcity.R.id.cardView)
        }
    }

    class Layout2210ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var webView_question: WebView?=  null
        var webView_option1: WebView?=  null
        var webView_option2: WebView?=  null
        var webView_option3: WebView?=  null
        var webView_option4: WebView?=  null
        var cardView: CardView?=  null

        init {
            webView_question = itemView.findViewById(com.blobcity.R.id.webView_question)
            webView_option1 = itemView.findViewById(com.blobcity.R.id.webView_option1)
            webView_option2 = itemView.findViewById(com.blobcity.R.id.webView_option2)
            webView_option3 = itemView.findViewById(com.blobcity.R.id.webView_option3)
            webView_option4 = itemView.findViewById(com.blobcity.R.id.webView_option4)
            cardView = itemView.findViewById(com.blobcity.R.id.cardView)
        }
    }

    class Layout2201ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var webView_question: WebView?=  null
        var webView_option1: WebView?=  null
        var webView_option2: WebView?=  null
        var webView_option3: WebView?=  null
        var webView_option4: WebView?=  null
        var cardView: CardView?=  null

        init {
            webView_question = itemView.findViewById(com.blobcity.R.id.webView_question)
            webView_option1 = itemView.findViewById(com.blobcity.R.id.webView_option1)
            webView_option2 = itemView.findViewById(com.blobcity.R.id.webView_option2)
            webView_option3 = itemView.findViewById(com.blobcity.R.id.webView_option3)
            webView_option4 = itemView.findViewById(com.blobcity.R.id.webView_option4)
            cardView = itemView.findViewById(com.blobcity.R.id.cardView)
        }
    }

    fun setWebViewBGDefault(webView_question: WebView,
                                    webView_option1: WebView,
                                    webView_option2: WebView,
                                    webView_option3: WebView,
                                    webView_option4: WebView, position: Int){
        webView_question.setBackgroundColor(0)

        webView_option1.setBackgroundResource(com.blobcity.R.drawable.option_curved_border)
        webView_option2.setBackgroundResource(com.blobcity.R.drawable.option_curved_border)
        webView_option3.setBackgroundResource(com.blobcity.R.drawable.option_curved_border)
        webView_option4.setBackgroundResource(com.blobcity.R.drawable.option_curved_border)

        webView_option1.setBackgroundColor(0x00000000)
        webView_option2.setBackgroundColor(0x00000000)
        webView_option3.setBackgroundColor(0x00000000)
        webView_option4.setBackgroundColor(0x00000000)


        reviewModelList.get(position).listOfOptions!!.forEachIndexed { index, s ->

        }

        for (item in reviewModelList.get(position).optionsWithAnswerList!!){
            if (0 == item.option) {
                if (item.istrue!!) {
                    webView_option1.setBackgroundResource(com.blobcity.R.drawable.option_correct_curved_border)
                } else {
                    webView_option1.setBackgroundResource(com.blobcity.R.drawable.wrong_answer_overlay)
                }
            }
            if (1 == item.option) {
                if (item.istrue!!){
                    webView_option2.setBackgroundResource(com.blobcity.R.drawable.option_correct_curved_border)
                }else{
                    webView_option2.setBackgroundResource(com.blobcity.R.drawable.wrong_answer_overlay)
                }
            }

            if (2 == item.option) {
                if (item.istrue!!){
                    webView_option3.setBackgroundResource(com.blobcity.R.drawable.option_correct_curved_border)
                }else{
                    webView_option3.setBackgroundResource(com.blobcity.R.drawable.wrong_answer_overlay)
                }
            }
            if (3 == item.option) {
                if (item.istrue!!){
                    webView_option4.setBackgroundResource(com.blobcity.R.drawable.option_correct_curved_border)
                }else{
                    webView_option4.setBackgroundResource(com.blobcity.R.drawable.wrong_answer_overlay)
                }
            }
        }

        /*if (reviewModelList.get(position).optionsWithAnswerList!!.get(1).istrue!!){
            webView_option2.setBackgroundResource(R.drawable.option_correct_green_border)
        }else{
            webView_option2.setBackgroundResource(R.drawable.option_red_wrong_broder)
        }

        if (reviewModelList.get(position).optionsWithAnswerList!!.get(2).istrue!!){
            webView_option3.setBackgroundResource(R.drawable.option_correct_green_border)
        }else{
            webView_option3.setBackgroundResource(R.drawable.option_red_wrong_broder)
        }
        if (reviewModelList.get(position).optionsWithAnswerList!!.get(3).istrue!!){
            webView_option4.setBackgroundResource(R.drawable.option_correct_green_border)
        }else{
            webView_option4.setBackgroundResource(R.drawable.option_red_wrong_broder)
        }*/
    }

    fun setWebViewBGDefault(webView_question: WebView,
                            webView_option1: WebView,
                            webView_option2: WebView, position: Int){
        webView_question!!.setBackgroundColor(0)
        webView_option1!!.setBackgroundResource(com.blobcity.R.drawable.option_curved_border)
        webView_option2!!.setBackgroundResource(com.blobcity.R.drawable.option_curved_border)

        webView_option1!!.setBackgroundColor(0x00000000)
        webView_option2!!.setBackgroundColor(0x00000000)

        reviewModelList.get(position).listOfOptions!!.forEachIndexed { index, s ->
            for (item in reviewModelList.get(position).optionsWithAnswerList!!){
                if (index == item.option){
                    if (item.option == 0){
                        if (item.istrue!!){
                            webView_option1.setBackgroundResource(com.blobcity.R.drawable.option_correct_curved_border)
                        }else{
                            webView_option1.setBackgroundResource(com.blobcity.R.drawable.wrong_answer_overlay)
                        }
                    }
                    if (item.option == 1){
                        if (item.istrue!!){
                            webView_option2.setBackgroundResource(com.blobcity.R.drawable.option_correct_curved_border)
                        }else{
                            webView_option2.setBackgroundResource(com.blobcity.R.drawable.wrong_answer_overlay)
                        }
                    }
                }
            }
        }

        /*if (reviewModelList.get(position).optionsWithAnswerList!!.get(0).istrue!!){
            webView_option1.setBackgroundResource(R.drawable.option_correct_green_border)
        }else{
            webView_option1.setBackgroundResource(R.drawable.option_red_wrong_broder)
        }

        if (reviewModelList.get(position).optionsWithAnswerList!!.get(1).istrue!!){
            webView_option2.setBackgroundResource(R.drawable.option_correct_green_border)
        }else{
            webView_option2.setBackgroundResource(R.drawable.option_red_wrong_broder)
        }*/
    }
}
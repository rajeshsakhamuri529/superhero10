package com.blobcity.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import com.blobcity.R
import com.blobcity.model.ReviewModel
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils


class ReviewAdapter(val reviewModelList: ArrayList<ReviewModel>,
                    val context: Context) :
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
                    .inflate(R.layout.review_2100_layout, p0, false)
                viewHolder = Layout2100ViewHolder(view)
                return viewHolder
            }

            type4100 ->{
                view = LayoutInflater.from(context)
                    .inflate(R.layout.review_4100_layout, p0, false)
                viewHolder = Layout4100ViewHolder(view)
                return viewHolder
            }

            type2210 ->{
                view = LayoutInflater.from(context)
                    .inflate(R.layout.review_2210_layout, p0, false)
                viewHolder = Layout2210ViewHolder(view)
                return viewHolder
            }

            type2201 ->{
                view = LayoutInflater.from(context)
                    .inflate(R.layout.review_2201_layout, p0, false)
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

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        listOfOptions = reviewModelList.get(p1).listOfOptions
        path = reviewModelList.get(p1).questionsItem!!.id
        for (filename in Utils.getListOfFilesFromAsset(path, context)){
            if (filename.contains("question")){
                questionPath = WEBVIEW_PATH+path+"/"+filename
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

    class Layout4100ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var webView_question: WebView?=  null
        var webView_option1: WebView?=  null
        var webView_option2: WebView?=  null
        var webView_option3: WebView?=  null
        var webView_option4: WebView?=  null

        init {
            webView_question = itemView.findViewById(R.id.webView_question)
            webView_option1 = itemView.findViewById(R.id.webView_option1)
            webView_option2 = itemView.findViewById(R.id.webView_option2)
            webView_option3 = itemView.findViewById(R.id.webView_option3)
            webView_option4 = itemView.findViewById(R.id.webView_option4)
            //        this.txtHead = (TextView) itemView.findViewById(R.id.txtHead);
        }

    }

    class Layout2100ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var webView_question: WebView?=  null
        var webView_option1: WebView?=  null
        var webView_option2: WebView?=  null
        init {
            webView_question = itemView.findViewById(R.id.webView_question)
            webView_option1 = itemView.findViewById(R.id.webView_option1)
            webView_option2 = itemView.findViewById(R.id.webView_option2)
            //        this.txtHead = (TextView) itemView.findViewById(R.id.txtHead);
        }
    }

    class Layout2210ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var webView_question: WebView?=  null
        var webView_option1: WebView?=  null
        var webView_option2: WebView?=  null
        var webView_option3: WebView?=  null
        var webView_option4: WebView?=  null

        init {
            webView_question = itemView.findViewById(R.id.webView_question)
            webView_option1 = itemView.findViewById(R.id.webView_option1)
            webView_option2 = itemView.findViewById(R.id.webView_option2)
            webView_option3 = itemView.findViewById(R.id.webView_option3)
            webView_option4 = itemView.findViewById(R.id.webView_option4)
            //        this.txtHead = (TextView) itemView.findViewById(R.id.txtHead);
        }
    }

    class Layout2201ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var webView_question: WebView?=  null
        var webView_option1: WebView?=  null
        var webView_option2: WebView?=  null
        var webView_option3: WebView?=  null
        var webView_option4: WebView?=  null

        init {
            webView_question = itemView.findViewById(R.id.webView_question)
            webView_option1 = itemView.findViewById(R.id.webView_option1)
            webView_option2 = itemView.findViewById(R.id.webView_option2)
            webView_option3 = itemView.findViewById(R.id.webView_option3)
            webView_option4 = itemView.findViewById(R.id.webView_option4)
            //        this.txtHead = (TextView) itemView.findViewById(R.id.txtHead);
        }
    }

    fun setWebViewBGDefault(webView_question: WebView,
                                    webView_option1: WebView,
                                    webView_option2: WebView,
                                    webView_option3: WebView,
                                    webView_option4: WebView){
        webView_question!!.setBackgroundColor(0)
        webView_option1!!.setBackgroundResource(R.drawable.option_curved_border)
        webView_option2!!.setBackgroundResource(R.drawable.option_curved_border)
        if (webView_option3 != null) {
            webView_option3!!.setBackgroundResource(R.drawable.option_curved_border)
            webView_option3!!.setBackgroundColor(0x00000000)
        }
        if (webView_option4 != null) {
            webView_option4!!.setBackgroundResource(R.drawable.option_curved_border)
            webView_option4!!.setBackgroundColor(0x00000000)
        }

        webView_option1!!.setBackgroundColor(0x00000000)
        webView_option2!!.setBackgroundColor(0x00000000)
    }
}
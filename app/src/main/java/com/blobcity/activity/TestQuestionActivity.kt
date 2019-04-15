package com.blobcity.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.ArrayMap
import android.util.Log
import android.view.View
import com.blobcity.R
import com.blobcity.model.TopicOneBasicResponseModel
import com.blobcity.model.TopicOneQuestionsItem
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_test_question.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import android.support.v7.app.AlertDialog
import android.webkit.WebView
import android.widget.Button

class TestQuestionActivity : AppCompatActivity(), View.OnClickListener {

    private var questionPath: String?=""
    private var hintPath: String?=""
    private var opt1Path: String?=""
    private var opt2Path: String?=""
    private var opt3Path: String?=""
    private var opt4Path: String?=""
    private var arrayMap: ArrayMap<String, List<TopicOneQuestionsItem>>?=null
    private var listWithUniqueString: ArrayList<String>?=null
    private var position: Int = -1
    private var questionsItem: List<TopicOneQuestionsItem>?=null
    private var totalQuestion: Int?=null
    private var listSize: Int?=null
    private var countInt: Int =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_question)

        val path = intent.getStringExtra(DYNAMIC_PATH)

        createArrayMapList(path)

        btn_next.setOnClickListener(this)
        btn_hint.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btn_next ->{
                onBtnNext()
            }

            R.id.btn_hint ->{
                hintAlertDialog()
            }
        }
    }

    private fun hintAlertDialog(){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.hint_dialog_layout, null)
        dialogBuilder.setView(dialogView)

        val webview = dialogView.findViewById(R.id.webview_hint) as WebView
        val btn_gotIt = dialogView.findViewById(R.id.btn_gotIt) as Button
        webview.loadUrl(hintPath)
        webview.setBackgroundColor(0)
        val alertDialog = dialogBuilder.create()
        btn_gotIt.setOnClickListener({
            alertDialog.dismiss()
        })

        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()
    }

    private fun onBtnNext(){
        position++
        if (position < totalQuestion!!){
            countInt++
            val count = "$countInt of $totalQuestion"
            tv_count.text = count
            questionsItem = arrayMap!!.get(listWithUniqueString!!.get(position))
            if (questionsItem!!.size > 1){
                val randomPosition = Random.nextInt(questionsItem!!.size)
                val path = assetOutputPath+questionsItem!!.get(randomPosition).id
                loadDataInWebView(path)
            } else
            {
                val path = assetOutputPath+questionsItem!!.get(0).id
                loadDataInWebView(path)
            }
        }
    }

    private fun createArrayMapList(path: String) {
        val gsonFile = Gson()
        val questionResponseModel = gsonFile.fromJson(path, TopicOneBasicResponseModel::class.java)
        val questionsItem = questionResponseModel.questions
        val listWithDuplicateKeys = ArrayList<String>()

        totalQuestion = questionResponseModel.questionCount
        var questionItemList: ArrayList<TopicOneQuestionsItem>
        arrayMap = ArrayMap()

        for (questionItem in questionsItem){
            val questionBank = questionItem.bank
            listWithDuplicateKeys.add(questionBank)
            if (!arrayMap!!.contains(questionBank)){
                questionItemList = ArrayList()
                questionItemList.add(questionItem)
                arrayMap!![questionBank] = questionItemList
            }else{
                questionItemList = arrayMap!![questionBank] as ArrayList<TopicOneQuestionsItem>
                questionItemList.add(questionItem)
            }
        }
        val setWithUniqueValues = HashSet(listWithDuplicateKeys)
        listWithUniqueString = ArrayList(setWithUniqueValues)

        Collections.sort(listWithUniqueString)
        for (strings in listWithUniqueString!!){
            Log.e("unique", strings)
        }
        listSize = arrayMap!!.size
        if (questionResponseModel.questionCount == arrayMap!!.size){
            Log.e("is list Correct", "yes")
        }
        onBtnNext()
    }

    private fun loadDataInWebView(path: String){
        val listOfString = ArrayList<String>()
        for (filename in Utils.getListOfFilesFromAsset(path, this)){
            if (filename.contains("opt")){
                listOfString.add(filename)
            }
            if (filename.contains("question")){
                questionPath = WEBVIEW_PATH+path+"/"+filename
            }
            if (filename.contains("hint")){
                hintPath = WEBVIEW_PATH+path+"/"+filename
            }
        }

        if (listOfString.size > 2){
            Collections.shuffle(listOfString)
            opt1Path = WEBVIEW_PATH+path+"/"+listOfString.get(0)
            opt2Path = WEBVIEW_PATH+path+"/"+listOfString.get(1)
            opt3Path = WEBVIEW_PATH+path+"/"+listOfString.get(2)
            opt4Path = WEBVIEW_PATH+path+"/"+listOfString.get(3)
            webView_option1.loadUrl(opt1Path)
            webView_option2.loadUrl(opt2Path)
            webView_option3.loadUrl(opt3Path)
            webView_option4.loadUrl(opt4Path)
        }else{
            opt1Path = WEBVIEW_PATH+path+"/"+listOfString.get(0)
            opt2Path = WEBVIEW_PATH+path+"/"+listOfString.get(1)
            webView_option1.loadUrl(opt2Path)
            webView_option2.loadUrl(opt1Path)
            webView_option3.visibility = View.GONE
            webView_option4.visibility = View.GONE
        }

        webView_question.setBackgroundColor(0)
        webView_option1.setBackgroundColor(resources.getColor(R.color.purple_opt_bg))
        webView_option2.setBackgroundColor(resources.getColor(R.color.purple_opt_bg))
        webView_option3.setBackgroundColor(resources.getColor(R.color.purple_opt_bg))
        webView_option4.setBackgroundColor(resources.getColor(R.color.purple_opt_bg))

        webView_question.loadUrl(questionPath)
    }
}
package com.blobcity.activity

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

class TestQuestionActivity : AppCompatActivity() {

    var questionPath: String?=""
    var opt1Path: String?=""
    var opt2Path: String?=""
    var opt3Path: String?=""
    var opt4Path: String?=""
    var arrayMap: ArrayMap<String, List<TopicOneQuestionsItem>>?=null
    var listWithUniqueString: ArrayList<String>?=null
    var position: Int = -1
    var questionsItem: List<TopicOneQuestionsItem>?=null
    var totalQuestion: Int?=null
    var listSize: Int?=null
    var countInt: Int =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_question)

        val path = intent.getStringExtra(DYNAMIC_PATH)

        createArrayMapList(path)

        btn_next.setOnClickListener({
            onBtnNext()
        })
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
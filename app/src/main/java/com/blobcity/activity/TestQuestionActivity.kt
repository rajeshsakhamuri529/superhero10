package com.blobcity.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.ArrayMap
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import com.blobcity.model.TopicOneBasicResponseModel
import com.blobcity.model.TopicOneQuestionsItem
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_test_question.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

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
    private var listOfOptions: ArrayList<String>? = null
    private var randomPosition: Int = -1
    private var availableLife: Int? = null
    private var totalLife: Int? = null
    var btn_hint: Button ?=  null
    var btn_next: Button ?=  null
    var webView_question: WebView ?=  null
    var webView_option1: WebView ?=  null
    var webView_option2: WebView ?=  null
    var webView_option3: WebView ?=  null
    var webView_option4: WebView ?=  null
    var startClickTime: Long? = null
    private val MAX_CLICK_DURATION = 200

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.blobcity.R.layout.activity_test_question)

        val path = intent.getStringExtra(DYNAMIC_PATH)

        btn_hint = findViewById(com.blobcity.R.id.btn_hint) as Button
        btn_next = findViewById(com.blobcity.R.id.btn_next) as Button
        webView_question = findViewById(com.blobcity.R.id.webView_question) as WebView
        webView_option1 = findViewById(com.blobcity.R.id.webView_option1) as WebView
        webView_option2 = findViewById(com.blobcity.R.id.webView_option2) as WebView
        webView_option3 = findViewById(com.blobcity.R.id.webView_option3) as WebView
        webView_option4 = findViewById(com.blobcity.R.id.webView_option4) as WebView

        createArrayMapList(path)

        btn_hint!!.visibility = View.GONE

        btn_next!!.setOnClickListener(this)
        btn_hint!!.setOnClickListener(this)
        webView_option1!!.setOnTouchListener { v, event ->
            clickOptions(event, 0, "true")
            true
        }

        webView_option2!!.setOnTouchListener { v, event ->
            clickOptions(event, 1, "false")
            true
        }

        webView_option3!!.setOnTouchListener { v, event ->
            clickOptions(event, 2, "")
            true
        }

        webView_option4!!.setOnTouchListener { v, event ->
            clickOptions(event, 3, "")
            true
        }
    }

    private fun clickOptions(event: MotionEvent, position: Int, stringAns: String){
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            startClickTime = Calendar.getInstance().getTimeInMillis()
        }

        if (event.action == MotionEvent.ACTION_UP){
            val clickDuration = Calendar.getInstance().timeInMillis - startClickTime!!
            if (clickDuration < MAX_CLICK_DURATION){
                checkAnswer(position, stringAns)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            com.blobcity.R.id.btn_next ->{
                onBtnNext()
            }

            com.blobcity.R.id.btn_hint ->{
                hintAlertDialog()
            }
        }
    }

    private fun hintAlertDialog(){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(com.blobcity.R.layout.hint_dialog_layout, null)
        dialogBuilder.setView(dialogView)

        val webview = dialogView.findViewById(com.blobcity.R.id.webview_hint) as WebView
        val btn_gotIt = dialogView.findViewById(com.blobcity.R.id.btn_gotIt) as Button
        webview.loadUrl(hintPath)
        webview.setBackgroundColor(0)
        val alertDialog = dialogBuilder.create()
        btn_gotIt.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()
    }

    private fun onBtnNext(){
        position++
        btn_hint!!.visibility = View.GONE
        if (position < totalQuestion!!){
            countInt++
            val count = "$countInt of $totalQuestion"
            tv_count.text = count
            questionsItem = arrayMap!!.get(listWithUniqueString!!.get(position))
            if (questionsItem!!.size > 1){
                randomPosition = Random.nextInt(questionsItem!!.size)
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
        if (questionResponseModel.questionCount > 4){
            iv_life3.visibility = View.VISIBLE
            availableLife = 3
            totalLife = 3
        }else{
            iv_life3.visibility = View.GONE
            availableLife = 2
            totalLife = 2
        }
        onBtnNext()
    }

    private fun loadDataInWebView(path: String){
        listOfOptions = ArrayList()
        for (filename in Utils.getListOfFilesFromAsset(path, this)){
            if (filename.contains("opt")){
                listOfOptions!!.add(filename)
            }
            if (filename.contains("question")){
                questionPath = WEBVIEW_PATH+path+"/"+filename
            }
            if (filename.contains("hint")){
                hintPath = WEBVIEW_PATH+path+"/"+filename
            }
        }

        if (listOfOptions!!.size > 2){
            webView_option3!!.visibility = View.VISIBLE
            webView_option4!!.visibility = View.VISIBLE
            Collections.shuffle(listOfOptions!!)
            opt1Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(0)
            opt2Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(1)
            opt3Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(2)
            opt4Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(3)
            webView_option1!!.loadUrl(opt1Path)
            webView_option2!!.loadUrl(opt2Path)
            webView_option3!!.loadUrl(opt3Path)
            webView_option4!!.loadUrl(opt4Path)
        }else{
            opt1Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(0)
            opt2Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(1)
            webView_option1!!.loadUrl(opt2Path)
            webView_option2!!.loadUrl(opt1Path)
            webView_option3!!.visibility = View.GONE
            webView_option4!!.visibility = View.GONE
        }

        webView_question!!.setBackgroundColor(0)
        webView_option1!!.setBackgroundColor(resources.getColor(com.blobcity.R.color.purple_opt_bg))
        webView_option2!!.setBackgroundColor(resources.getColor(com.blobcity.R.color.purple_opt_bg))
        webView_option3!!.setBackgroundColor(resources.getColor(com.blobcity.R.color.purple_opt_bg))
        webView_option4!!.setBackgroundColor(resources.getColor(com.blobcity.R.color.purple_opt_bg))

        webView_question!!.loadUrl(questionPath)
    }

    private fun checkAnswer(optionClicked: Int, answer: String){
        if (listOfOptions!!.size > 2){
            if (listOfOptions!!.get(optionClicked).contains("opt1")){
                Toast.makeText(this, "Right Answer", Toast.LENGTH_LONG).show()
            }else{
                btn_hint!!.visibility = View.VISIBLE
                val intLife = availableLife!!.dec()
                checkLife(intLife)
                availableLife = intLife
            }
        }
        else{
            if (questionsItem!!.size > 1) {
                if (answer.equals(questionsItem!!.get(randomPosition).text, true)) {
                    Toast.makeText(this, "Right Answer", Toast.LENGTH_LONG).show()
                } else {
                    btn_hint!!.visibility = View.VISIBLE
                    val intLife = availableLife!!.dec()
                    checkLife(intLife)
                    availableLife = intLife
                }
            }else{
                if (answer.equals(questionsItem!!.get(0).text, true)) {
                    Toast.makeText(this, "Right Answer", Toast.LENGTH_LONG).show()
                } else {
                    btn_hint!!.visibility = View.VISIBLE
                    val intLife = availableLife!!.dec()
                    checkLife(intLife)
                    availableLife = intLife
                }
            }
        }
    }

    private fun checkLife(life: Int){
        if (totalLife == 3){
            if (life == 2){
                Glide.with(this)
                    .load(com.blobcity.R.drawable.inactive_heart)
                    .into(iv_life3)
            }
        }
        if (life == 1){
            Glide.with(this)
                .load(com.blobcity.R.drawable.inactive_heart)
                .into(iv_life2)
        }
        if (life == 0){
            Glide.with(this)
                .load(com.blobcity.R.drawable.inactive_heart)
                .into(iv_life1)
            webView_option1!!.setOnTouchListener(null)
            webView_option2!!.setOnTouchListener(null)
            webView_option3!!.setOnTouchListener(null)
            webView_option4!!.setOnTouchListener(null)
            Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show()
        }
    }
}
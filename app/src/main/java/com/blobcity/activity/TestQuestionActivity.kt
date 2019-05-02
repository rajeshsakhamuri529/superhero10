package com.blobcity.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import com.blobcity.R
import com.blobcity.model.TrackingModel
import com.blobcity.model.TopicOneBasicResponseModel
import com.blobcity.model.TopicOneQuestionsItem
import com.blobcity.model.TopicStatusModel
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.UniqueUUid
import com.blobcity.utils.Utils
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_test_question.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class TestQuestionActivity : BaseActivity(), View.OnClickListener {

    private var hintPath: String?=""
    private var opt1Path: String?=""
    private var opt2Path: String?=""
    private var opt3Path: String?=""
    private var opt4Path: String?=""
    var courseName: String?=""
    var topicName: String?=""
    private var arrayMap: ArrayMap<String, List<TopicOneQuestionsItem>>?=null
    private var listWithUniqueString: ArrayList<String>?=null
    private var position: Int = -1
    private var questionsItem: List<TopicOneQuestionsItem>?=null
    private var totalQuestion: Int?=null
    private var listSize: Int?=null
    private var countInt: Int =0
    private var listOfOptions: ArrayList<String>? = null
    private var randomPosition: Int = -1
    private var availableLife: Int  = -1
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
    private var isAnswerCorrect: Boolean = false
    private var isLifeZero: Boolean = false
    private var isLevelCompleted: Boolean = false
    var dbRStatus: DatabaseReference?= null
    var dbTrackingStatus: DatabaseReference?=null
    var dbTrackingHintStatus: DatabaseReference?=null
    var courseId: String? =""
    var topicId: String? =""
    var topicLevel: String? = ""
    var complete: String?=""
    var dbAttempts: Int = 0
    var dbTimeStamp: Long ?= 0
    var dbQuestionBank: String?=""
    var dbQPaths: String = ""
    var dbQLevel: String = ""
    var isDbCorrectAnswer: String = ""
    var dbAnswer: String = ""

    override fun setLayout(): Int {
        return R.layout.activity_test_question
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView(){
        val path = intent.getStringExtra(DYNAMIC_PATH)
        courseId = intent.getStringExtra(COURSE_ID)
        topicId = intent.getStringExtra(TOPIC_ID)
        courseName = intent.getStringExtra(COURSE_NAME)
        topicLevel = intent.getStringExtra(TOPIC_LEVEL)
        topicName = intent.getStringExtra(TOPIC_NAME)
        complete = intent.getStringExtra(LEVEL_COMPLETED)
        dbRStatus = FirebaseDatabase.getInstance()
            .getReference("topic_status")
        dbTrackingStatus = FirebaseDatabase.getInstance().getReference("quiz_tracking")
        dbTrackingHintStatus = FirebaseDatabase.getInstance().getReference("hint_tracking")
        dbRStatus!!.keepSynced(true)

        initializeView()

        createArrayMapList(path)

        btn_hint!!.visibility = View.GONE
        btn_next!!.visibility = View.GONE

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

    private fun initializeView(){
        btn_hint = findViewById(com.blobcity.R.id.btn_hint) as Button
        btn_next = findViewById(com.blobcity.R.id.btn_next) as Button
        webView_question = findViewById(com.blobcity.R.id.webView_question) as WebView
        webView_option1 = findViewById(com.blobcity.R.id.webView_option1) as WebView
        webView_option2 = findViewById(com.blobcity.R.id.webView_option2) as WebView
        webView_option3 = findViewById(com.blobcity.R.id.webView_option3) as WebView
        webView_option4 = findViewById(com.blobcity.R.id.webView_option4) as WebView
    }

    private fun clickOptions(event: MotionEvent,
                             position: Int,
                             stringAns: String){
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
            R.id.btn_next ->{
                onBtnNext()
            }

            R.id.btn_hint ->{
                hintAlertDialog()
            }
        }
    }

    private fun hintAlertDialog(){
        if (!isLifeZero) {
            if (!isAnswerCorrect) {
                addTrackDataInDb("hint")
                val dialogBuilder = AlertDialog.Builder(this)
                val inflater = this.layoutInflater
                val dialogView = inflater.inflate(R.layout.hint_dialog_layout, null)
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
        }else{
            Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show()
        }
    }

    private fun onBtnNext(){
        if (!isLifeZero) {
            if (isAnswerCorrect) {
                createPath()
            } else {
                Toast.makeText(this, "Please select right option.", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show()
        }
    }

    private fun createArrayMapList(path: String) {
        val gsonFile = Gson()
        val questionResponseModel = gsonFile.fromJson(path, TopicOneBasicResponseModel::class.java)
        val questionsItems = questionResponseModel.questions
        val listWithDuplicateKeys = ArrayList<String>()

        totalQuestion = questionResponseModel.questionCount
        var questionItemList: ArrayList<TopicOneQuestionsItem>
        arrayMap = ArrayMap()

        for (questionItem in questionsItems){
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
        createPath()
    }

    private fun createPath(){
        position++
        btn_hint!!.visibility = View.GONE
        dbAttempts = 0
        if (position < totalQuestion!!) {
            countInt++
            isAnswerCorrect = false
            btn_next!!.visibility = View.GONE
            val count = "$countInt of $totalQuestion"
            tv_count.text = count
            val paths: String
            questionsItem = arrayMap!!.get(listWithUniqueString!!.get(position))
            if (questionsItem!!.size > 1) {
                randomPosition = Random.nextInt(questionsItem!!.size)
                dbQPaths = questionsItem!!.get(randomPosition).id
                dbQuestionBank = questionsItem!!.get(randomPosition).bank
                dbQLevel = questionsItem!!.get(randomPosition).level
                paths = assetOutputPath + dbQPaths
                loadDataInWebView(paths)
            } else {
                dbQPaths = questionsItem!!.get(0).id
                dbQuestionBank = questionsItem!!.get(0).bank
                dbQLevel = questionsItem!!.get(0).level
                paths = assetOutputPath + dbQPaths
                loadDataInWebView(paths)
            }
        }
        if (position >= totalQuestion!!){
            isLevelCompleted = true
            onBackPressed()
        }
    }

    private fun loadDataInWebView(path: String){
        listOfOptions = ArrayList()
        var questionPath = ""
        for (filename in Utils.getListOfFilesFromAsset(path, this)){
            if (filename.contains("opt")){
                if (!filename.contains("opt5")) {
                    listOfOptions!!.add(filename)
                }
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
        if (!isLifeZero) {
            if (!isAnswerCorrect) {
                dbAttempts++
                if (listOfOptions!!.size > 2) {
                    if (listOfOptions!!.get(optionClicked).contains("opt1")) {
                        isAnswerCorrect = true
                        isDbCorrectAnswer = "true"
                        dbAnswer = "opt1"
                        btn_next!!.visibility = View.VISIBLE
                        Toast.makeText(this, "Right Answer", Toast.LENGTH_LONG).show()
                    } else {
                        if (listOfOptions!!.get(optionClicked).contains("opt2")){
                            dbAnswer = "opt2"
                        }
                        if (listOfOptions!!.get(optionClicked).contains("opt3")){
                            dbAnswer = "opt3"
                        }
                        if (listOfOptions!!.get(optionClicked).contains("opt4")){
                            dbAnswer = "opt4"
                        }
                        isAnswerCorrect = false
                        isDbCorrectAnswer = "false"
                        btn_hint!!.visibility = View.VISIBLE
                        availableLife--
                        checkLife(availableLife)
                    }
                } else {
                    if (questionsItem!!.size > 1) {
                        if (answer.equals(questionsItem!!.get(randomPosition).text, true)) {
                            isAnswerCorrect = true
                            isDbCorrectAnswer = "true"
                            dbAnswer = answer
                            btn_next!!.visibility = View.VISIBLE
                            Toast.makeText(this, "Right Answer", Toast.LENGTH_LONG).show()
                        } else {
                            isAnswerCorrect = false
                            isDbCorrectAnswer = "false"
                            dbAnswer = answer
                            btn_hint!!.visibility = View.VISIBLE
                            availableLife--
                            checkLife(availableLife)
                        }
                    } else {
                        if (answer.equals(questionsItem!!.get(0).text, true)) {
                            isAnswerCorrect = true
                            isDbCorrectAnswer = "true"
                            dbAnswer = answer
                            btn_next!!.visibility = View.VISIBLE
                            Toast.makeText(this, "Right Answer", Toast.LENGTH_LONG).show()
                        } else {
                            isAnswerCorrect = false
                            isDbCorrectAnswer = "false"
                            dbAnswer = answer
                            btn_hint!!.visibility = View.VISIBLE
                            availableLife--
                            checkLife(availableLife)
                        }
                    }
                }
                addTrackDataInDb("answer")
            }
        }else{
            Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show()
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
            isLifeZero = true
            Glide.with(this)
                .load(com.blobcity.R.drawable.inactive_heart)
                .into(iv_life1)
            Toast.makeText(this, "Game Over", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isLevelCompleted){
            if (TextUtils.isEmpty(complete)) {
                addDataInDb()
            }
        }
    }

    private fun addDataInDb(){
        val id: String? = dbRStatus!!.push().key
        val uId: String = UniqueUUid.id(this)
        val topicStatusModel = TopicStatusModel()
        topicStatusModel.id = id
        topicStatusModel.courseId = courseId
        topicStatusModel.topicId = topicId
        topicStatusModel.uId = uId
        topicStatusModel.topicLevel = topicLevel
        topicStatusModel.isLevelComplete = 1
        dbRStatus!!.child(uId).child(id!!).setValue(topicStatusModel)
    }

    private fun addTrackDataInDb(type: String){
        val id: String? = dbTrackingStatus!!.push().key
        val uId: String = UniqueUUid.id(this)
        dbTimeStamp = System.currentTimeMillis()/1000
        val timeStamp: String = dbTimeStamp.toString()
        val trackingModel = TrackingModel()
        trackingModel.timeStamp = timeStamp
        trackingModel.uid = uId
        trackingModel.id = id!!
        trackingModel.type = type
        trackingModel.grade = courseName
        trackingModel.topic = topicName
        trackingModel.quizLevel = dbQLevel
        trackingModel.qb = dbQuestionBank
        trackingModel.questionPath = dbQPaths
        trackingModel.attempt = dbAttempts
        if (type.equals("answer")) {
            trackingModel.answer = dbAnswer
            trackingModel.answerStatus = isDbCorrectAnswer
            dbTrackingStatus!!.child(dbQPaths).child(id).setValue(trackingModel)
        }else{
            dbTrackingHintStatus!!.child(dbQPaths).child(id).setValue(trackingModel)
        }

    }
}
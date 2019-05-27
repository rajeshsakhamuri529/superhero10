package com.blobcity.activity

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.blobcity.R
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.model.*
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.UniqueUUid
import com.blobcity.utils.Utils
import com.blobcity.viewmodel.TopicStatusVM
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
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
    var dbPosition: Int ?= null
    var courseName: String?=""
    var topicName: String?=""
    private var arrayMap: ArrayMap<String, List<TopicOneQuestionsItem>>?=null
    private var listWithUniqueString: ArrayList<String>?=null
    private var position: Int = -1
    private var questionsItem: List<TopicOneQuestionsItem>?=null
    private var singleQuestionsItem: TopicOneQuestionsItem?=null
    private var totalQuestion: Int?=null
    private var listSize: Int?=null
    private var countInt: Int =0
    private var listOfOptions: ArrayList<String>? = null
    private var randomPosition: Int = -1
    private var availableLife: Int  = -1
    private var totalLife: Int? = null
    var webView_question: WebView ?=  null
    var webView_option1: WebView ?=  null
    var webView_option2: WebView ?=  null
    var webView_option3: WebView ?=  null
    var webView_option4: WebView ?=  null
    var startClickTime: Long? = null
    private val MAX_CLICK_DURATION = 200
    private var isLevelCompleted: Boolean = false
    private var isFirstAnswerGiven: Boolean = false
    private var isAnswerCorrect: Boolean = false
    private var isLifeZero: Boolean = false
    private var isHandlerExecuted: Boolean = false
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
    val handler = Handler()
    var animationFadeIn: Animation ?= null
    var animationFadeIn1500: Animation ?= null
    var animationFadeIn1000: Animation ?= null
    var animationFadeIn500: Animation ?= null
    var type:Int = 0
    var child: View ?= null
    var isOption1Wrong = false
    var isOption2Wrong = false
    var isOption3Wrong = false
    var isOption4Wrong = false
    var firestore: FirebaseFirestore?=null
    var quizTimer: Timer ?= null
    var quizTimerCount = 0
    var perQuizTimer: Timer ?= null
    var perQuizTimerCount = 0
    var bank: Bank ?= null
    var dbIsHintUsed = false
    val bankList: ArrayList<Bank> = ArrayList()
    var answerList: ArrayList<String> ?= null
    val reviewModelList : ArrayList<ReviewModel> = ArrayList()
    var reviewModel : ReviewModel ?= null
    var optionsWithAnswerList: ArrayList<OptionsWithAnswer>?= null

    override fun setLayout(): Int {
        return R.layout.activity_test_question
    }

    override fun initView(){
        val path = intent.getStringExtra(DYNAMIC_PATH)
        courseId = intent.getStringExtra(COURSE_ID)
        topicId = intent.getStringExtra(TOPIC_ID)
        courseName = intent.getStringExtra(COURSE_NAME)
        topicLevel = intent.getStringExtra(TOPIC_LEVEL)
        topicName = intent.getStringExtra(TOPIC_NAME)
        complete = intent.getStringExtra(LEVEL_COMPLETED)
        dbPosition = intent.getIntExtra(TOPIC_POSITION, -1)
        quizTimer = Timer()

        animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_700)
        animationFadeIn1500 = AnimationUtils.loadAnimation(this, R.anim.fade_in_500)
        animationFadeIn1000 = AnimationUtils.loadAnimation(this, R.anim.fade_in_300)
        animationFadeIn500 = AnimationUtils.loadAnimation(this, R.anim.fade_in_100)

        firestore = FirebaseFirestore.getInstance()

        /*dbRStatus = FirebaseDatabase.getInstance()
            .getReference("topic_status")
        dbTrackingStatus = FirebaseDatabase.getInstance().getReference("quiz_tracking")
        dbTrackingHintStatus = FirebaseDatabase.getInstance().getReference("hint_tracking")
        dbRStatus!!.keepSynced(true)*/

        createArrayMapList(path)

        btn_hint!!.visibility = View.INVISIBLE
        btn_next!!.visibility = View.INVISIBLE

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeView(view: View){
        webView_question = view.findViewById(R.id.webView_question)
        webView_option1 = view.findViewById(R.id.webView_option1)
        webView_option2 = view.findViewById(R.id.webView_option2)
        webView_option3 = view.findViewById(R.id.webView_option3)
        webView_option4 = view.findViewById(R.id.webView_option4)

        btn_next!!.setOnClickListener(this)
        btn_hint!!.setOnClickListener(this)
        webView_option1!!.setOnTouchListener { v, event ->
            if (!isOption1Wrong){
                clickOptions(event, 0, "true")
            }
            true
        }

        webView_option2!!.setOnTouchListener { v, event ->
            if (!isOption2Wrong){
                clickOptions(event, 1, "false")
            }
            true
        }

        if (webView_option3 != null) {
            webView_option3!!.setOnTouchListener { v, event ->
                if (!isOption3Wrong) {
                    clickOptions(event, 2, "")
                }
                true
            }
        }

        if (webView_option4 != null) {
            webView_option4!!.setOnTouchListener { v, event ->
                if (!isOption4Wrong) {
                    clickOptions(event, 3, "")
                }
                true
            }
        }
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
                dbIsHintUsed = true
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
            if (reviewModel != null){
                reviewData()
                reviewModelList.add(reviewModel!!)
                reviewModel = null
            }
            if (isAnswerCorrect) {
                createPath()
            } else {
                Toast.makeText(this, "Please select right option.", Toast.LENGTH_LONG).show()
            }
        }else{

            if (reviewModel != null){
                reviewData()
                reviewModelList.add(reviewModel!!)
                reviewModel = null
            }
            navigateToSummaryScreen(false)
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
        btn_hint!!.visibility = View.INVISIBLE
        quizTimer!!.scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                quizTimerCount++
            }

        },1000,1000)
        handler.removeCallbacksAndMessages(null)
        if (child != null) {
            ll_inflate.removeView(child!!)
        }
        if (bank != null){
            addBankData()
            bankList.add(bank!!)
            bank = null
            dbIsHintUsed = false
            dbAttempts = 0
        }
        if (perQuizTimer != null){
            perQuizTimer = null
            perQuizTimerCount = 0
        }
       /* if (reviewModel != null){
            reviewData()
            reviewModelList.add(reviewModel!!)
            reviewModel = null
        }*/
        if (position < totalQuestion!!) {
            bank = Bank()
            reviewModel = ReviewModel()
            answerList = ArrayList()
            optionsWithAnswerList = ArrayList()
            perQuizTimer = Timer()
            perQuizTimer!!.scheduleAtFixedRate(object : TimerTask(){
                override fun run() {
                    perQuizTimerCount++
                }

            },1000,1000)
            isOption1Wrong = false
            isOption2Wrong = false
            isOption3Wrong = false
            isOption4Wrong = false
            countInt++
            isAnswerCorrect = false
            btn_next!!.visibility = View.INVISIBLE
            val count = "$countInt of $totalQuestion"
            tv_count.text = count
            val paths: String
            questionsItem = arrayMap!!.get(listWithUniqueString!!.get(position))
            if (questionsItem!!.size > 1) {
                randomPosition = Random.nextInt(questionsItem!!.size)
                singleQuestionsItem = questionsItem!!.get(randomPosition)
                dbQPaths = questionsItem!!.get(randomPosition).id
                dbQuestionBank = questionsItem!!.get(randomPosition).bank
                dbQLevel = questionsItem!!.get(randomPosition).level
                paths = assetOutputPath + dbQPaths
                type = questionsItem!!.get(randomPosition).type
                loadDataInWebView(paths)
            } else {
                singleQuestionsItem = questionsItem!!.get(0)
                dbQPaths = questionsItem!!.get(0).id
                dbQuestionBank = questionsItem!!.get(0).bank
                dbQLevel = questionsItem!!.get(0).level
                paths = assetOutputPath + dbQPaths
                type = questionsItem!!.get(0).type
                loadDataInWebView(paths)
            }
        }
        if (position >= totalQuestion!!){
            navigateToSummaryScreen(true)
        }
    }

    private fun addBankData(){
        bank!!.answer = answerList
        bank!!.attempts = dbAttempts
        bank!!.result = isAnswerCorrect
        bank!!.timeTaken = perQuizTimerCount
        bank!!.hint = dbIsHintUsed
        bank!!.id = dbQPaths.substring(dbQPaths.length - 3)
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

        if (type == 4100){
            inflateView4100()
            setWebViewBGDefault()
            webViewGone()
            handler.postDelayed(object : Runnable{
                override fun run() {
                    webViewAnimation()
                }
            }, 2500)
            webViewPathAndLoad(path)
        }

        if (type == 2201){
            inflateView2201()
            setWebViewBGDefault()
            webViewGone()
            handler.postDelayed(object : Runnable{
                override fun run() {
                    webViewAnimation()
                }
            }, 2500)
            webViewPathAndLoad(path)
        }

        if (type == 2210){
            inflateView2210()
            setWebViewBGDefault()
            webViewGone()
            handler.postDelayed(object : Runnable{
                override fun run() {
                    webViewAnimation()
                }
            }, 2500)
            webViewPathAndLoad(path)
        }

        if (type == 2100){
            inflateView2100()
            setWebViewBGDefault()
            webView_question!!.visibility = View.GONE
            webView_option1!!.visibility = View.GONE
            webView_option2!!.visibility = View.GONE
            handler.postDelayed(object : Runnable{
                override fun run() {
                    webView_option1!!.visibility = View.VISIBLE
                    webView_option1!!.startAnimation(animationFadeIn500)
                    webView_option2!!.visibility = View.VISIBLE
                    webView_option2!!.startAnimation(animationFadeIn1000)
                }

            }, 2500)
            opt1Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(0)
            opt2Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(1)
            webView_option1!!.loadUrl(opt2Path)
            webView_option2!!.loadUrl(opt1Path)
        }
        webView_question!!.setBackgroundColor(0)
        setWebViewBGDefault()
        handler.postDelayed(object : Runnable{
            override fun run() {
                webView_question!!.visibility = View.VISIBLE
                webView_question!!.startAnimation(animationFadeIn500)
            }
        }, 500)
        webView_question!!.loadUrl(questionPath)
    }

    private fun webViewGone(){
        webView_question!!.visibility = View.GONE
        webView_option1!!.visibility = View.GONE
        webView_option2!!.visibility = View.GONE
        webView_option3!!.visibility = View.GONE
        webView_option4!!.visibility = View.GONE
    }

    private fun webViewAnimation(){
        webView_option1!!.visibility = View.VISIBLE
        webView_option1!!.startAnimation(animationFadeIn500)
        webView_option2!!.visibility = View.VISIBLE
        webView_option2!!.startAnimation(animationFadeIn1000)
        webView_option3!!.visibility = View.VISIBLE
        webView_option3!!.startAnimation(animationFadeIn1500)
        webView_option4!!.visibility = View.VISIBLE
        webView_option4!!.startAnimation(animationFadeIn)
    }

    private fun webViewPathAndLoad(path : String){
        Collections.shuffle(listOfOptions!!)
        opt1Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(0)
        opt2Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(1)
        opt3Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(2)
        opt4Path = WEBVIEW_PATH+path+"/"+listOfOptions!!.get(3)
        webView_option1!!.loadUrl(opt1Path)
        webView_option2!!.loadUrl(opt2Path)
        webView_option3!!.loadUrl(opt3Path)
        webView_option4!!.loadUrl(opt4Path)
    }

    private fun inflateView4100(){
        child = layoutInflater.inflate(R.layout.webview_4100_layout, null)
        ll_inflate.addView(child)
        initializeView(child!!)
    }

    private fun inflateView2201(){
        child = layoutInflater.inflate(R.layout.webview_2201_layout, null)
        ll_inflate.addView(child)
        initializeView(child!!)
    }

    private fun inflateView2100(){
        child = layoutInflater.inflate(R.layout.webview_2100_layout, null)
        ll_inflate.addView(child)
        initializeView(child!!)
    }

    private fun inflateView2210(){
        child = layoutInflater.inflate(R.layout.webview_2210_layout, null)
        ll_inflate.addView(child)
        initializeView(child!!)
    }

    private fun setWebViewBGDefault(){
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

    private fun checkAnswer(optionClicked: Int, answer: String){
        if (!isLifeZero) {
            if (!isAnswerCorrect) {
                if (position == 0){
                    isFirstAnswerGiven = true
                }
                dbAttempts++
                if (listOfOptions!!.size > 2) {
                    if (listOfOptions!!.get(optionClicked).contains("opt1")) {
                        if (position+1 == totalQuestion){
                            isLevelCompleted = true
                        }

                        isAnswerCorrect = true
                        isDbCorrectAnswer = "true"
                        checkWebView(optionClicked, isAnswerCorrect)
                        dbAnswer = "opt1"
                        btn_next!!.visibility = View.VISIBLE
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
                        checkWebView(optionClicked, isAnswerCorrect)
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
                            if (position+1 == totalQuestion){
                                isLevelCompleted = true
                            }
                            checkWebView(optionClicked, isAnswerCorrect)
                            dbAnswer = answer
                            btn_next!!.visibility = View.VISIBLE
                        } else {
                            isAnswerCorrect = false
                            isDbCorrectAnswer = "false"
                            checkWebView(optionClicked, isAnswerCorrect)
                            dbAnswer = answer
                            btn_hint!!.visibility = View.VISIBLE
                            availableLife--
                            checkLife(availableLife)
                        }
                    } else {
                        if (answer.equals(questionsItem!!.get(0).text, true)) {
                            isAnswerCorrect = true
                            isDbCorrectAnswer = "true"
                            if (position+1 == totalQuestion){
                                isLevelCompleted = true
                            }
                            checkWebView(optionClicked, isAnswerCorrect)
                            dbAnswer = answer
                            btn_next!!.visibility = View.VISIBLE
                        } else {
                            isAnswerCorrect = false
                            isDbCorrectAnswer = "false"
                            checkWebView(optionClicked, isAnswerCorrect)
                            dbAnswer = answer
                            btn_hint!!.visibility = View.VISIBLE
                            availableLife--
                            checkLife(availableLife)
                        }
                    }
                }
                answerList!!.add(dbAnswer)
                addTrackDataInDb("answer")
            }
        }else{
            Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkWebView(optionClicked: Int, isRightAnswer: Boolean){
        val optionsWithAnswer = OptionsWithAnswer()
        if (isRightAnswer) {
            if (optionClicked == 0) {
                optionsWithAnswer!!.option = 0
                optionsWithAnswer!!.istrue = true
                optionsWithAnswerList!!.add(optionsWithAnswer!!)
                setCorrectBackground(webView_option1!!)
            }
            if (optionClicked == 1) {
                optionsWithAnswer!!.option = 1
                optionsWithAnswer!!.istrue = true
                optionsWithAnswerList!!.add(optionsWithAnswer!!)
                setCorrectBackground(webView_option2!!)
            }
            if (optionClicked == 2) {
                optionsWithAnswer!!.option = 2
                optionsWithAnswer!!.istrue = true
                optionsWithAnswerList!!.add(optionsWithAnswer!!)
                setCorrectBackground(webView_option3!!)
            }
            if (optionClicked == 3) {
                optionsWithAnswer!!.option = 3
                optionsWithAnswer!!.istrue = true
                optionsWithAnswerList!!.add(optionsWithAnswer!!)
                setCorrectBackground(webView_option4!!)
            }
        }else{
            if (optionClicked == 0) {
                isOption1Wrong = true
                optionsWithAnswer!!.option = 0
                optionsWithAnswer!!.istrue = false
                optionsWithAnswerList!!.add(optionsWithAnswer!!)
                setWrongBackground(webView_option1!!)
            }
            if (optionClicked == 1) {
                isOption2Wrong = true
                optionsWithAnswer!!.option = 1
                optionsWithAnswer!!.istrue = false
                optionsWithAnswerList!!.add(optionsWithAnswer!!)
                setWrongBackground(webView_option2!!)
            }
            if (optionClicked == 2) {
                isOption3Wrong = true
                optionsWithAnswer!!.option = 2
                optionsWithAnswer!!.istrue = false
                optionsWithAnswerList!!.add(optionsWithAnswer!!)
                setWrongBackground(webView_option3!!)
            }
            if (optionClicked == 3) {
                isOption4Wrong = true
                optionsWithAnswer!!.option = 3
                optionsWithAnswer!!.istrue = false
                optionsWithAnswerList!!.add(optionsWithAnswer!!)
                setWrongBackground(webView_option4!!)
            }
        }
    }

    private fun setCorrectBackground(webView: WebView){
        handler.postDelayed(object : Runnable{
            override fun run() {
                isHandlerExecuted = true
                webView.setBackgroundResource(R.drawable.option_correct_curved_border)
            }

        }, 1000)
        webView.setBackgroundResource(R.drawable.option_correct_green_border)

    }

    private fun setWrongBackground(webView: WebView){
        webView.setBackgroundResource(R.drawable.wrong_answer_overlay)
    }

    private fun checkLife(life: Int){
        val zoominAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        val zoomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        val bounceAnim = AnimationUtils.loadAnimation(this, R.anim.blink_anim)
        val animationSet = AnimationSet(false)
        animationSet.addAnimation(bounceAnim)
        if (totalLife == 3){
            if (life == 2){
                iv_life3.startAnimation(animationSet)
                animationSet.setAnimationListener(object : Animation.AnimationListener{
                    override fun onAnimationRepeat(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        Glide.with(this@TestQuestionActivity)
                            .load(R.drawable.inactive_heart)
                            .into(iv_life3)
                    }

                    override fun onAnimationStart(animation: Animation?) {

                    }
                })
            }
        }
        if (life == 1){
            iv_life2.startAnimation(animationSet)
            animationSet.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    Glide.with(this@TestQuestionActivity)
                        .load(com.blobcity.R.drawable.inactive_heart)
                        .into(iv_life2)
                }

                override fun onAnimationStart(animation: Animation?) {

                }
            })

        }
        if (life == 0){
            iv_life1.startAnimation(animationSet)
            animationSet.setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    Glide.with(this@TestQuestionActivity)
                        .load(com.blobcity.R.drawable.inactive_heart)
                        .into(iv_life1)
                }

                override fun onAnimationStart(animation: Animation?) {

                }
            })
            isLifeZero = true
            btn_next.visibility = View.VISIBLE
            /*navigateToSummaryScreen(false)*/
        }
    }

    private fun navigateToSummaryScreen(isLevelCompleted: Boolean){
        quizTimer!!.cancel()
        if (isLevelCompleted){
            if (TextUtils.isEmpty(complete)) {
                addDataInDb()
            }
        }
        addAllDataInDb(isLevelCompleted, false)
        val intent = Intent(this, QuizSummaryActivity::class.java)
        intent.putExtra(REVIEW_MODEL, reviewModelList)
        intent.putExtra(TOPIC_NAME, topicName)
        intent.putExtra(TOPIC_LEVEL, topicLevel)
        intent.putExtra(QUIZ_COUNT, totalQuestion)
        intent.putExtra(TOPIC_ID, topicId)
        intent.putExtra(TOPIC_POSITION, dbPosition)
        startActivity(intent)
        finish()
    }

    private fun addAllDataInDb(isLevelCompleted : Boolean, quit: Boolean){
        val pinfo : PackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0)
        val versionNumber : Int = pinfo.versionCode
        val quiz = Quiz()
        quiz.osv = android.os.Build.VERSION.SDK_INT
        quiz.timeTaken = quizTimerCount
        quiz.topicId = topicId
        quiz.quizSessionId = UUID.randomUUID().toString()
        quiz.topicName = topicName
        quiz.quit = quit
        quiz.uId = UniqueUUid.id(this)
        quiz.timeStamp = System.currentTimeMillis()/1000
        quiz.result = isLevelCompleted
        quiz.os = "android"
        quiz.courseId = courseId
        quiz.courseName = courseName
        quiz.completed = isLevelCompleted
        quiz.appversion = versionNumber
        quiz.quizType = topicLevel
        val bankHashList: HashMap<String, Bank> = HashMap()
        if (bankList.size > 0){
            bankList.forEachIndexed { index, bank ->
                bankHashList.put("b"+(index+1), bank)
            }
        }
        quiz.quizSession = bankHashList

        firestore!!.collection("quiz")
            .add(quiz)
            .addOnSuccessListener { object : OnSuccessListener<DocumentReference> {
                override fun onSuccess(p0: DocumentReference?) {
                    Toast.makeText(getApplicationContext(), "Note has been added!", Toast.LENGTH_SHORT).show()
                }

            } }
            .addOnFailureListener(object : OnFailureListener {
                override fun onFailure(p0: Exception) {
                    Log.e("failure", p0.message)
                    Toast.makeText(getApplicationContext(), "Note could not be added!", Toast.LENGTH_SHORT).show()
                }

            })
    }

    override fun onBackPressed() {
        if (isLevelCompleted) {
            addAllDataInDb(true, false)
            addDataInDb()
            super.onBackPressed()
        }else{
            backPressDialog()
        }
    }

    private fun addDataInDb(){
        val topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        val uId: String = UniqueUUid.id(this)
        val topicStatusEntity = TopicStatusEntity()
        topicStatusEntity.courseId = courseId
        topicStatusEntity.topicId = topicId
        topicStatusEntity.uid = uId
        topicStatusEntity.topicLevel = topicLevel
        topicStatusEntity.isLevelComplete = 1
        topicStatusEntity.topicPosition = dbPosition!!
        topicStatusVM.insert(topicStatusEntity)
        /*val id: String? = dbRStatus!!.push().key
        val uId: String = UniqueUUid.id(this)
        val topicStatusModel = TopicStatusModel()
        topicStatusModel.id = id
        topicStatusModel.courseId = courseId
        topicStatusModel.topicId = topicId
        topicStatusModel.uId = uId
        topicStatusModel.topicLevel = topicLevel
        topicStatusModel.isLevelComplete = 1
        dbRStatus!!.child(uId).child(id!!).setValue(topicStatusModel)*/
    }

    private fun backPressDialog(){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.back_pressed_dialog_layout, null)
        dialogBuilder.setView(dialogView)

        val tv_quit = dialogView.findViewById(R.id.tv_quit) as TextView
        val tv_return = dialogView.findViewById(R.id.tv_return) as TextView
        tv_quit.setOnClickListener {
            if (position >= 0) {
                if (isFirstAnswerGiven) {
                    addAllDataInDb(false, true)
                }
            }
            finish()
        }
        val alertDialog = dialogBuilder.create()
        tv_return.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()
    }

    private fun reviewData(){
        reviewModel!!.questionsItem = singleQuestionsItem
        reviewModel!!.optionsWithAnswerList = optionsWithAnswerList
        reviewModel!!.listOfOptions = listOfOptions
    }

    private fun addTrackDataInDb(type: String){
        /*val id: String? = dbTrackingStatus!!.push().key
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
        }*/

    }
}
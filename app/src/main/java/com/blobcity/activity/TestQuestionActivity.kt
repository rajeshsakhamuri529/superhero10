package com.blobcity.activity

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresApi
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
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.blobcity.R
import com.blobcity.model.*
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.UniqueUUid
import com.blobcity.utils.Utils
import com.blobcity.utils.Utils.listAssetFiles
import com.blobcity.viewmodel.TopicStatusVM
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_test_question.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random
import android.util.Base64
import android.widget.ImageView
import java.lang.Exception


class TestQuestionActivity : BaseActivity(), View.OnClickListener {

    private var hintPath: String? = ""
    private var opt1Path: String? = ""
    private var opt2Path: String? = ""
    private var opt3Path: String? = ""
    private var opt4Path: String? = ""
    var dbPosition: Int? = null
    var courseName: String? = ""
    var topicName: String? = ""
    private var arrayMap: ArrayMap<String, List<TopicOneQuestionsItem>>? = null
    private var listWithUniqueString: ArrayList<String>? = null
    private var position: Int = -1
    private var questionsItem: List<TopicOneQuestionsItem>? = null
    private var singleQuestionsItem: TopicOneQuestionsItem? = null
    private var totalQuestion: Int? = null
    private var listSize: Int? = null
    private var countInt: Int = 0
    private var listOfOptions: ArrayList<String>? = null
    private var randomPosition: Int = -1
    private var availableLife: Int = -1
    private var totalLife: Int? = null
    var webView_question: WebView? = null
    var webView_option1: WebView? = null
    var webView_option2: WebView? = null
    var webView_option3: WebView? = null
    var webView_option4: WebView? = null
    var webView_option1_opacity: WebView? = null
    var webView_option2_opacity: WebView? = null
    var webView_option3_opacity: WebView? = null
    var webView_option4_opacity: WebView? = null
    var startClickTime: Long? = null
    private val MAX_CLICK_DURATION = 200
    private var isLevelCompleted: Boolean = false
    private var isFirstAnswerGiven: Boolean = false
    private var isAnswerCorrect: Boolean = false
    private var isLifeZero: Boolean = false
    private var isHandlerExecuted: Boolean = false
    var dbRStatus: DatabaseReference? = null
    var dbTrackingStatus: DatabaseReference? = null
    var dbTrackingHintStatus: DatabaseReference? = null
    var courseId: String? = ""
    var topicId: String? = ""
    var topicLevel: String? = ""
    var complete: String? = ""
    var dbAttempts: Int = 0
    var dbTimeStamp: Long? = 0
    var dbQuestionBank: String? = ""
    var dbQPaths: String = ""
    var dbQLevel: String = ""
    var isDbCorrectAnswer: String = ""
    var dbAnswer: String = ""
    val handler = Handler()
    var animationFadeIn: Animation? = null
    var animationFadeIn1500: Animation? = null
    var animationFadeIn1000: Animation? = null
    var animationFadeIn500: Animation? = null
    var type: Int = 0
    var child: View? = null
    var isOption1Wrong = false
    var isOption2Wrong = false
    var isOption3Wrong = false
    var isOption4Wrong = false
    var firestore: FirebaseFirestore? = null
    var quizTimer: Timer? = null
    var quizTimerCount = 0
    var perQuizTimer: Timer? = null
    var perQuizTimerCount = 0
    var bank: Bank? = null
    var dbIsHintUsed = false
    val bankList: ArrayList<Bank> = ArrayList()
    var answerList: ArrayList<String>? = null
    val reviewModelList: ArrayList<ReviewModel> = ArrayList()
    var reviewModel: ReviewModel? = null
    var optionsWithAnswerList: ArrayList<OptionsWithAnswer>? = null
    var oPath: String? = null
    var topicStatusVM: TopicStatusVM? = null
    var dynamicPath: String? = null
    var folderName: String? = null
    var gradeTitle: String? = null
    var unAnsweredList: ArrayList<Int>? = null

    override var layoutID: Int = R.layout.activity_test_question

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun initView() {
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        dynamicPath = intent.getStringExtra(DYNAMIC_PATH)
        courseId = intent.getStringExtra(COURSE_ID)
        topicId = intent.getStringExtra(TOPIC_ID)
        courseName = intent.getStringExtra(COURSE_NAME)
        topicLevel = intent.getStringExtra(TOPIC_LEVEL)
        topicName = intent.getStringExtra(TOPIC_NAME)
        complete = intent.getStringExtra(LEVEL_COMPLETED)
        dbPosition = intent.getIntExtra(TOPIC_POSITION, -1)
        oPath = intent.getStringExtra(FOLDER_PATH)
        folderName = intent.getStringExtra(FOLDER_NAME)
        gradeTitle = intent.getStringExtra(TITLE_TOPIC)
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

        createArrayMapList(dynamicPath!!)

        btn_hint!!.visibility = View.INVISIBLE
        btn_next!!.visibility = View.INVISIBLE

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeView(view: View) {
        webView_question = view.findViewById(R.id.webView_question)
        webView_option1 = view.findViewById(R.id.webView_option1)
        webView_option2 = view.findViewById(R.id.webView_option2)
        webView_option3 = view.findViewById(R.id.webView_option3)
        webView_option4 = view.findViewById(R.id.webView_option4)

        webView_option1_opacity = view.findViewById(R.id.webView_option1_opacity)
        webView_option2_opacity = view.findViewById(R.id.webView_option2_opacity)
        webView_option3_opacity = view.findViewById(R.id.webView_option3_opacity)
        webView_option4_opacity = view.findViewById(R.id.webView_option4_opacity)
        /*webView_option2?.setInitialScale(1);
        webView_option2?.getSettings()?.setLoadWithOverviewMode(true)
        webView_option2?.getSettings()?.setUseWideViewPort(true)*/
        //webView_option2?.getSettings()?.setDefaultZoom(WebSettings.ZoomDensity.FAR);

        btn_next!!.setOnClickListener(this)
        btn_hint!!.setOnClickListener(this)
        iv_cancel_test_question!!.setOnClickListener(this)
        webView_option1!!.setOnTouchListener { v, event ->
            if (!isOption1Wrong) {
                clickOptions(event, 0, "true")
            }
            true
        }

        webView_option2!!.setOnTouchListener { v, event ->
            if (!isOption2Wrong) {
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

    private fun clickOptions(
        event: MotionEvent,
        position: Int,
        stringAns: String
    ) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startClickTime = Calendar.getInstance().getTimeInMillis()
        }

        if (event.action == MotionEvent.ACTION_UP) {
            val clickDuration = Calendar.getInstance().timeInMillis - startClickTime!!
            if (clickDuration < MAX_CLICK_DURATION) {
                checkAnswer(position, stringAns)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_cancel_test_question -> {
                onBackPressed()
            }
            R.id.btn_next -> {
                onBtnNext()
            }

            R.id.btn_hint -> {
                hintAlertDialog()
            }
        }
    }

    private fun hintAlertDialog() {
        //if (!isLifeZero) {
        // if (!isAnswerCorrect) {
        dbIsHintUsed = true
        /*addTrackDataInDb("hint")*/
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.hint_dialog_layout, null)
        dialogBuilder.setView(dialogView)

        val webview = dialogView.findViewById(com.blobcity.R.id.webview_hint) as WebView
        val btn_gotIt = dialogView.findViewById(com.blobcity.R.id.btn_gotIt) as Button

        webview.settings.javaScriptEnabled = true
        val hint = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("onPageFinished", url + "!")
                injectCSS(view, "Hint")
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
        // }
        ///}
    }

    private fun wrongAnswerAlertDialog() {
        // if (!isLifeZero) {
        // if (!isAnswerCorrect) {

        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.wrong_answer_dialog_layout, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        val btn_ok = dialogView.findViewById(R.id.btn_ok) as Button
        val iv_heart_3 = dialogView.findViewById(R.id.dialog_iv_life3) as ImageView
        val iv_heart_2 = dialogView.findViewById(R.id.dialog_iv_life2) as ImageView
        val iv_heart_1 = dialogView.findViewById(R.id.dialog_iv_life1) as ImageView
        val tv_text = dialogView.findViewById(R.id.tv_msg2) as TextView
        val alertDialog = dialogBuilder.create()

        Log.d("dialog", availableLife.toString() + "!")
        if (totalLife == 3) {
            iv_heart_3.visibility = View.VISIBLE
        } else {
            iv_heart_3.visibility = View.INVISIBLE
        }

        if (availableLife == 2) {
            if (totalLife == 3) {
                heartType(this, false, iv_heart_3)
            }
            heartType(this, true, iv_heart_2)
            heartType(this, true, iv_heart_1)

        } else if (availableLife == 1) {
            if (totalLife == 3) {
                heartType(this, false, iv_heart_3)
            }
            heartType(this, false, iv_heart_2)
            heartType(this, true, iv_heart_1)

        } else if (availableLife == 0) {
            tv_text.text = "GAME OVER"
            if (totalLife == 3) {
                heartType(this, false, iv_heart_3)
            }
            heartType(this, false, iv_heart_2)
            heartType(this, false, iv_heart_1)


        } else {
            Log.d("available Life", availableLife.toString() + "dialog")
        }

        dialogBuilder.setOnDismissListener {
            Log.d("dialog", "dismiss")
            btn_hint.visibility = View.VISIBLE

            /*if (availableLife == 0) {
                btn_next.text = "DONE"
                btn_next.visibility = View.VISIBLE
                if (webView_option1 != null) {
                    Log.d("web1","true")
                    if (webView_option1!!.url.contains("opt1", false)) {
                        setCorrectBackground(webView_option1!!)
                        //webView_option1!!.setBackgroundResource(R.drawable.option_correct_curved_border)
                    } else {
                        setWrongBackground(webView_option1!!,"")
                        //webView_option1!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                    }
                }

                if (webView_option2 != null) {
                    Log.d("web2","true")
                    if (webView_option2!!.url.contains("opt1", false)) {
                        setCorrectBackground(webView_option2!!)
                        //webView_option2!!.setBackgroundResource(R.drawable.option_correct_curved_border)
                    } else {
                        setWrongBackground(webView_option2!!,"")
                        //webView_option2!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                    }
                }

                if (webView_option3 != null) {
                    if (webView_option3!!.url.contains("opt1", false)) {
                        setCorrectBackground(webView_option3!!)
                        //webView_option3!!.setBackgroundResource(R.drawable.option_correct_curved_border)
                    } else {
                        setWrongBackground(webView_option3!!,"")
                        //webView_option3!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                    }
                }

                if (webView_option4 != null) {
                    if (webView_option4!!.url.contains("opt1", false)) {
                        setCorrectBackground(webView_option4!!)
                        //webView_option4!!.setBackgroundResource(R.drawable.option_correct_curved_border)
                    } else {
                        setWrongBackground(webView_option4!!,"")
                        //webView_option4!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                    }
                }
            }*/
        }

        btn_ok.setOnClickListener {
            btn_hint.visibility = View.VISIBLE

            if (availableLife == 0) {
                btn_next.text = "DONE"
                btn_next.visibility = View.VISIBLE
                //TODO opacity;
                setInactiveBackground2()
                /*if (webView_option1 != null) {
                    Log.d("web1", "true")
                    if (webView_option1!!.url.contains("opt1", false)) {
                        setCorrectBackground(webView_option1!!)
                    } else {
                        setWrongBackground(webView_option1!!, "")
                    }
                }

                if (webView_option2 != null) {
                    Log.d("web2", "true")
                    if (webView_option2!!.url.contains("opt1", false)) {
                        setCorrectBackground(webView_option2!!)
                        //webView_option2!!.setBackgroundResource(R.drawable.option_correct_curved_border)
                    } else {
                        setWrongBackground(webView_option2!!, "")
                        //webView_option2!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                    }
                }

                if (webView_option3 != null) {
                    if (webView_option3!!.url.contains("opt1", false)) {
                        setCorrectBackground(webView_option3!!)
                        //webView_option3!!.setBackgroundResource(R.drawable.option_correct_curved_border)
                    } else {
                        setWrongBackground(webView_option3!!, "")
                        //webView_option3!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                    }
                }

                if (webView_option4 != null) {
                    if (webView_option4!!.url.contains("opt1", false)) {
                        setCorrectBackground(webView_option4!!)
                        //webView_option4!!.setBackgroundResource(R.drawable.option_correct_curved_border)
                    } else {
                        setWrongBackground(webView_option4!!, "")
                        //webView_option4!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                    }
                }*/
            }
            alertDialog.dismiss()
        }

        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()
        // }
        //}
        /*else {
            Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show()
        }*/
    }

    private fun onBtnNext() {
        if (!isLifeZero) {
            if (reviewModel != null) {
                reviewData()
                reviewModelList.add(reviewModel!!)
                reviewModel = null
            }
            if (isAnswerCorrect) {
                createPath()
            } else {
                Toast.makeText(this, "Please select right option.", Toast.LENGTH_LONG).show()
            }
        } else {

            if (reviewModel != null) {
                reviewData()
                reviewModelList.add(reviewModel!!)
                reviewModel = null
            }
            navigateToSummaryScreen(false)
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun createArrayMapList(path: String) {
        val gsonFile = Gson()
        val questionResponseModel = gsonFile.fromJson(path, TopicOneBasicResponseModel::class.java)
        val questionsItems = questionResponseModel.questions
        val listWithDuplicateKeys = ArrayList<String>()

        totalQuestion = questionResponseModel.questionCount
        var questionItemList: ArrayList<TopicOneQuestionsItem>
        arrayMap = ArrayMap()

        for (questionItem in questionsItems) {
            val questionBank = questionItem.bank
            listWithDuplicateKeys.add(questionBank)
            if (!arrayMap!!.contains(questionBank)) {
                questionItemList = ArrayList()
                questionItemList.add(questionItem)
                arrayMap!![questionBank] = questionItemList
            } else {
                questionItemList = arrayMap!![questionBank] as ArrayList<TopicOneQuestionsItem>
                questionItemList.add(questionItem)
            }
        }
        val setWithUniqueValues = HashSet(listWithDuplicateKeys)
        listWithUniqueString = ArrayList(setWithUniqueValues)

        Collections.sort(listWithUniqueString)
        for (strings in listWithUniqueString!!) {
            Log.e("unique", strings)
        }
        listSize = arrayMap!!.size
        if (questionResponseModel.questionCount == arrayMap!!.size) {
            Log.e("is list Correct", "yes")
        }
        if (questionResponseModel.questionCount > 4) {
            iv_life3.visibility = View.VISIBLE
            availableLife = 3
            totalLife = 3
        } else {
            iv_life3.visibility = View.GONE
            availableLife = 2
            totalLife = 2
        }
        createPath()
    }

    private fun createPath() {
        unAnsweredList = ArrayList<Int>()
        Log.d("createPath", unAnsweredList!!.size.toString() + "!")
        unAnsweredList!!.clear()
        unAnsweredList!!.add(3)
        unAnsweredList!!.add(2)
        unAnsweredList!!.add(1)
        unAnsweredList!!.add(0)
        Log.d("createPath", unAnsweredList!!.size.toString() + "!")
        position++
        btn_hint!!.visibility = View.INVISIBLE
        quizTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                quizTimerCount++
            }

        }, 1000, 1000)
        handler.removeCallbacksAndMessages(null)
        if (child != null) {
            ll_inflate.removeView(child!!)
        }
        if (bank != null) {
            addBankData()
            bankList.add(bank!!)
            bank = null
            dbIsHintUsed = false
            dbAttempts = 0
        }
        if (perQuizTimer != null) {
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
            perQuizTimer!!.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    perQuizTimerCount++
                }

            }, 1000, 1000)
            isOption1Wrong = false
            isOption2Wrong = false
            isOption3Wrong = false
            isOption4Wrong = false
            countInt++
            isAnswerCorrect = false
            btn_next!!.visibility = View.INVISIBLE
            val count = "$countInt of $totalQuestion"

            //tv_count.text = Utils.ofSize(count,countInt.toString().length)
            tv_count1.text = "$countInt"
            tv_count2.text = "$totalQuestion"

            val paths: String
            questionsItem = arrayMap!!.get(listWithUniqueString!!.get(position))
            if (questionsItem!!.size > 1) {
                randomPosition = Random.nextInt(questionsItem!!.size)
                singleQuestionsItem = questionsItem!!.get(randomPosition)
                dbQPaths = questionsItem!!.get(randomPosition).id
                dbQuestionBank = questionsItem!!.get(randomPosition).bank
                dbQLevel = questionsItem!!.get(randomPosition).level
                paths = localBlobcityPath + dbQPaths
                type = questionsItem!!.get(randomPosition).type
                loadDataInWebView(paths)
            } else {
                singleQuestionsItem = questionsItem!!.get(0)
                dbQPaths = questionsItem!!.get(0).id
                dbQuestionBank = questionsItem!!.get(0).bank
                dbQLevel = questionsItem!!.get(0).level
                paths = localBlobcityPath + dbQPaths
                type = questionsItem!!.get(0).type
                loadDataInWebView(paths)
            }
        }
        if (position >= totalQuestion!!) {
            navigateToSummaryScreen(true)
        }
    }

    private fun addBankData() {
        bank!!.answer = answerList
        bank!!.attempts = dbAttempts
        bank!!.result = isAnswerCorrect
        bank!!.timeTaken = perQuizTimerCount
        bank!!.hint = dbIsHintUsed
        bank!!.id = dbQPaths.substring(dbQPaths.length - 3)
    }

    private fun loadDataInWebView(path: String) {
        listOfOptions = ArrayList()
        Log.e("testPath", path)
        var questionPath = ""
        //Log.d("list", "!" + listAssetFiles(path, applicationContext))
        for (filename in listAssetFiles(path, applicationContext)!!) {
            if (filename.contains("opt")) {
                if (!filename.contains("opt5")) {
                    listOfOptions!!.add(filename)
                }
            }
            if (filename.contains("question")) {
                questionPath = WEBVIEW_PATH + path + "/" + filename
            }
            if (filename.contains("hint")) {
                hintPath = WEBVIEW_PATH + path + "/" + filename
            }
        }

        if (type == 4100) {
            Log.d("type", "4100")
            inflateView4100()
            setWebViewBGDefault()
            webViewGone()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    webViewAnimation()
                }
            }, 2500)
            webViewPathAndLoad(path, type)
        }

        if (type == 2201) {
            Log.d("type", "2201")
            inflateView2201()
            setWebViewBGDefault()
            webViewGone()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    webViewAnimation()
                }
            }, 2500)
            webViewPathAndLoad(path, type)
        }

        if (type == 2210) {
            Log.d("type", "2210")
            inflateView2210()
            setWebViewBGDefault()
            webViewGone()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    webViewAnimation()
                }
            }, 2500)
            webViewPathAndLoad(path, type)
        }

        if (type == 2100) {
            Log.d("type", "2100")
            inflateView2100()
            setWebViewBGDefault()
            webView_question!!.visibility = View.GONE
            webView_option1!!.visibility = View.GONE
            webView_option2!!.visibility = View.GONE
            webView_option1_opacity!!.visibility = View.GONE
            webView_option2_opacity!!.visibility = View.GONE
            handler.postDelayed(object : Runnable {
                override fun run() {
                    Utils.transitionBack(applicationContext, webView_option1)
                    Utils.transitionBack(applicationContext, webView_option2)
                    //Utils.transitionBack(applicationContext,webView_option3)
                    //Utils.transitionBack(applicationContext,webView_option4)
                    webView_option1!!.visibility = View.VISIBLE
                    webView_option1!!.startAnimation(animationFadeIn500)
                    webView_option2!!.visibility = View.VISIBLE
                    webView_option2!!.startAnimation(animationFadeIn1000)
                }

            }, 2500)

            opt1Path = WEBVIEW_PATH + path + "/" + listOfOptions!!.get(1)
            opt2Path = WEBVIEW_PATH + path + "/" + listOfOptions!!.get(0)


            webView_option1!!.settings.javaScriptEnabled = true
            webView_option2!!.settings.javaScriptEnabled = true
            webView_option1_opacity!!.settings.javaScriptEnabled = true
            webView_option2_opacity!!.settings.javaScriptEnabled = true
            val webviewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    Log.d("onPageFinished", url + "!")
                    injectCSS(view, "AnswerQA")
                    // view!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#ffffff}</style>';")
                }

            }

            val OpacitywebviewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    Log.d("onPageFinished", url + "!")
                    injectCSS(view, "OpacityAnswerQA")
                    // view!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#ffffff}</style>';")
                }

            }

            webView_option1!!.webViewClient = webviewClient
            webView_option2!!.webViewClient = webviewClient
            webView_option1_opacity!!.webViewClient = OpacitywebviewClient
            webView_option2_opacity!!.webViewClient = OpacitywebviewClient

            if (Utils.jsoupWrapper(path + "/" + listOfOptions!!.get(1), this)) {
                webView_option1!!.loadUrl(opt1Path)
                webView_option1_opacity!!.loadUrl(opt1Path)
                webView_option2_opacity!!.loadUrl(opt2Path)
                webView_option2!!.loadUrl(opt2Path)
            } else {
                webView_option1!!.loadUrl(opt2Path)
                webView_option2!!.loadUrl(opt1Path)
                webView_option2_opacity!!.loadUrl(opt1Path)
                webView_option1_opacity!!.loadUrl(opt2Path)
            }

        }
        webView_question!!.setBackgroundColor(0)
        //setWebViewBGDefault()

        val qa = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("onPageFinished", url + "!")
                injectCSS(view, "QA")
                //view!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#ffffff}</style>';")
            }

            /*  override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                  Log.d("loadurl@question",questionPath+"!"+url)
                  //view?.loadUrl(questionPath)
                  view!!.loadUrl(url)
                  return false
              }*/

        }
        handler.postDelayed(object : Runnable {
            override fun run() {
                webView_question!!.visibility = View.VISIBLE
                webView_question!!.startAnimation(animationFadeIn500)
            }
        }, 500)


        if (type != 2210) {
            webView_question!!.settings.javaScriptEnabled = true
            webView_question!!.webViewClient = qa

        } /*else {
            var html = Utils.jsoup2210(questionPath, applicationContext)
            //webView_question!!.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            //webView_question!!.loadData(html, "text/html", "UTF-8");
        }*/
        webView_question!!.loadUrl(questionPath)


    }

    private fun webViewGone() {
        webView_question!!.visibility = View.GONE
        webView_option1!!.visibility = View.GONE
        webView_option2!!.visibility = View.GONE
        webView_option3!!.visibility = View.GONE
        webView_option4!!.visibility = View.GONE
        webView_option1_opacity!!.visibility = View.GONE
        webView_option2_opacity!!.visibility = View.GONE
        webView_option3_opacity!!.visibility = View.GONE
        webView_option4_opacity!!.visibility = View.GONE

    }

    private fun webViewAnimation() {
        Utils.transitionBack(applicationContext, webView_option1)
        Utils.transitionBack(applicationContext, webView_option2)
        Utils.transitionBack(applicationContext, webView_option3)
        Utils.transitionBack(applicationContext, webView_option4)
        webView_option1!!.visibility = View.VISIBLE
        webView_option1!!.startAnimation(animationFadeIn500)
        webView_option2!!.visibility = View.VISIBLE
        webView_option2!!.startAnimation(animationFadeIn1000)
        webView_option3!!.visibility = View.VISIBLE
        webView_option3!!.startAnimation(animationFadeIn1500)
        webView_option4!!.visibility = View.VISIBLE
        webView_option4!!.startAnimation(animationFadeIn)
    }

    private fun webViewPathAndLoad(path: String, type: Int) {
        Collections.shuffle(listOfOptions!!)
        opt1Path = WEBVIEW_PATH + path + "/" + listOfOptions!!.get(0)
        opt2Path = WEBVIEW_PATH + path + "/" + listOfOptions!!.get(1)
        opt3Path = WEBVIEW_PATH + path + "/" + listOfOptions!!.get(2)
        opt4Path = WEBVIEW_PATH + path + "/" + listOfOptions!!.get(3)
        Log.d("webViewPathAndLoad", opt1Path + " ! " + opt2Path)


        webView_option1!!.settings.javaScriptEnabled = true
        webView_option2!!.settings.javaScriptEnabled = true
        webView_option3!!.settings.javaScriptEnabled = true
        webView_option4!!.settings.javaScriptEnabled = true
        webView_option1_opacity!!.settings.javaScriptEnabled = true
        webView_option2_opacity!!.settings.javaScriptEnabled = true
        webView_option3_opacity!!.settings.javaScriptEnabled = true
        webView_option4_opacity!!.settings.javaScriptEnabled = true

        val webview = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("onPageFinished", url + "!")
                injectCSS(view, "AnswerQA")
                //view!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#cdcdcd}</style>';")
            }
        }
        val webviewopacity = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("onPageFinished", url + "!")
                injectCSS(view, "OpacityAnswerQA")
                //view!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#000000}</style>';")
            }
        }
        if (type != 2201) {
            webView_option2!!.webViewClient = webview
            webView_option3!!.webViewClient = webview
            webView_option4!!.webViewClient = webview
            webView_option1!!.webViewClient = webview
            webView_option1_opacity!!.webViewClient = webviewopacity
            webView_option2_opacity!!.webViewClient = webviewopacity
            webView_option3_opacity!!.webViewClient = webviewopacity
            webView_option4_opacity!!.webViewClient = webviewopacity

        }
        /*webView_option1!!.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                //super.onPageFinished(view, url)
                Log.d("onPageFinished",url+"!")
                injectCSS(view)
                webView_option1!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#ffffff}</style>';")

            }

           *//* override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.d("loadurl",opt1Path+"!"+url)
                //view?.loadUrl(opt1Path)
                webView_option1!!.loadUrl(url)
                return false
            }*//*
        }*/

        webView_option1_opacity!!.loadUrl(opt1Path)
        webView_option2_opacity!!.loadUrl(opt2Path)
        webView_option3_opacity!!.loadUrl(opt3Path)
        webView_option4_opacity!!.loadUrl(opt4Path)
        webView_option1!!.loadUrl(opt1Path)
        webView_option2!!.loadUrl(opt2Path)
        webView_option3!!.loadUrl(opt3Path)
        webView_option4!!.loadUrl(opt4Path)
        // Log.d("url1",webView_option1!!.url+"!")


    }

    private fun injectCSS(webview: WebView?, type: String) {
        try {
            var cssType: String? = ""
            if (type.equals("AnswerQA")) {
                cssType = "iPhone/AnswerQA.css"
            } else if (type.equals("QA")) {
                cssType = "iPhone/QA.css"
            } else if (type.equals("OpacityAnswerQA")) {
                cssType = "iPhone/OpacityAnswerQA.css"
            }else {
                cssType = "iPhone/Hint.css"
            }
            Log.d("cssType", cssType + "!")
            //cssType = "iPhone/AnswerQA.css"
            var inputStream = getAssets().open(cssType);
            val buffer = ByteArray(inputStream.available())
            inputStream.read(buffer)
            inputStream.close()
            val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            webview!!.loadUrl(
                "javascript:(function() {" +
                        "var parent = document.getElementsByTagName('head').item(0);" +
                        "var style = document.createElement('style');" +
                        "style.type = 'text/css';" +
                        // Tell the browser to BASE64-decode the string into your script !!!
                        "style.innerHTML = window.atob('" + encoded + "');" +
                        "parent.appendChild(style)" +
                        "})()"
            );
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    private fun inflateView4100() {
        child = layoutInflater.inflate(R.layout.webview_4100_layout, null)
        ll_inflate.addView(child)
        initializeView(child!!)
    }

    private fun inflateView2201() {
        child = layoutInflater.inflate(R.layout.webview_2201_layout, null)
        ll_inflate.addView(child)
        initializeView(child!!)
    }

    private fun inflateView2100() {
        child = layoutInflater.inflate(R.layout.webview_2100_layout, null)
        ll_inflate.addView(child)
        initializeView(child!!)
    }

    private fun inflateView2210() {
        child = layoutInflater.inflate(R.layout.webview_2210_layout, null)
        ll_inflate.addView(child)
        initializeView(child!!)
    }

    private fun setWebViewBGDefault() {
        Log.d("setWebViewBGDefault", "true")

        webView_option1!!.setBackgroundResource(R.drawable.option_curved_border)
        webView_option2!!.setBackgroundResource(R.drawable.option_curved_border)
        if (webView_option3 != null) {
            webView_option3!!.setBackgroundResource(R.drawable.option_curved_border)
            webView_option3!!.setBackgroundColor(0x00000000)
            webView_option3_opacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
            webView_option3_opacity!!.setBackgroundColor(0x00000000)
        }
        if (webView_option4 != null) {
            webView_option4!!.setBackgroundResource(R.drawable.option_curved_border)
            webView_option4!!.setBackgroundColor(0x00000000)
            webView_option4_opacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
            webView_option4_opacity!!.setBackgroundColor(0x00000000)
        }

        webView_option1!!.setBackgroundColor(0x00000000)
        webView_option2!!.setBackgroundColor(0x00000000)
        webView_option1_opacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
        webView_option1_opacity!!.setBackgroundColor(0x00000000)
        webView_option2_opacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
        webView_option2_opacity!!.setBackgroundColor(0x00000000)



        /*val tran = webView_option1!!.background as GradientDrawable
        tran.color =applicationContext.resources.getColor(R.color.purple_opt_bg)*/

    }

    private fun checkAnswer(optionClicked: Int, answer: String) {
        //btn_hint!!.visibility = View.VISIBLE
        if (!isLifeZero) {
            if (!isAnswerCorrect) {
                Log.d("2", "!" + isAnswerCorrect.toString())
                if (position == 0) {
                    isFirstAnswerGiven = true
                }
                dbAttempts++
                if (listOfOptions!!.size > 2) {
                    Log.d("listOfOptions", "!" + listOfOptions!!.size.toString())
                    if (listOfOptions!!.get(optionClicked).contains("opt1")) {
                        if (position + 1 == totalQuestion) {
                            isLevelCompleted = true
                        }

                        isAnswerCorrect = true
                        isDbCorrectAnswer = "true"
                        checkWebView(optionClicked, isAnswerCorrect)
                        dbAnswer = "opt1"
                        if (isLevelCompleted) {
                            btn_next.text = "DONE"
                        } else {
                            btn_next.text = "NEXT"
                        }
                        btn_next!!.visibility = View.VISIBLE
                        btn_hint!!.visibility = View.VISIBLE
                        //tv_try_again!!.visibility = View.GONE
                    } else {
                        if (listOfOptions!!.get(optionClicked).contains("opt2")) {
                            dbAnswer = "opt2"
                        }
                        if (listOfOptions!!.get(optionClicked).contains("opt3")) {
                            dbAnswer = "opt3"
                        }
                        if (listOfOptions!!.get(optionClicked).contains("opt4")) {
                            dbAnswer = "opt4"
                        }
                        isAnswerCorrect = false
                        checkWebView(optionClicked, isAnswerCorrect)
                        isDbCorrectAnswer = "false"
                        //btn_hint!!.visibility = View.VISIBLE
                        //tv_try_again!!.visibility = View.VISIBLE
                        availableLife--
                        checkLife(availableLife)

                    }
                } else {
                    if (questionsItem!!.size > 1) {
                        if (answer.equals(questionsItem!!.get(randomPosition).text, true)) {
                            isAnswerCorrect = true
                            isDbCorrectAnswer = "true"
                            if (position + 1 == totalQuestion) {
                                isLevelCompleted = true
                            }
                            checkWebView(optionClicked, isAnswerCorrect)
                            dbAnswer = answer
                            if (isLevelCompleted) {
                                btn_next.text = "DONE"
                            } else {
                                btn_next.text = "NEXT"
                            }
                            btn_next!!.visibility = View.VISIBLE
                            btn_hint!!.visibility = View.VISIBLE
                            //tv_try_again!!.visibility = View.GONE
                        } else {
                            isAnswerCorrect = false
                            isDbCorrectAnswer = "false"
                            checkWebView(optionClicked, isAnswerCorrect)
                            dbAnswer = answer
                            //btn_hint!!.visibility = View.VISIBLE
                            //tv_try_again!!.visibility = View.VISIBLE
                            availableLife--
                            checkLife(availableLife)
                            //wrongAnswerAlertDialog()
                        }
                    } else {
                        if (answer.equals(questionsItem!!.get(0).text, true)) {
                            isAnswerCorrect = true
                            isDbCorrectAnswer = "true"
                            if (position + 1 == totalQuestion) {
                                isLevelCompleted = true
                            }
                            checkWebView(optionClicked, isAnswerCorrect)
                            dbAnswer = answer
                            if (isLevelCompleted) {
                                btn_next.text = "DONE"
                            } else {
                                btn_next.text = "NEXT"
                            }
                            btn_next!!.visibility = View.VISIBLE
                            btn_hint!!.visibility = View.VISIBLE
                            //tv_try_again!!.visibility = View.GONE
                        } else {
                            isAnswerCorrect = false
                            isDbCorrectAnswer = "false"
                            checkWebView(optionClicked, isAnswerCorrect)
                            dbAnswer = answer
                            //btn_hint!!.visibility = View.VISIBLE
                            //tv_try_again!!.visibility = View.VISIBLE
                            availableLife--
                            checkLife(availableLife)
                            //wrongAnswerAlertDialog()
                        }
                    }
                }
                answerList!!.add(dbAnswer)
                /*addTrackDataInDb("answer")*/
            }
        } /*else {
            Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show()
        }*/
    }

    private fun checkWebView(optionClicked: Int, isRightAnswer: Boolean) {
        val optionsWithAnswer = OptionsWithAnswer()
        unAnsweredList!!.remove(optionClicked)
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
            setInactiveBackground()
        } else {
            if (optionClicked == 0) {
                isOption1Wrong = true
                optionsWithAnswer!!.option = 0
                optionsWithAnswer!!.istrue = false
                optionsWithAnswerList!!.add(optionsWithAnswer!!)
                setWrongBackground(webView_option1!!, opt1Path, webView_option1_opacity!!)
            }
            if (optionClicked == 1) {
                isOption2Wrong = true
                optionsWithAnswer!!.option = 1
                optionsWithAnswer!!.istrue = false
                optionsWithAnswerList!!.add(optionsWithAnswer!!)
                setWrongBackground(webView_option2!!, opt2Path, webView_option2_opacity!!)
            }
            if (optionClicked == 2) {
                isOption3Wrong = true
                optionsWithAnswer!!.option = 2
                optionsWithAnswer!!.istrue = false
                optionsWithAnswerList!!.add(optionsWithAnswer!!)
                setWrongBackground(webView_option3!!, opt3Path, webView_option3_opacity!!)
            }
            if (optionClicked == 3) {
                isOption4Wrong = true
                optionsWithAnswer!!.option = 3
                optionsWithAnswer!!.istrue = false
                optionsWithAnswerList!!.add(optionsWithAnswer!!)
                setWrongBackground(webView_option4!!, opt4Path, webView_option4_opacity!!)
            }
        }
    }

    private fun setInactiveBackground() {
        for (i in unAnsweredList!!) {
            if (i == 0) {
                //webView_option1!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                webView_option1!!.visibility = View.GONE
                webView_option1_opacity!!.visibility = View.VISIBLE
            } else if (i == 1) {
                //webView_option2!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                webView_option2!!.visibility = View.GONE
                webView_option2_opacity!!.visibility = View.VISIBLE
            } else if (i == 2) {
                if (webView_option3 != null) {
                    webView_option3!!.visibility = View.GONE
                    webView_option3_opacity!!.visibility = View.VISIBLE
                    //webView_option3!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                }

            } else if (i == 3) {
                if (webView_option4 != null) {
                    webView_option4!!.visibility = View.GONE
                    webView_option4_opacity!!.visibility = View.VISIBLE
                    //webView_option4!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                }
            }
        }
    }

    private fun setInactiveBackground2() {
        for (i in unAnsweredList!!) {
            if (i == 0) {

                Log.d("web1inactive", "true")
                if (webView_option1!!.url.contains("opt1", false)) {
                    setCorrectBackground(webView_option1!!)
                } else {
                    setWrongBackground(webView_option1!!, "",webView_option1_opacity!!)
                }

            } else if (i == 1) {
                Log.d("web2inactive", "true")
                if (webView_option2!!.url.contains("opt1", false)) {
                    setCorrectBackground(webView_option2!!)
                } else {
                    setWrongBackground(webView_option2!!, "",webView_option2_opacity!!)
                }
            } else if (i == 2) {
                Log.d("web3inactive", "true")
                if (webView_option3 != null) {
                    Log.d("web3", "true")
                    if (webView_option3!!.url.contains("opt1", false)) {
                        setCorrectBackground(webView_option3!!)
                    } else {
                        setWrongBackground(webView_option3!!, "",webView_option3_opacity!!)
                    }
                }

            } else if (i == 3) {
                Log.d("web4inactive", "true")
                if (webView_option4 != null) {
                    Log.d("web4", "true")
                    if (webView_option4!!.url.contains("opt1", false)) {
                        setCorrectBackground(webView_option4!!)
                    } else {
                        setWrongBackground(webView_option4!!, "",webView_option4_opacity!!)
                    }
                }
            }
        }
    }

    private fun setCorrectBackground(webView: WebView) {
        Log.d("setCurrentBackground", webView.url + "!")
        handler.postDelayed(object : Runnable {
            override fun run() {
                isHandlerExecuted = true
                webView.setBackgroundResource(R.drawable.option_correct_curved_border)
            }

        }, 1500)
        //webView.setBackgroundResource(R.drawable.option_correct_green_border)
        Utils.transition(applicationContext, webView, true)
        //webView!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#cdcdcd}</style>';")

    }

    private fun setWrongBackground(webViewReal: WebView, path: String?, webViewOpacity: WebView) {
        /*val trans = webView.background as TransitionDrawable
        trans.startTransition(5000)*/
        handler.postDelayed(object : Runnable {
            override fun run() {
                isHandlerExecuted = true
                webViewReal.visibility = View.GONE
                webViewOpacity.visibility = View.VISIBLE
                //webView.setBackgroundResource(R.drawable.inactive_answer_overlay)
                if (path!!.length != 0) {
                    wrongAnswerAlertDialog()
                }

            }

        }, 1500)
        Utils.transition(applicationContext, webViewReal, false)

        /*val webviewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("onPageFinished", url + "!")
                injectCSS(view, "AnswerQA")
                view!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#000000}</style>';")
            }

        }
        webView.webViewClient = webviewClient
        webView.loadUrl(path)*/

        //webView.setBackgroundResource(R.drawable.option_wrong_red)
    }

    private fun checkLife(life: Int) {
        val blinkAnim = AnimationUtils.loadAnimation(this, R.anim.blink_anim)
        val animationSet = AnimationSet(false)
        animationSet.addAnimation(blinkAnim)
        if (totalLife == 3) {
            if (life == 2) {
                /*iv_life3.startAnimation(animationSet)
                animationSet.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(animation: Animation?) {

                    }

                    override fun onAnimationEnd(animation: Animation?) {
                        Glide.with(this@TestQuestionActivity)
                            .load(R.drawable.inactive_heart)
                            .into(iv_life3)
                    }

                    override fun onAnimationStart(animation: Animation?) {

                    }
                })*/
                Glide.with(this@TestQuestionActivity)
                    .load(R.drawable.inactive_heart)
                    .into(iv_life3)
            }
        }
        if (life == 1) {
            /* iv_life2.startAnimation(animationSet)
             animationSet.setAnimationListener(object : Animation.AnimationListener {
                 override fun onAnimationRepeat(animation: Animation?) {

                 }

                 override fun onAnimationEnd(animation: Animation?) {
                     Glide.with(this@TestQuestionActivity)
                         .load(com.blobcity.R.drawable.inactive_heart)
                         .into(iv_life2)
                 }

                 override fun onAnimationStart(animation: Animation?) {

                 }
             })*/
            Glide.with(this@TestQuestionActivity)
                .load(com.blobcity.R.drawable.inactive_heart)
                .into(iv_life2)

        }

        if (life == 0) {
            /*iv_life1.startAnimation(animationSet)
            animationSet.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    Glide.with(this@TestQuestionActivity)
                        .load(com.blobcity.R.drawable.inactive_heart)
                        .into(iv_life1)
                }

                override fun onAnimationStart(animation: Animation?) {

                }
            })*/
            Glide.with(this@TestQuestionActivity)
                .load(com.blobcity.R.drawable.inactive_heart)
                .into(iv_life1)
            isLifeZero = true
            /*btn_next.text = "DONE"
            btn_next.visibility = View.VISIBLE*/
            /*navigateToSummaryScreen(false)*/
        }
    }

    private fun navigateToSummaryScreen(isLevelCompleted: Boolean) {
        quizTimer!!.cancel()
        if (isLevelCompleted) {
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
        intent.putExtra(IS_LEVEL_COMPLETE, isLevelCompleted)

        intent.putExtra(DYNAMIC_PATH, dynamicPath)
        intent.putExtra(COURSE_ID, courseId)
        intent.putExtra(COURSE_NAME, courseName)
        intent.putExtra(LEVEL_COMPLETED, complete)
        intent.putExtra(FOLDER_PATH, oPath)
        intent.putExtra(FOLDER_NAME, folderName)
        intent.putExtra(TITLE_TOPIC, gradeTitle!!)
        startActivity(intent)
        finish()
    }

    private fun addAllDataInDb(isLevelCompleted: Boolean, quit: Boolean) {
        val pinfo: PackageInfo = getPackageManager().getPackageInfo(getPackageName(), 0)
        val versionNumber: Int = pinfo.versionCode
        val quiz = Quiz()
        quiz.osv = android.os.Build.VERSION.SDK_INT
        quiz.timeTaken = quizTimerCount
        quiz.topicId = topicId
        quiz.quizSessionId = UUID.randomUUID().toString()
        quiz.topicName = topicName
        quiz.quit = quit
        quiz.uId = UniqueUUid.id(this)
        quiz.timeStamp = System.currentTimeMillis() / 1000
        quiz.result = isLevelCompleted
        quiz.os = "android"
        quiz.courseId = courseId
        quiz.courseName = courseName
        quiz.completed = isLevelCompleted
        quiz.appversion = versionNumber
        quiz.quizType = topicLevel
        val bankHashList: HashMap<String, Bank> = HashMap()
        if (bankList.size > 0) {
            bankList.forEachIndexed { index, bank ->
                bankHashList.put("b" + (index + 1), bank)
            }
        }
        quiz.quizSession = bankHashList

        firestore!!.collection("quiz")
            .add(quiz)
            .addOnCompleteListener(object : OnCompleteListener<DocumentReference> {
                override fun onComplete(task: Task<DocumentReference>) {
                    if (task.isSuccessful) {
                        Log.e("quizAddStatus", "quiz added successfully")
                    } else {
                        Log.e("quizAddStatus", task.exception.toString())
                    }
                }
            })
    }

    override fun onBackPressed() {
        if (isLevelCompleted) {
            addAllDataInDb(true, false)
            addDataInDb()
            super.onBackPressed()
        } else {
            backPressDialog()
        }
    }

    private fun addDataInDb() {
        val uId: String = UniqueUUid.id(this)
        topicStatusVM!!.insert(
            courseId!!, uId,
            topicId!!, topicLevel!!, dbPosition!!, gradeTitle!!
        )
    }

    private fun backPressDialog() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.mytheme)
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

        /*val map = takeScreenShot(this);

        val fast = fastblur(map, 10);
        val draw = BitmapDrawable(getResources(), fast);*/
        tv_return.setOnClickListener {
            alertDialog.dismiss()
        }
        //alertDialog.getWindow().setBackgroundDrawable(draw);
        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()
    }

    private fun reviewData() {
        reviewModel!!.questionsItem = singleQuestionsItem
        reviewModel!!.optionsWithAnswerList = optionsWithAnswerList
        reviewModel!!.listOfOptions = listOfOptions
    }

    fun heartType(context: Context, type:Boolean, view: ImageView){
        if(type){
            Glide.with(context)
                .load(R.drawable.active_heart_copy)
                .into(view)
        }else{
            Glide.with(context)
                .load(R.drawable.inactive_heart)
                .into(view)
        }

    }
}
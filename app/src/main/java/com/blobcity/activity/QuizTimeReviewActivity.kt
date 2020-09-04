package com.blobcity.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat

import android.text.Html
import android.util.Base64
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.blobcity.R
import com.blobcity.adapter.ReviewAdapter
import com.blobcity.database.QuizGameDataBase
import com.blobcity.model.ReviewModel
import com.blobcity.model.Topic
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.RecyclerViewPositionHelper
import com.blobcity.utils.SharedPrefs
import com.blobcity.utils.Utils
import com.blobcity.utils.Utils.getListOfFilesFromFolder
import com.blobcity.utils.Utils.listAssetFiles
import com.bumptech.glide.Glide

import kotlinx.android.synthetic.main.activity_quiz_summary.*
import kotlinx.android.synthetic.main.activity_quiz_time_review.*
import kotlinx.android.synthetic.main.activity_review.*
import kotlinx.android.synthetic.main.activity_review.btn_close
import kotlinx.android.synthetic.main.activity_review.btn_hint
import kotlinx.android.synthetic.main.activity_review.btn_next
import kotlinx.android.synthetic.main.activity_review.hint_btn
import kotlinx.android.synthetic.main.activity_review.iv_cancel_test_question
import kotlinx.android.synthetic.main.activity_review.left_arrow
import kotlinx.android.synthetic.main.activity_review.ll_dots
import kotlinx.android.synthetic.main.activity_review.ll_inflate
import kotlinx.android.synthetic.main.activity_review.next_btn
import kotlinx.android.synthetic.main.activity_review.prev_btn
import kotlinx.android.synthetic.main.activity_review.right_arrow
import kotlinx.android.synthetic.main.activity_review.txt_next
import kotlinx.android.synthetic.main.activity_review.txt_prev
import kotlinx.android.synthetic.main.activity_review.webview_hint
import kotlinx.android.synthetic.main.activity_test_quiz.*
import java.io.File
import java.lang.Exception
import java.net.URLDecoder
import java.util.*
import kotlin.collections.ArrayList


class QuizTimeReviewActivity : BaseActivity(), View.OnClickListener {


    var res: String = ""
    var item: Int ?= null

    private var hintPath: String? = ""
    private var opt1Path: String? = ""
    private var opt2Path: String? = ""
    private var opt3Path: String? = ""
    private var opt4Path: String? = ""
    var context: Context ? = null
    var adapter : ReviewAdapter? = null
    var topicLevel: String? = ""
    var dynamicPath: String? = null
    var folderName: String? = null
    var gradeTitle: String? = null
    var courseId: String? = ""
    var topicId: String? = ""

    var complete: String? = ""

    var dbPosition: Int? = null
    var courseName: String? = ""
    var topicName: String? = ""
    var oPath: String? = null
    var readyCardNumber = 0
    private var countInt: Int = 0
    var type: Int = 0
    private var totalQuestion: Int? = null
    val handler = Handler()
    var isOption1Wrong = false
    var isOption2Wrong = false
    var isOption3Wrong = false
    var isOption4Wrong = false
    var webView_question: WebView? = null
    var webView_option1: WebView? = null
    var webView_option2: WebView? = null
    var webView_option3: WebView? = null
    var webView_option4: WebView? = null
    var webView_option1_opacity: WebView? = null
    var webView_option2_opacity: WebView? = null
    var webView_option3_opacity: WebView? = null
    var webView_option4_opacity: WebView? = null

    var child: View? = null
    private var listOfOptions: ArrayList<String>? = null
    lateinit var circles: Array<ImageView?>
    var unAnsweredList: ArrayList<Int>? = null
    var databaseHandler: QuizGameDataBase?= null
    var dialog: Dialog? = null;
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    var title:String = ""
    var lastplayed:String = ""
    var playeddate:String = ""

    var animationFadeIn: Animation? = null
    var animationFadeIn1500: Animation? = null
    var animationFadeIn1000: Animation? = null
    var animationFadeIn500: Animation? = null
    var mLastClickTime:Long = 0;
    override var layoutID: Int = R.layout.activity_quiz_time_review

    override fun initView() {
       // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorbottomnav));
        }

        //topic = intent.getSerializableExtra(TOPIC) as Topic
        /*dynamicPath = intent.getStringExtra(DYNAMIC_PATH)
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
        readyCardNumber = intent.getIntExtra(CARD_NO, -1)*/
        totalQuestion = intent.getIntExtra(ConstantPath.QUIZ_COUNT, 0)


        title = intent.getStringExtra("title")
        lastplayed = intent.getStringExtra("lastplayed")
        playeddate = intent.getStringExtra("playeddate")


        sharedPrefs = SharedPrefs()
        unAnsweredList = ArrayList<Int>()
        //createArrayMapList(dynamicPath!!)
        databaseHandler = QuizGameDataBase(this);

        animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_700)
        animationFadeIn1500 = AnimationUtils.loadAnimation(this, R.anim.fade_in_500)
        animationFadeIn1000 = AnimationUtils.loadAnimation(this, R.anim.fade_in_300)
        animationFadeIn500 = AnimationUtils.loadAnimation(this, R.anim.fade_in_100)

        createPath()

    }

    private fun createPath() {
        unAnsweredList!!.clear()
        unAnsweredList!!.add(3)
        unAnsweredList!!.add(2)
        unAnsweredList!!.add(1)
        unAnsweredList!!.add(0)
        Log.d("createPath", unAnsweredList!!.size.toString() + "!")
        //position++
        handler.removeCallbacksAndMessages(null)
        if (child != null) {
            ll_inflate.removeView(child!!)
        }
        countInt++
        addDot(countInt, totalQuestion!!)
        val paths: String = databaseHandler!!.getQuizQuestionPathFinal(title,playeddate,lastplayed)
        var ans:List<String> = paths.split(",")
        var ans1:List<String> = ans.get((countInt - 1)).split("~")
        type = ans1[1].toInt()
        loadDataInWebView(ans1[0])

    }

    private fun createPathForPrev() {
        unAnsweredList!!.clear()
        unAnsweredList!!.add(3)
        unAnsweredList!!.add(2)
        unAnsweredList!!.add(1)
        unAnsweredList!!.add(0)
        Log.d("createPath", unAnsweredList!!.size.toString() + "!")
        //position++
        handler.removeCallbacksAndMessages(null)
        if (child != null) {
            ll_inflate.removeView(child!!)
        }
        countInt--
        addDot(countInt, totalQuestion!!)
        val paths: String = databaseHandler!!.getQuizQuestionPathFinal(title,playeddate,lastplayed)
        var ans:List<String> = paths.split(",")
        var ans1:List<String> = ans.get((countInt - 1)).split("~")
        type = ans1[1].toInt()
        loadDataInWebView(ans1[0])

    }

    fun addDot(countint:Int,totalquestions:Int) {
        //val layout_dot = findViewById(R.id.ll_dots) as LinearLayout
        circles = arrayOfNulls<ImageView>(totalquestions)
        ll_dots.removeAllViews()

        for (i in 0 until circles!!.size)
        {
            if((i+1) == countint){
                val params = LinearLayout.LayoutParams(getResources().getDimension(R.dimen._12sdp).toInt(), getResources().getDimension(R.dimen._12sdp).toInt())
                if(i != 0){
                    params.leftMargin = getResources().getDimension(R.dimen._8sdp).toInt()
                }
                /*dot!![i] = TextView(this)
                dot!![i]?.setText(Html.fromHtml("&#9679;"))
                dot!![i]?.setTextSize(30F)
                //set default dot color
                dot!![i]?.setTextColor(getResources().getColor(R.color.button_close_text))
                dot[i]!!.gravity = Gravity.CENTER
                dot[i]!!.layoutParams = params*/



                circles[i] = ImageView(this)

                Glide.with(this)
                    .load(R.drawable.question_fill_circle)
                    .into(circles[i]!!)
                //ImageViewCompat.setImageTintList(circles[i]!!, ColorStateList.valueOf(getResources().getColor(R.color.right_tick)));
                circles[i]!!.layoutParams = params


                ll_dots.addView(circles!![i])
            }else{
                val params = LinearLayout.LayoutParams(getResources().getDimension(R.dimen._12sdp).toInt(), getResources().getDimension(R.dimen._12sdp).toInt())
                //val params = LinearLayout.LayoutParams(getResources().getDimension(R.dimen._15sdp).toInt(), getResources().getDimension(R.dimen._30sdp).toInt())
                if(i != 0){
                    params.leftMargin = getResources().getDimension(R.dimen._8sdp).toInt()
                }
                /*dot!![i] = TextView(this)
                dot!![i]?.setText(Html.fromHtml("&#9675;"))
                dot!![i]?.setTextSize(30F)
                // dot!![i]?.setBackgroundResource(R.color.purple)
                //set default dot color
                dot!![i]?.setTextColor(getResources().getColor(R.color.button_close_text))
                dot[i]!!.gravity = Gravity.CENTER
                dot[i]!!.layoutParams = params*/

                circles[i] = ImageView(this)

                Glide.with(this)
                    .load(R.drawable.question_white_circle)
                    .into(circles[i]!!)
                //ImageViewCompat.setImageTintList(circles[i]!!, ColorStateList.valueOf(getResources().getColor(R.color.right_tick)));
                circles[i]!!.layoutParams = params


                ll_dots.addView(circles!![i])
            }

        }
        /*for (i in 0 until dot!!.size)
        {
            if((i+1) == countint){
                val params = LinearLayout.LayoutParams(getResources().getDimension(R.dimen._15sdp).toInt(), getResources().getDimension(R.dimen._30sdp).toInt())
                if(i != 0){
                    params.leftMargin = getResources().getDimension(R.dimen._10sdp).toInt()
                }
                dot!![i] = TextView(this)
                dot!![i]?.setText(Html.fromHtml("&#9679;"))
                dot!![i]?.setTextSize(30F)
                //set default dot color

                dot!![i]?.setTextColor(getResources().getColor(R.color.button_close_text))
                dot[i]!!.gravity = Gravity.CENTER
                dot[i]!!.layoutParams = params

                ll_dots.addView(dot!![i])
            }else{
                val params = LinearLayout.LayoutParams(getResources().getDimension(R.dimen._15sdp).toInt(), getResources().getDimension(R.dimen._30sdp).toInt())
                if(i != 0){
                    params.leftMargin = getResources().getDimension(R.dimen._10sdp).toInt()
                }

                dot!![i] = TextView(this)
                dot!![i]?.setText(Html.fromHtml("&#9675;"))
                dot!![i]?.setTextSize(30F)
                dot[i]!!.gravity = Gravity.CENTER
                // dot!![i]?.setBackgroundResource(R.color.purple)
                //set default dot color
                dot!![i]?.setTextColor(getResources().getColor(R.color.button_close_text))
                dot[i]!!.layoutParams = params
                ll_dots.addView(dot!![i])
            }

        }*/

        if(countint == 1 && countint == totalquestions){
            next_btn.visibility = View.GONE
            prev_btn.visibility = View.GONE

            next_btn.isEnabled = false
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_inactive_btn));
            txt_next.setTextColor(getResources().getColor(R.color.submit_inactive_color))
            ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.arrow_tint)));


            prev_btn.isEnabled = false
            prev_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_inactive_btn));
            txt_prev.setTextColor(getResources().getColor(R.color.submit_inactive_color))
            ImageViewCompat.setImageTintList(left_arrow, ColorStateList.valueOf(getResources().getColor(R.color.arrow_tint)));

        }else if(countint == 1 && countint != totalquestions){
            next_btn.visibility = View.VISIBLE
            prev_btn.visibility = View.GONE
            next_btn.isEnabled = true
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_active_btn));
            txt_next.setTextColor(getResources().getColor(R.color.button_close_text))
            ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.button_close_text)));
            /*next_btn.isEnabled = false
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_inactive_btn));
            txt_next.setTextColor(getResources().getColor(R.color.submit_inactive_color))
            ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.arrow_tint)));

*/

            prev_btn.isEnabled = false
            prev_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_inactive_btn));
            txt_prev.setTextColor(getResources().getColor(R.color.submit_inactive_color))
            ImageViewCompat.setImageTintList(left_arrow, ColorStateList.valueOf(getResources().getColor(R.color.arrow_tint)));

        }else if(countint == totalquestions){
            next_btn.visibility = View.GONE
            prev_btn.visibility = View.VISIBLE
            next_btn.isEnabled = false
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_inactive_btn));
            txt_next.setTextColor(getResources().getColor(R.color.submit_inactive_color))
            ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.arrow_tint)));


            prev_btn.isEnabled = true
            prev_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_active_btn));
            txt_prev.setTextColor(getResources().getColor(R.color.button_close_text))
            ImageViewCompat.setImageTintList(left_arrow, ColorStateList.valueOf(getResources().getColor(R.color.button_close_text)));
        }else{
            next_btn.visibility = View.VISIBLE
            prev_btn.visibility = View.VISIBLE
            next_btn.isEnabled = true
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_active_btn));
            txt_next.setTextColor(getResources().getColor(R.color.button_close_text))
            ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.button_close_text)));

            prev_btn.isEnabled = true
            prev_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_active_btn));
            txt_prev.setTextColor(getResources().getColor(R.color.button_close_text))
            ImageViewCompat.setImageTintList(left_arrow, ColorStateList.valueOf(getResources().getColor(R.color.button_close_text)));
            //left_arrow.setColorFilter(null)

        }


        //set active dot color
        // dot[2]!!.setTextColor(getResources().getColor(R.color.button_close_text))
    }

    private fun loadDataInWebView(path: String) {
        listOfOptions = ArrayList()
        Log.e("test question activity","testPath................"+ path)
        var questionPath = ""
        //Log.d("list", "!" + listAssetFiles(path, applicationContext))
        /*for (filename in listAssetFiles(path, applicationContext)!!) {
            *//*if (filename.contains("opt")) {
                if (!filename.contains("opt5")) {
                    listOfOptions!!.add(filename)
                }
            }*//*
            if (filename.contains("question")) {
                questionPath = WEBVIEW_PATH + path + "/" + filename
            }
            if (filename.contains("hint")) {
                hintPath = WEBVIEW_PATH + path + "/" + filename
                //showDialog()

                webview_hint.settings.javaScriptEnabled = true
                //  webview.setVerticalScrollBarEnabled(true)
                // Enable responsive layout
                // webview.getSettings().setUseWideViewPort(true);
                // Zoom out if the content width is greater than the width of the viewport
                //webview.getSettings().setLoadWithOverviewMode(true);
                val hint = object : WebViewClient() {

                    override fun onPageFinished(view: WebView?, url: String?) {
                        Log.d("onPageFinished", url + "!")
                        injectCSS(view, "Hint")
                        // view!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#ffffff}</style>';")
                    }
                }
                Log.e("test question activity","hint alert dialog....hint path..."+hintPath);
                webview_hint.webViewClient = hint
                webview_hint.loadUrl(hintPath)
                webview_hint.setBackgroundColor(0)

            }
        }*/

        val file = File(path)
        val listFile = file.listFiles()
        if(listFile.size > 0){
            for (file1 in listFile){
                if (file1.name.contains("question")) {
                    questionPath = file1.absolutePath
                }
                if (file1.name.contains("hint")) {
                    hintPath = file1.absolutePath
                    //showDialog()
                    webview_hint.settings.javaScriptEnabled = true
                    //  webview.setVerticalScrollBarEnabled(true)
                    // Enable responsive layout
                    // webview.getSettings().setUseWideViewPort(true);
                    // Zoom out if the content width is greater than the width of the viewport
                    //webview.getSettings().setLoadWithOverviewMode(true);
                    val hint = object : WebViewClient() {

                        override fun onPageFinished(view: WebView?, url: String?) {
                            Log.d("onPageFinished", url + "!")
                            injectCSS(view, "Hint")
                            // view!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#ffffff}</style>';")
                        }
                    }
                    Log.e("test question activity","hint alert dialog....hint path..."+hintPath);
                    webview_hint.webViewClient = hint
                    webview_hint.loadUrl(WEBVIEW_FILE_PATH + hintPath)
                    webview_hint.setBackgroundColor(0)

                }

                /*if (file1.name.contains("opt")) {
                    listOfOptions!!.add(file1.absolutePath)
                }*/


            }

        }

        var optionString:String = databaseHandler!!.getQuizFinalOptions(title,playeddate,lastplayed)

        var queans:List<String> = optionString.split(",")
        var optionsmutanslist = queans.toMutableList()

        var options1:List<String> = optionsmutanslist.get((countInt - 1)).split("~")
        Log.e("review activity","options1......."+options1)
        //var options1list = options1.toMutableList()
        var options2list:List<String> = options1[1].split("#")
        for(i in 0 until options2list.size){
            listOfOptions!!.add(options2list[i])
        }

        Log.e("quiz time review","listOfOptions......."+listOfOptions)

        if (type == 4100) {
            Log.d("type", "4100")
            inflateView4100()
            setWebViewBGDefault()
            webViewGone()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    webViewAnimation()
                }
            }, 1500)
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
            }, 1500)
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
            }, 1500)
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

            /*Utils.transitionBack(applicationContext, webView_option1)
            Utils.transitionBack(applicationContext, webView_option2)
            //Utils.transitionBack(applicationContext,webView_option3)
            //Utils.transitionBack(applicationContext,webView_option4)
            webView_option1!!.visibility = View.VISIBLE
            //webView_option1!!.startAnimation(animationFadeIn500)
            webView_option2!!.visibility = View.VISIBLE*/
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

            }, 1500)

            opt1Path = WEBVIEW_FILE_PATH + listOfOptions!!.get(0)
            opt2Path = WEBVIEW_FILE_PATH + listOfOptions!!.get(1)


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

            if (Utils.jsoupWrapper(WEBVIEW_FILE_PATH + listOfOptions!!.get(0), this)) {
                webView_option1!!.loadUrl(opt1Path)
                webView_option2!!.loadUrl(opt2Path)
                webView_option1_opacity!!.loadUrl(opt1Path)
                webView_option2_opacity!!.loadUrl(opt2Path)
            } else {
                webView_option1!!.loadUrl(opt2Path)
                webView_option2!!.loadUrl(opt1Path)
                webView_option1_opacity!!.loadUrl(opt2Path)
                webView_option2_opacity!!.loadUrl(opt1Path)
            }

        }
        webView_question!!.setBackgroundColor(0)
        //setWebViewBGDefault()

        val qa = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("onPageFinished", url + "!")
                injectCSS(view, "QA")
                //view!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#ffffff}</style>';")
                //Log.d("question",view.)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                val html = URLDecoder.decode(url, "UTF-8").toString()
                Log.d("loadurl@question","!"+url+"!"+html+"!")
                //view?.loadUrl(questionPath)
                //view!!.loadUrl(url)
                return false
            }

        }
       // webView_question!!.visibility = View.VISIBLE
        handler.postDelayed(object : Runnable {
            override fun run() {
                webView_question!!.visibility = View.VISIBLE
                webView_question!!.startAnimation(animationFadeIn500)
            }
        }, 1500)


        if (type != 2210) {
            webView_question!!.settings.javaScriptEnabled = true
            webView_question!!.webViewClient = qa
        }
        /*else {
            var html = Utils.jsoup(questionPath, applicationContext)
            Log.d("html",html+"!")
            //webView_question!!.loadDataWithBaseURL("file:///android_asset/courses/Test Course/topic-one/intermediate-400/", html, "text/html", "utf-8", null)
            webView_question!!.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", questionPath)
            //webView_question!!.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
            //webView_question!!.loadData(html, "text/html", "UTF-8");
        }*/

        webView_question!!.loadUrl(WEBVIEW_FILE_PATH + questionPath)

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
            Log.d("inputStream",inputStream.toString())
            Log.d("buffer",buffer.toString())
            inputStream.read(buffer)
            inputStream.close()
            val encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            Log.d("encoded",encoded+"!")
            webview!!.loadUrl(
                "javascript:(function() {" +
                        "var parent = document.getElementsByTagName('head').item(0);" +
                        "var style = document.createElement('style');" +
                        "style.type = 'text/css';" +
                        // Tell the browser to BASE64-decode the string into your script !!!
                        "style.innerHTML = window.atob('" + encoded + "');" +
                        "parent.appendChild(style);" +
                        "console.log(style.innerHTML);"+
                        "})()"
            );
        } catch (e: Exception) {
            e.printStackTrace();
        }
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

    private fun webViewPathAndLoad(path: String, type: Int) {
       // Collections.shuffle(listOfOptions!!)
        opt1Path = WEBVIEW_FILE_PATH + listOfOptions!!.get(0)
        opt2Path = WEBVIEW_FILE_PATH + listOfOptions!!.get(1)
        opt3Path = WEBVIEW_FILE_PATH + listOfOptions!!.get(2)
        opt4Path = WEBVIEW_FILE_PATH + listOfOptions!!.get(3)
        Log.d("webViewPathAndLoad", opt1Path + " ! " + opt2Path)


        webView_option1!!.settings.javaScriptEnabled = true
        webView_option2!!.settings.javaScriptEnabled = true
        webView_option3!!.settings.javaScriptEnabled = true
        webView_option4!!.settings.javaScriptEnabled = true
        webView_option1_opacity!!.settings.javaScriptEnabled = true
        webView_option2_opacity!!.settings.javaScriptEnabled = true
        webView_option3_opacity!!.settings.javaScriptEnabled = true
        webView_option4_opacity!!.settings.javaScriptEnabled = true

        /* webView_option1.setInitialScale(1);
         x.getSettings().setLoadWithOverviewMode(true);
         x.getSettings().setUseWideViewPort(true);*/
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

        //webViewAnimation()
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


        var answers:String = databaseHandler!!.getQuizAnswerStatus(title,playeddate,lastplayed);


        var ans:List<String> = answers.split(",")
        Log.e("test question activity","next.....countInt..."+countInt);
        Log.e("test question activity","next.....ans..."+ans);

        if(ans.get((countInt - 1)).equals("1")){
            var questionanswers:String = databaseHandler!!.getQuizQuestionAnswersFinal(title,playeddate,lastplayed);
            var queans:List<String> = questionanswers.split(",")
            var ans1:List<String> = queans.get((countInt - 1)).split("~")
            Log.e("test question activity","next.....ans1..."+ans1);


            checkwebviewborder(ans1[1])


        }/*else{
            checkwebviewborder("opt1")
        }*/
    }

    private fun checkwebviewborder(answer: String){
        //Log.e("test question actvitiy","..checkwebviewborder....optionClicked..."+optionClicked)

        if (listOfOptions!!.size > 2) {
            if(answer.equals("opt1")){
                if (webView_option1!!.url.contains(answer)) {
                    Log.e("change question","check answer...0.");
                    setCorrectBackground(webView_option1!!)
                    unAnsweredList!!.remove(0)
                }
                if (webView_option2!!.url.contains(answer)) {
                    Log.e("change question","check answer...1.");
                    setCorrectBackground(webView_option2!!)
                    unAnsweredList!!.remove(1)
                }
                if (webView_option3!!.url.contains(answer)) {
                    Log.e("change question","check answer...2.");
                    setCorrectBackground(webView_option3!!)
                    unAnsweredList!!.remove(2)
                }
                if (webView_option4!!.url.contains(answer)) {
                    Log.e("change question","check answer...3..");
                    setCorrectBackground(webView_option4!!)
                    unAnsweredList!!.remove(3)
                }
                setInactiveBackgroundNew();
            }else{
                if (webView_option1!!.url.contains(answer)) {
                    Log.e("change question","check answer...0.");
                    setWrongBackground(webView_option1!!)
                    unAnsweredList!!.remove(0)
                }
                if (webView_option2!!.url.contains(answer)) {
                    Log.e("change question","check answer...1.");
                    setWrongBackground(webView_option2!!)
                    unAnsweredList!!.remove(1)
                }
                if (webView_option3!!.url.contains(answer)) {
                    Log.e("change question","check answer...2.");
                    setWrongBackground(webView_option3!!)
                    unAnsweredList!!.remove(2)
                }
                if (webView_option4!!.url.contains(answer)) {
                    Log.e("change question","check answer...3..");
                    setWrongBackground(webView_option4!!)
                    unAnsweredList!!.remove(3)
                }
                setInactiveBackgroundNew();
            }


        }else{
            if (webView_option1!!.url.contains(answer)) {
                Log.e("change question","check answer...0.");
                setCorrectBackground(webView_option1!!)
                unAnsweredList!!.remove(0)
            }
            if (webView_option2!!.url.contains(answer)) {
                Log.e("change question","check answer...1.");
                setCorrectBackground(webView_option2!!)
                unAnsweredList!!.remove(1)
            }
            //setInactiveBackgroundNew()
        }
    }

    private fun setInactiveBackgroundNew() {
        Log.e("test question activity","unAnsweredList......"+unAnsweredList!!.size);
        for (i in unAnsweredList!!) {
            Log.e("test question activity","unAnsweredList...i..."+i);
            if (i == 0) {
                if(webView_option1!!.url.contains("opt1")){
                    setCorrectBackground(webView_option1!!)
                }else{
                    webView_option1!!.setBackgroundResource(R.drawable.option_curved_border)
                    webView_option1!!.setBackgroundColor(0x00000000)
                    webView_option1_opacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                    webView_option1_opacity!!.setBackgroundColor(0x00000000)
                }

            } else if (i == 1) {
                if(webView_option2!!.url.contains("opt1")){
                    setCorrectBackground(webView_option2!!)
                }else{
                    webView_option2!!.setBackgroundResource(R.drawable.option_curved_border)
                    webView_option2!!.setBackgroundColor(0x00000000)
                    webView_option2_opacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                    webView_option2_opacity!!.setBackgroundColor(0x00000000)
                }

            } else if (i == 2) {

                if (webView_option3 != null) {
                    if(webView_option3!!.url.contains("opt1")){
                        setCorrectBackground(webView_option3!!)
                    }else{
                        webView_option3!!.setBackgroundResource(R.drawable.option_curved_border)
                        webView_option3!!.setBackgroundColor(0x00000000)
                        webView_option3_opacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                        webView_option3_opacity!!.setBackgroundColor(0x00000000)
                    }

                }

            } else if (i == 3) {
                if (webView_option4 != null) {
                    if(webView_option4!!.url.contains("opt1")){
                        setCorrectBackground(webView_option4!!)
                    }else{
                        webView_option4!!.setBackgroundResource(R.drawable.option_curved_border)
                        webView_option4!!.setBackgroundColor(0x00000000)
                        webView_option4_opacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                        webView_option4_opacity!!.setBackgroundColor(0x00000000)
                    }

                }
            }
        }
    }

    private fun setCorrectBackground(webView: WebView) {
        Log.d("setCurrentBackground", webView.url + "!")
        webView.setBackgroundResource(R.drawable.review_correct_ans_border)

    }
    private fun setWrongBackground(webView: WebView) {
        Log.d("setWrongBackground", webView.url + "!")
        webView.setBackgroundResource(R.drawable.option_wrong_red)
        //Utils.reviewtransition(applicationContext, webView, false)
    }

    /*private fun checkwebviewborder(answer: String){
        //Log.e("test question actvitiy","..checkwebviewborder....optionClicked..."+optionClicked)

        if (listOfOptions!!.size > 2) {
            if (webView_option1!!.url.contains(answer)) {
                Log.e("change question","check answer...0.");
                setCorrectBackground(webView_option1!!)
                unAnsweredList!!.remove(0)
            }
            if (webView_option2!!.url.contains(answer)) {
                Log.e("change question","check answer...1.");
                setCorrectBackground(webView_option2!!)
                unAnsweredList!!.remove(1)
            }
            if (webView_option3!!.url.contains(answer)) {
                Log.e("change question","check answer...2.");
                setCorrectBackground(webView_option3!!)
                unAnsweredList!!.remove(2)
            }
            if (webView_option4!!.url.contains(answer)) {
                Log.e("change question","check answer...3..");
                setCorrectBackground(webView_option4!!)
                unAnsweredList!!.remove(3)
            }
            setInactiveBackgroundNew();
        }else{
            if (webView_option1!!.url.contains(answer)) {
                Log.e("change question","check answer...0.");
                setCorrectBackground(webView_option1!!)
                unAnsweredList!!.remove(0)
            }
            if (webView_option2!!.url.contains(answer)) {
                Log.e("change question","check answer...1.");
                setCorrectBackground(webView_option2!!)
                unAnsweredList!!.remove(1)
            }
            setInactiveBackgroundNew()
        }
    }*/

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

        next_btn!!.setOnClickListener(this)
        prev_btn!!.setOnClickListener(this)
        btn_close!!.setOnClickListener(this)
        hint_btn!!.setOnClickListener(this)
        close!!.setOnClickListener(this)
        iv_cancel_test_question!!.setOnClickListener(this)
        /*webView_option1!!.setOnTouchListener { v, event ->
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
        }*/
    }

    private fun clickOptions(
        event: MotionEvent,
        position: Int,
        stringAns: String
    ) {
        sound = sharedPrefs?.getBooleanPrefVal(this, ConstantPath.SOUNDS) ?: true
        if(!sound){
            // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
            //  mediaPlayer.start()
            if (Utils.loaded) {
                Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                Log.e("Test...............", "Played sound...volume..."+ Utils.volume);
                //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
            }

        }
        unAnsweredList!!.clear()
        unAnsweredList!!.add(3)
        unAnsweredList!!.add(2)
        unAnsweredList!!.add(1)
        unAnsweredList!!.add(0)
        // checkAnswer(position, stringAns)
        /* if (event.getAction() == MotionEvent.ACTION_DOWN) {
             startClickTime = Calendar.getInstance().getTimeInMillis()
         }*/

        /*if (event.action == MotionEvent.ACTION_UP) {
            val clickDuration = Calendar.getInstance().timeInMillis - startClickTime!!
            if (clickDuration < MAX_CLICK_DURATION) {
                unAnsweredList!!.clear()
                unAnsweredList!!.add(3)
                unAnsweredList!!.add(2)
                unAnsweredList!!.add(1)
                unAnsweredList!!.add(0)
                checkAnswer(position, stringAns)
            }
        }*/
    }

    override fun onClick(v: View?) {
       /* if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime()*/
        when (v!!.id) {
            R.id.btn_close -> {
                sound = sharedPrefs?.getBooleanPrefVal(this, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    //  mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test...............", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }

                }
                finish()
            }
            R.id.next_btn -> {
                sound = sharedPrefs?.getBooleanPrefVal(this, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    //  mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test...............", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }

                }
                createPath()
            }
            R.id.prev_btn -> {
                sound = sharedPrefs?.getBooleanPrefVal(this, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    //  mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test...............", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }

                }
                createPathForPrev()
            }
            R.id.hint_btn -> {
                sound = sharedPrefs?.getBooleanPrefVal(this, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    //  mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test...............", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }

                }
                //dialog!!.show()
                solution.visibility = View.VISIBLE
                solution.startAnimation(AnimationUtils.loadAnimation(this, R.anim.transalate_anim));
            }
            R.id.close -> {
                sound = sharedPrefs?.getBooleanPrefVal(this, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    //  mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test...............", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }

                }
                //dialog!!.show()
                solution.visibility = View.GONE
                //solution.startAnimation(AnimationUtils.loadAnimation(this, R.anim.transalate_anim));
            }
        }
    }


    private fun showDialog() {
        dialog = Dialog(this)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(false)
        dialog!!.setContentView(R.layout.hint_dialog)
        val webview = dialog!!.findViewById(com.blobcity.R.id.webview_hint) as WebView
        val btn_gotIt = dialog!!.findViewById(com.blobcity.R.id.btn_gotIt) as Button


        // Creating Dynamic
        /*val displayRectangle = Rect()
        val window = this.getWindow()
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle)
        dialog!!.getWindow().setLayout(((displayRectangle.width() * 0.8f).toInt()), dialog!!.getWindow().getAttributes().height)
*/

        dialog!!.window?.decorView?.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            val displayRectangle = Rect()
            val window = dialog!!.window
            v.getWindowVisibleDisplayFrame(displayRectangle)
            val maxHeight = displayRectangle.height() * 0.8f // 60%

            if (v.height > maxHeight) {
                window?.setLayout(window.attributes.width, maxHeight.toInt())
            }
        }
        webview.settings.javaScriptEnabled = true
        //  webview.setVerticalScrollBarEnabled(true)
        // Enable responsive layout
        // webview.getSettings().setUseWideViewPort(true);
        // Zoom out if the content width is greater than the width of the viewport
        //webview.getSettings().setLoadWithOverviewMode(true);
        val hint = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("onPageFinished", url + "!")
                injectCSS(view, "Hint")
                // view!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#ffffff}</style>';")
            }
        }
        Log.e("test question activity","hint alert dialog....hint path..."+hintPath);
        webview.webViewClient = hint
        webview.loadUrl(hintPath)
        webview.setBackgroundColor(0)
        //buttonEffect(btn_gotIt,false)
        // alertDialog = dialogBuilder.create()
        btn_gotIt.setOnClickListener {
            sound = sharedPrefs?.getBooleanPrefVal(this, SOUNDS) ?: true
            if(!sound){
                // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                //  mediaPlayer.start()
                if (Utils.loaded) {
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound...volume..."+ Utils.volume);
                    //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                }
            }
            dialog!!.dismiss()
        }
        // dialog!!.show()
        // val alertDialog = dialogBuilder.create()
        //  alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        //  alertDialog.show()
        /*yesBtn.setOnClickListener {
            dialog!!.dismiss()
        }
        noBtn.setOnClickListener { dialog .dismiss() }
        dialog .show()*/

    }
}
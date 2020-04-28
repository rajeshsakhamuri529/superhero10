package com.blobcity.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import com.blobcity.R
import com.blobcity.database.DatabaseHandler
import com.blobcity.model.ChallengeModel
import com.blobcity.model.PlayedDailyChallenge
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.SharedPrefs
import com.blobcity.utils.UniqueUUid
import com.blobcity.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_challenge_question.*
import kotlinx.android.synthetic.main.activity_challenge_question.btn_hint

import kotlinx.android.synthetic.main.activity_challenge_question.iv_cancel_test_question
import kotlinx.android.synthetic.main.activity_challenge_question.ll_inflate
import kotlinx.android.synthetic.main.back_pressed_dialog_layout.*


import java.io.File
import java.lang.Exception
import java.net.URLDecoder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChallengeQuestionActivity : BaseActivity(), View.OnClickListener {


    private var hintPath: String? = ""
    private var opt1Path: String? = ""
    private var opt2Path: String? = ""
    private var opt3Path: String? = ""
    private var opt4Path: String? = ""
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
    var quizTimer: Timer? = null
    val handler = Handler()
    var animationFadeIn: Animation? = null
    var animationFadeIn1500: Animation? = null
    var animationFadeIn1000: Animation? = null
    var animationFadeIn500: Animation? = null
    private var listOfOptions: ArrayList<String>? = null
    var unAnsweredList: ArrayList<Int>? = null
    private var questionPath: String? = ""
    var type: String = ""
    var child: View? = null
    var dialog: Dialog? = null;
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    var challenge:ChallengeModel? = null
    var startClickTime: Long? = null
    private val MAX_CLICK_DURATION = 200
    private var isAnswerCorrect: Boolean = false
    private var optionselected: Int = -1
    var databaseHandler: DatabaseHandler?= null
    var firestore: FirebaseFirestore? = null
    override var layoutID: Int = R.layout.activity_challenge_question

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun initView() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        databaseHandler = DatabaseHandler(this);
        firestore = FirebaseFirestore.getInstance()
        listOfOptions = ArrayList()
        quizTimer = Timer()
        sharedPrefs = SharedPrefs()
        unAnsweredList = ArrayList<Int>()
        Log.d("createPath", unAnsweredList!!.size.toString() + "!")
        unAnsweredList!!.clear()
        unAnsweredList!!.add(3)
        unAnsweredList!!.add(2)
        unAnsweredList!!.add(1)
        unAnsweredList!!.add(0)
        challenge = intent.getSerializableExtra("challenge") as? ChallengeModel
        type = challenge?.questiontype!!

        if(challenge?.serialno!!.toInt() < 10){
            tv_count2.setText("#0"+challenge?.serialno)
        }else{
            tv_count2.setText("#"+challenge?.serialno)
        }

        Log.e("challenge question","type...."+type);
        var file = File(getExternalFilesDir(null), "/Challenge/" + challenge?.serialno)

        //var filesList:ArrayList<File> = getfile(file)
        //var filesList:File = getfile(file)

        val listFile = file.listFiles()
        if(listFile.size > 0){
            for (file1 in listFile){
                if (file1.name.contains("question")) {
                    questionPath = file1.absolutePath
                }
                if (file1.name.contains("hint")) {
                    hintPath = file1.absolutePath
                }

                if (file1.name.contains("opt")) {
                    listOfOptions!!.add(file1.absolutePath)
                }


            }

        }

        /*if(filesList.size > 0){
            for (filename in filesList){
                if (filename.absolutePath.contains("opt")) {
                    if (!filename.absolutePath.contains("opt5")) {
                        listOfOptions!!.add(filename.absolutePath)
                    }
                }
                if (filename.absolutePath.contains("question")) {
                    questionPath = filename.absolutePath
                }
                if (filename.absolutePath.contains("hint")) {
                    hintPath = filename.absolutePath
                }
            }

        }*/
        //showDialog()

        webview_hint1.settings.javaScriptEnabled = true
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
        webview_hint1.webViewClient = hint
        webview_hint1.loadUrl("file://"+hintPath)
        webview_hint1.setBackgroundColor(0)



        Log.e("challenge question","....questionPath...."+questionPath);
        Log.e("challenge question","....hintPath...."+hintPath);

        animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_700)
        animationFadeIn1500 = AnimationUtils.loadAnimation(this, R.anim.fade_in_500)
        animationFadeIn1000 = AnimationUtils.loadAnimation(this, R.anim.fade_in_300)
        animationFadeIn500 = AnimationUtils.loadAnimation(this, R.anim.fade_in_100)
        isOption1Wrong = false
        isOption2Wrong = false
        isOption3Wrong = false
        isOption4Wrong = false
        loadDataInWebView()
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

        btn_hint!!.visibility = View.INVISIBLE
        btn_submit!!.visibility = View.VISIBLE
        btn_submit.isEnabled = false
        buttonEffect(btn_submit,true)
        buttonEffect(btn_hint,false)
        btn_submit!!.setOnClickListener(this)
        btn_hint!!.setOnClickListener(this)
        btn_close!!.setOnClickListener(this)
        buttonEffect(btn_close,false)
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
    fun buttonEffect(button: View,isNext:Boolean) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (isNext){
                        v.background.setColorFilter(Color.parseColor("#FF790BF8"), PorterDuff.Mode.SRC_ATOP)
                    }else{
                        v.background.setColorFilter(Color.parseColor("#FFBDB6F3"), PorterDuff.Mode.SRC_ATOP)
                    }

                    v.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    v.background.clearColorFilter()
                    v.invalidate()
                }
            }
            false
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
        webview.loadUrl("file://"+hintPath)
        webview.setBackgroundColor(0)
        buttonEffect(btn_gotIt,false)
        // alertDialog = dialogBuilder.create()
        btn_gotIt.setOnClickListener {
            sound = sharedPrefs?.getBooleanPrefVal(this, ConstantPath.SOUNDS) ?: true
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
        checkAnswer(position, stringAns)
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
    private fun checkAnswer(optionClicked: Int, answer: String) {
        unAnsweredList!!.remove(optionClicked)
        Log.e("change question","check answer...."+optionClicked);
        if (optionClicked == 0) {
            isOption1Wrong = true
            isOption2Wrong = false
            isOption3Wrong = false
            isOption4Wrong = false
            Log.e("change question","check answer...0.");
            setCorrectBackground(webView_option1!!)
        }
        if (optionClicked == 1) {
            isOption2Wrong = true
            isOption3Wrong = false
            isOption4Wrong = false
            isOption1Wrong = false
            Log.e("change question","check answer...1.");
            setCorrectBackground(webView_option2!!)
        }
        if (optionClicked == 2) {
            isOption3Wrong = true
            isOption4Wrong = false
            isOption1Wrong = false
            isOption2Wrong = false
            Log.e("change question","check answer...2.");
            setCorrectBackground(webView_option3!!)
        }
        if (optionClicked == 3) {
            isOption4Wrong = true
            isOption1Wrong = false
            isOption3Wrong = false
            isOption2Wrong = false
            Log.e("change question","check answer...3..");
            setCorrectBackground(webView_option4!!)
        }
        if (listOfOptions!!.get(optionClicked).contains("opt1")) {
            isAnswerCorrect = true
            optionselected = optionClicked;
        }else{
            isAnswerCorrect = false
            optionselected = optionClicked;
        }
        Log.e("chanllenge question","isAnswerCorrect....."+isAnswerCorrect);
        btn_submit!!.visibility = View.VISIBLE
        btn_submit.isEnabled = true
        btn_submit.setBackgroundResource(R.drawable.action)
        btn_submit.setTextColor(resources.getColor(R.color.white))
        setInactiveBackground()
    }

    private fun setInactiveBackground() {
        for (i in unAnsweredList!!) {
            if (i == 0) {
                webView_option1!!.setBackgroundResource(R.drawable.option_curved_border)
                webView_option1!!.setBackgroundColor(0x00000000)
                webView_option1_opacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                webView_option1_opacity!!.setBackgroundColor(0x00000000)
            } else if (i == 1) {
                webView_option2!!.setBackgroundResource(R.drawable.option_curved_border)
                webView_option2!!.setBackgroundColor(0x00000000)
                webView_option2_opacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                webView_option2_opacity!!.setBackgroundColor(0x00000000)
            } else if (i == 2) {
                if (webView_option3 != null) {
                    webView_option3!!.setBackgroundResource(R.drawable.option_curved_border)
                    webView_option3!!.setBackgroundColor(0x00000000)
                    webView_option3_opacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                    webView_option3_opacity!!.setBackgroundColor(0x00000000)
                }

            } else if (i == 3) {
                if (webView_option4 != null) {
                    webView_option4!!.setBackgroundResource(R.drawable.option_curved_border)
                    webView_option4!!.setBackgroundColor(0x00000000)
                    webView_option4_opacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
                    webView_option4_opacity!!.setBackgroundColor(0x00000000)
                }
            }
        }
    }
    private fun setCorrectBackground(webView: WebView) {
        Log.d("setCurrentBackground", webView.url + "!")

        webView.setBackgroundResource(R.drawable.challenge_option_correct_curved_border)
        /*handler.postDelayed(object : Runnable {
            override fun run() {
                //isHandlerExecuted = true
                webView.setBackgroundResource(R.drawable.challenge_option_correct_curved_border)
            }

        }, 1500)*/
        //webView.setBackgroundResource(R.drawable.option_correct_green_border)
       // Utils.challengetransition(applicationContext, webView, true)
        //webView!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#cdcdcd}</style>';")

    }
    private fun loadDataInWebView() {
        if (type == "4100") {
            Log.d("type", "4100")
            inflateView4100()
            setWebViewBGDefault()
            webViewGone()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    webViewAnimation()
                }
            }, 2500)
            webViewPathAndLoad(type)
        }
        if (type == "2201") {
            Log.d("type", "2201")
            inflateView2201()
            setWebViewBGDefault()
            webViewGone()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    webViewAnimation()
                }
            }, 2500)
            webViewPathAndLoad(type)
        }

        if (type == "2210") {
            Log.d("type", "2210")
            inflateView2210()
            setWebViewBGDefault()
            webViewGone()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    webViewAnimation()
                }
            }, 2500)
            webViewPathAndLoad(type)
        }

        if (type == "2100") {
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

            opt1Path = "file://"+ listOfOptions!!.get(0)
            opt2Path = "file://"+ listOfOptions!!.get(1)


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

            if (Utils.jsoupWrapper("file://"+ listOfOptions!!.get(0), this)) {
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


        val qa = object : WebViewClient() {

            override fun onPageFinished(view: WebView?, url: String?) {
                Log.d("onPageFinished", url + "!")
                injectCSS(view, "QA")

            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                val html = URLDecoder.decode(url, "UTF-8").toString()
                Log.d("loadurl@question","!"+url+"!"+html+"!")

                return false
            }

        }
        handler.postDelayed(object : Runnable {
            override fun run() {
                webView_question!!.visibility = View.VISIBLE
                webView_question!!.startAnimation(animationFadeIn500)
            }
        }, 500)


        if (type != "2210") {
            webView_question!!.settings.javaScriptEnabled = true
            webView_question!!.webViewClient = qa
        }


        webView_question!!.loadUrl("file://"+questionPath)

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

    private fun webViewPathAndLoad(type: String) {
        Collections.shuffle(listOfOptions!!)
        opt1Path = "file://"+listOfOptions!!.get(0)
        opt2Path = "file://"+listOfOptions!!.get(1)
        opt3Path = "file://"+listOfOptions!!.get(2)
        opt4Path = "file://"+listOfOptions!!.get(3)
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
        if (type != "2201") {
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_cancel_test_question -> {
                sound = sharedPrefs?.getBooleanPrefVal(this, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    //  mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                }
                onBackPressed()
            }
            R.id.btn_submit -> {
                sound = sharedPrefs?.getBooleanPrefVal(this, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    //  mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                }
                onBtnSubmit()
            }
            R.id.btn_hint -> {
                sound = sharedPrefs?.getBooleanPrefVal(this, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    // mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                }
                solution.visibility = View.VISIBLE
                solution.startAnimation(AnimationUtils.loadAnimation(this, R.anim.transalate_anim));
               // dialog!! .show()

            }
            R.id.btn_close -> {
                sound = sharedPrefs?.getBooleanPrefVal(this, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    // mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                }
                solution.visibility = View.GONE
                // dialog!! .show()

            }
        }
    }

    private fun onBtnSubmit() {

        var btnText:String = btn_submit.text.toString()

        Log.e("submit btn","....btn text...."+btnText);

        if(btnText.equals("Check")){
            btn_submit.text = "Done"
            btn_hint!!.visibility = View.VISIBLE
            btnCheckAction()
        }else if(btnText.equals("Done")){
            if(isAnswerCorrect){
                databaseHandler?.updateDailyChallengeAttempt("1",challenge?.documentid)
            }else{
                databaseHandler?.updateDailyChallengeAttempt("2",challenge?.documentid)
            }
            addAllDataInFireStore()
            val intent = Intent(this, ChallengeSummaryActivity::class.java)
            intent.putExtra("isanswercorrect", isAnswerCorrect)
            intent.putExtra("challenge", challenge)
            startActivity(intent)
            finish()
        }

    }
    private fun btnCheckAction(){
        if(isAnswerCorrect){
            if (optionselected == 0) {
                setCorrectAnsBackground(webView_option1!!, webView_option1_opacity!!)
            }
            if (optionselected == 1) {
                setCorrectAnsBackground(webView_option2!!, webView_option2_opacity!!)
            }
            if (optionselected == 2) {
                setCorrectAnsBackground(webView_option3!!, webView_option3_opacity!!)
            }
            if (optionselected == 3) {
                setCorrectAnsBackground(webView_option4!!, webView_option4_opacity!!)
            }
            setInactiveBackgroundForGrey()
        }else{
            if (optionselected == 0) {
                setWrongAnsBackground(webView_option1!!, webView_option1_opacity!!)
            }
            if (optionselected == 1) {
                setWrongAnsBackground(webView_option2!!, webView_option2_opacity!!)
            }
            if (optionselected == 2) {
                setWrongAnsBackground(webView_option3!!, webView_option3_opacity!!)
            }
            if (optionselected == 3) {
                setWrongAnsBackground(webView_option4!!, webView_option4_opacity!!)
            }
            setInactiveBackgroundForGrey()
        }

    }
    private fun setInactiveBackgroundForGrey() {
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
    private fun setCorrectAnsBackground(webView: WebView, webViewOpacity: WebView) {
        Log.d("setCurrentBackground", webView.url + "!")
        handler.postDelayed(object : Runnable {
            override fun run() {
               // isHandlerExecuted = true
                webView.setBackgroundResource(R.drawable.option_correct_curved_border)
            }

        }, 1500)
        //webView.setBackgroundResource(R.drawable.option_correct_green_border)
        webView!!.isEnabled = false
        webView!!.setBackgroundResource(R.drawable.option_curved_border)
        webView!!.setBackgroundColor(0x00000000)
        webViewOpacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
        webViewOpacity!!.setBackgroundColor(0x00000000)
        Utils.transition(applicationContext, webView, true)
        //webView!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#cdcdcd}</style>';")

    }
    private fun setWrongAnsBackground(webViewReal: WebView, webViewOpacity: WebView) {
        /*val trans = webView.background as TransitionDrawable
        trans.startTransition(5000)*/
        handler.postDelayed(object : Runnable {
            override fun run() {
                //isHandlerExecuted = true
                webViewReal.visibility = View.VISIBLE
                //webViewOpacity.visibility = View.VISIBLE
                //webView.setBackgroundResource(R.drawable.inactive_answer_overlay)


            }

        }, 1500)
        webViewReal!!.isEnabled = false
        webViewReal!!.setBackgroundResource(R.drawable.option_curved_border)
        webViewReal!!.setBackgroundColor(0x00000000)
        webViewOpacity!!.setBackgroundResource(R.drawable.inactive_answer_overlay)
        webViewOpacity!!.setBackgroundColor(0x00000000)
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
    private fun addAllDataInFireStore() {

        val date = Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateTimeInstance() //or use getDateInstance()
        val formatedDate = formatter.format(date)
        Log.e("challenge question","formated date......"+formatedDate);

        var inputFormat: DateFormat = SimpleDateFormat("ddMMyyyy")
        var outputFormat: DateFormat = SimpleDateFormat("MMM dd, yyyy")
        var inputDateStr = challenge?.challengedate
        var date1 = inputFormat.parse(inputDateStr)
        var outputDateStr = outputFormat.format(date1)



        var playedDailyChallenge = PlayedDailyChallenge()

        playedDailyChallenge.uid = UniqueUUid.id(this)
        playedDailyChallenge.qid = challenge?.qid
        playedDailyChallenge.result = isAnswerCorrect
        playedDailyChallenge.title = challenge?.challengetitle
        playedDailyChallenge.challengedate = outputDateStr
        playedDailyChallenge.createddate = formatedDate
        firestore!!.collection("playeddailychallenge")
            .add(playedDailyChallenge)
            .addOnCompleteListener(object : OnCompleteListener<DocumentReference> {
                override fun onComplete(task: Task<DocumentReference>) {
                    if (task.isSuccessful) {
                        Log.e("playeddailychallenge", "playeddailychallenge added successfully")
                    } else {
                        Log.e("playeddailychallenge", task.exception.toString())
                    }
                }
            })

    }
    override fun onBackPressed() {
       // if (isLevelCompleted) {
            //addAllDataInDb(true, false)
         //   addDataInDb()
          //  super.onBackPressed()

       // } else {
           backPressDialog()
        //}
    }

    private fun addDataInDb() {
        val uId: String = UniqueUUid.id(this)
        /*topicStatusVM!!.insert(
            courseId!!, uId,
            topicId!!, topicLevel!!, dbPosition!!, gradeTitle!!
        )*/
    }
    private fun backPressDialog() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.mytheme)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.back_pressed_dialog_layout, null)
        dialogBuilder.setView(dialogView)

        val tv_quit = dialogView.findViewById(R.id.tv_quit) as TextView
        val tv_return = dialogView.findViewById(R.id.tv_return) as TextView
        val tv_message = dialogView.findViewById(R.id.tv_message) as TextView
        tv_message.setText("Are you sure you want to quit the Challenge? Your progress in this Challenge will not be saved!")
        tv_return.setText("Return to Challenge")
        tv_quit.setOnClickListener {
            /*if (countQuestion >= 0) {
                if (isFirstAnswerGiven) {
                    addAllDataInDb(false, true)
                }
            }*/
            //finish()
            val i = Intent(this, DashBoardActivity::class.java)
            i.putExtra("fragment", "dailychallenge")
            startActivity(i)
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
    fun getfile(dir: File):ArrayList<File> {

        Log.e("challenge question","....dir...."+dir);
        val listFile = dir.listFiles()
        val fileList = ArrayList<File>()
        if (listFile != null && listFile.size > 0)
        {
            for (i in listFile.indices)
            {
                if (listFile[i].isDirectory())
                {
                    fileList.add(listFile[i])
                    getfile(listFile[i])
                }
                else
                {
                    if ((listFile[i].getName().endsWith(".png")
                                || listFile[i].getName().endsWith(".jpg")
                                || listFile[i].getName().endsWith(".html")
                                || listFile[i].getName().endsWith(".jpeg")
                                || listFile[i].getName().endsWith(".gif")))
                    {
                        fileList.add(listFile[i])
                    }
                }
            }
        }
        return fileList
    }
}

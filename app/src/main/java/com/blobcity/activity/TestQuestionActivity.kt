package com.blobcity.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ImageViewCompat
import android.support.v7.app.AlertDialog
import android.text.Html
import android.text.TextUtils
import android.util.ArrayMap
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.webkit.WebViewClient
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
import android.util.TypedValue
import android.view.*
import android.widget.*
import com.blobcity.database.QuizGameDataBase
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.SharedPrefs
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_test_question.btn_close
import kotlinx.android.synthetic.main.activity_test_question.btn_hint
import kotlinx.android.synthetic.main.activity_test_question.btn_next
import kotlinx.android.synthetic.main.activity_test_question.btn_submit
import kotlinx.android.synthetic.main.activity_test_question.iv_cancel_test_question
import kotlinx.android.synthetic.main.activity_test_question.iv_life1
import kotlinx.android.synthetic.main.activity_test_question.iv_life2
import kotlinx.android.synthetic.main.activity_test_question.iv_life3
import kotlinx.android.synthetic.main.activity_test_question.left_arrow
import kotlinx.android.synthetic.main.activity_test_question.ll_dots
import kotlinx.android.synthetic.main.activity_test_question.ll_inflate
import kotlinx.android.synthetic.main.activity_test_question.next_btn
import kotlinx.android.synthetic.main.activity_test_question.prev_btn
import kotlinx.android.synthetic.main.activity_test_question.right_arrow
import kotlinx.android.synthetic.main.activity_test_question.tv_count1
import kotlinx.android.synthetic.main.activity_test_question.tv_count2
import kotlinx.android.synthetic.main.activity_test_question.txt_next
import kotlinx.android.synthetic.main.activity_test_question.txt_prev
import kotlinx.android.synthetic.main.activity_test_quiz.*
import java.lang.Exception
import java.lang.StringBuilder
import java.net.URLDecoder


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
    //private var position: Int = -1
    private var positionList: ArrayList<Int>? = ArrayList()
    private var countQuestion: Int = -1
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
    var readyCardNumber = 0
    private lateinit var mediaPlayer: MediaPlayer
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    var isDialogOpen : Boolean = false

    var alertDialog : AlertDialog? = null
    var dialog: Dialog? = null;
    var animFadeIn: Animation? = null
    var animFadeOut: Animation? = null

    lateinit var circles: Array<ImageView?>
    //lateinit var circles: Array<ImageView?>
    lateinit var optionsbuilder:StringBuilder
    lateinit var questionsbuilder:StringBuilder
    lateinit var answerbuilder:StringBuilder
    lateinit var questionanswerbuilder:StringBuilder

    lateinit var pathsbuilder:StringBuilder
    lateinit var typebuilder:StringBuilder
    var databaseHandler: QuizGameDataBase?= null
    var lastplayed:String = ""
    var comingfrom:String = ""
    var displayno:Int = 0
    var paths: String = ""
    //lateinit var topic: Topic
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override var layoutID: Int = R.layout.activity_test_question

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun initView() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        //window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorbottomnav));
        }
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        //topic = intent.getSerializableExtra(TOPIC) as Topic
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
        readyCardNumber = intent.getIntExtra(CARD_NO, -1)
        lastplayed = intent.getStringExtra("LAST_PLAYED")
        comingfrom = intent.getStringExtra("comingfrom")
        displayno = intent.getIntExtra("DISPLAY_NO", -1)
        quizTimer = Timer()
        sharedPrefs = SharedPrefs()
        animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_700)
        animationFadeIn1500 = AnimationUtils.loadAnimation(this, R.anim.fade_in_500)
        animationFadeIn1000 = AnimationUtils.loadAnimation(this, R.anim.fade_in_300)
        animationFadeIn500 = AnimationUtils.loadAnimation(this, R.anim.fade_in_100)

        firestore = FirebaseFirestore.getInstance()
        databaseHandler = QuizGameDataBase(this);
        databaseHandler!!.updatequiztopicslastplayed(topicName,lastplayed)
        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
            R.anim.fade_in);

        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),
            R.anim.fade_out);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        /*dbRStatus = FirebaseDatabase.getInstance()
            .getReference("topic_status")
        dbTrackingStatus = FirebaseDatabase.getInstance().getReference("quiz_tracking")
        dbTrackingHintStatus = FirebaseDatabase.getInstance().getReference("hint_tracking")
        dbRStatus!!.keepSynced(true)*/
        unAnsweredList = ArrayList<Int>()
        createArrayMapList(dynamicPath!!)
        btn_submit.isEnabled = false
        btn_hint!!.visibility = View.INVISIBLE
        btn_next!!.visibility = View.INVISIBLE
        buttonEffect(btn_next,true)
        buttonEffect(btn_hint,false)
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
        btn_submit!!.setOnClickListener(this)
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
               // checkAnswer(position, stringAns)
                unAnsweredList!!.clear()
                unAnsweredList!!.add(3)
                unAnsweredList!!.add(2)
                unAnsweredList!!.add(1)
                unAnsweredList!!.add(0)
                checkAnswerNew(position, stringAns)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_cancel_test_question -> {
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
                onBackPressed()
            }
            R.id.btn_next -> {
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
                onBtnNext()
            }

            R.id.btn_hint -> {
                sound = sharedPrefs?.getBooleanPrefVal(this, SOUNDS) ?: true
                if(!sound){
                   // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                   // mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                }

                dialog!! .show()

            }
            R.id.next_btn -> {
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
                createPath()
            }
            R.id.prev_btn -> {
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
                createPathForPrev()
            }
            R.id.btn_close -> {
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
                backPressDialog()
            }
            R.id.btn_submit -> {
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
                navigateToSummaryScreenNew()
            }
        }
    }

    private fun showNewDialog(){
        val builder = AlertDialog.Builder(this)
        val wv = WebView(this)
        wv.loadUrl(hintPath)
        wv.setWebViewClient(object:WebViewClient() {
            override fun shouldOverrideUrlLoading(view:WebView, url:String):Boolean {
                view.loadUrl(url)
                return true
            }
        })
        val container = FrameLayout(this@TestQuestionActivity)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        //val height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, getResources().getDisplayMetrics().heightPixels * 0.8f, getResources().getDisplayMetrics()) as Int
        //params.height = height
        wv.setLayoutParams(params)
        container.addView(wv)
        builder.setView(container)
        builder.setNegativeButton("OK", object:
            DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, id:Int) {
                dialog.dismiss()
            }
        })

        val dialog = builder.create()
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.getWindow().getAttributes())
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.gravity = Gravity.BOTTOM
        dialog.getWindow().setAttributes(layoutParams)
        dialog.getWindow().setWindowAnimations(R.style.DialogNoAnimation);
        dialog.show()


    }
    private fun show(msg: String) {
        val builder = AlertDialog.Builder(this).apply {
            setPositiveButton(android.R.string.ok, null)
            setNegativeButton(android.R.string.cancel, null)
        }

        val dialog = builder.create().apply {
            setMessage(msg)
        }
        dialog.show()

        dialog.window?.decorView?.addOnLayoutChangeListener { v, _, _, _, _, _, _, _, _ ->
            val displayRectangle = Rect()
            val window = dialog.window
            v.getWindowVisibleDisplayFrame(displayRectangle)
            val maxHeight = displayRectangle.height() * 0.6f // 60%

            if (v.height > maxHeight) {
                window?.setLayout(window.attributes.width, maxHeight.toInt())
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
        buttonEffect(btn_gotIt,false)
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
        buttonEffect(btn_gotIt,false)
        val alertDialog = dialogBuilder.create()
        btn_gotIt.setOnClickListener {
            alertDialog.dismiss()
        }
       // val alertDialog = dialogBuilder.create()
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
        val tv_text1 = dialogView.findViewById(R.id.tv_msg1) as TextView
        val tv_text2 = dialogView.findViewById(R.id.tv_msg3) as TextView


        val alertDialog = dialogBuilder.create()
        if(alertDialog.isShowing){
            alertDialog.dismiss()
        }
        Log.d("dialog", availableLife.toString() + "!")
        if (totalLife == 3) {
            iv_heart_3.visibility = View.VISIBLE
        } else {
            iv_heart_3.visibility = View.GONE
        }
        if (listOfOptions!!.size > 2) {
            tv_text.visibility = View.VISIBLE
        }else{
            tv_text.visibility = View.GONE
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
            tv_text.visibility = View.VISIBLE
            tv_text.text = "GAME OVER"
            tv_text1.text = "Oops.. no mistakes allowed!"
            tv_text2.text = "Check out the Hint and start again!"
            tv_text.setTextColor(resources.getColor(R.color.red_wrong_answer))
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
        buttonEffect(btn_ok,false)
        btn_ok.setOnClickListener {
            btn_hint.visibility = View.VISIBLE
            if (listOfOptions!!.size > 2) {

            }else{
                isAnswerCorrect = true
                btn_next.text = "NEXT"
                btn_next.visibility = View.VISIBLE
                //TODO opacity;
                setInactiveBackground2()
            }

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
            isDialogOpen = false
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
        var i=0
        while(i<totalQuestion!!){
            positionList!!.add(i)
            i++
        }
        positionList!!.shuffle()
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
            availableLife = 0
            totalLife = 3
        } else {
            iv_life3.visibility = View.GONE
            availableLife = 0
            totalLife = 2
        }

        pathsbuilder = StringBuilder()
        typebuilder = StringBuilder()
        optionsbuilder = StringBuilder()
        for (i in 0 until totalQuestion!!)
        {
            questionsItem = arrayMap!!.get(listWithUniqueString!!.get(positionList!![i]))
            Log.e("test question act","questionsItem....."+questionsItem)
            if (questionsItem!!.size > 1) {
                randomPosition = Random.nextInt(questionsItem!!.size)
                singleQuestionsItem = questionsItem!!.get(randomPosition)
                dbQPaths = questionsItem!!.get(randomPosition).id
                dbQuestionBank = questionsItem!!.get(randomPosition).bank
                dbQLevel = questionsItem!!.get(randomPosition).level
                paths = localBlobcityPath + dbQPaths
                type = questionsItem!!.get(randomPosition).type

            }
            if(pathsbuilder.length == 0){
                pathsbuilder.append(""+paths+"~"+type)

            }else{
                pathsbuilder.append(","+paths+"~"+type)
            }

            Log.e("test question activity","......for....i....."+i+"........"+paths);

            if(optionsbuilder.length == 0){
                var options = StringBuilder()
                for (filename in listAssetFiles(paths, applicationContext)!!) {
                    if (filename.contains("opt")) {
                        if (!filename.contains("opt5")) {
                            //listOfOptions!!.add(filename)
                            if (options.length == 0) {
                                options.append(filename)
                            } else {
                                options.append("-" + filename)
                            }
                        }
                    }
                }

                optionsbuilder.append(""+(i+1)+"~"+options.toString())
            }else{
                var options = StringBuilder()
                for (filename in listAssetFiles(paths, applicationContext)!!) {
                    if (filename.contains("opt")) {
                        if (!filename.contains("opt5")) {
                            //listOfOptions!!.add(filename)
                            if (options.length == 0) {
                                options.append(filename)
                            } else {
                                options.append("-" + filename)
                            }
                        }
                    }
                }
                optionsbuilder.append(","+(i+1)+"~"+options.toString())
            }





        }




        questionsbuilder = StringBuilder()
        answerbuilder = StringBuilder()
        questionanswerbuilder = StringBuilder()
        for (i in 0 until totalQuestion!!)
        {
            Log.e("test question activity","i value...."+i);
            if(questionsbuilder.length == 0){
                questionsbuilder.append(""+(i+1))
                questionanswerbuilder.append(""+(i+1) + "~"+"-1")
                answerbuilder.append(""+0)
            }else{
                questionsbuilder.append(","+(i+1))
                questionanswerbuilder.append(","+(i+1) + "~"+"-1")
                answerbuilder.append(","+0)
            }
        }

        /*for (i in 0 until totalQuestion!!)
        {

            if(optionsbuilder.length == 0){
                var options = StringBuilder()
                for (filename in listAssetFiles(paths, applicationContext)!!) {
                    if (filename.contains("opt")) {
                        if (!filename.contains("opt5")) {
                            //listOfOptions!!.add(filename)
                            if (options.length == 0) {
                                options.append(filename)
                            } else {
                                options.append("-" + filename)
                            }
                        }
                    }
                }

                optionsbuilder.append(""+(i+1)+"~"+options.toString())
            }else{
                var options = StringBuilder()
                for (filename in listAssetFiles(path, applicationContext)!!) {
                    if (filename.contains("opt")) {
                        if (!filename.contains("opt5")) {
                            //listOfOptions!!.add(filename)
                            if (options.length == 0) {
                                options.append(filename)
                            } else {
                                options.append("-" + filename)
                            }
                        }
                    }
                }
                optionsbuilder.append(","+(i+1)+"~"+options.toString())
            }

        }*/

        Log.e("test question activity","totalQuestion....."+totalQuestion);
        Log.e("test question activity","topicName....."+topicName);
        Log.e("test question activity","questionsbuilder....."+questionsbuilder.toString());
        Log.e("test question activity","answerbuilder....."+answerbuilder.toString());
        Log.e("test question activity","questionanswerbuilder....."+questionanswerbuilder.toString());
        databaseHandler!!.insertquizplay(topicName,
            totalQuestion!!,questionsbuilder.toString(),answerbuilder.toString(),questionanswerbuilder.toString(),pathsbuilder.toString(),optionsbuilder.toString())
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
        countQuestion++
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
        if (countQuestion < totalQuestion!!) {
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





            addDot(countInt, totalQuestion!!)



            val paths: String = databaseHandler!!.getQuizQuestionPath(topicName)
            var ans:List<String> = paths.split(",")
            var ans1:List<String> = ans.get((countInt - 1)).split("~")
            type = ans1[1].toInt()
            loadDataInWebView(ans1[0])
            var answers:String = databaseHandler!!.getQuizAnswers(topicName);


            var anslist:List<String> = answers.split(",")
            Log.e("test question activity","next.....countInt..."+countInt);
            Log.e("test question activity","next.....ans..."+anslist);

            if(anslist.get((countInt - 1)).equals("1")){

                if(countInt == totalQuestion){
                    next_btn.isEnabled = false
                    next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_inactive_btn));
                    txt_next.setTextColor(getResources().getColor(R.color.submit_inactive_color))
                    ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.arrow_tint)));


                }else{
                    next_btn.isEnabled = true
                    next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_active_btn));
                    txt_next.setTextColor(getResources().getColor(R.color.button_close_text))
                    ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.button_close_text)));

                }

            }

            /*questionsItem = arrayMap!!.get(listWithUniqueString!!.get(positionList!![countQuestion]))
            if (questionsItem!!.size > 1) {
                randomPosition = Random.nextInt(questionsItem!!.size)
                singleQuestionsItem = questionsItem!!.get((countInt-1))
                dbQPaths = questionsItem!!.get((countInt-1)).id
                dbQuestionBank = questionsItem!!.get((countInt-1)).bank
                dbQLevel = questionsItem!!.get((countInt-1)).level
                paths = localBlobcityPath + dbQPaths
                type = questionsItem!!.get((countInt-1)).type
                loadDataInWebView(paths)
            } else {
                singleQuestionsItem = questionsItem!!.get(0)
                dbQPaths = questionsItem!!.get(0).id
                dbQuestionBank = questionsItem!!.get(0).bank
                dbQLevel = questionsItem!!.get(0).level
                paths = localBlobcityPath + dbQPaths
                type = questionsItem!!.get(0).type
                loadDataInWebView(paths)
            }*/
        }
        /*if (countQuestion >= totalQuestion!!) {
            navigateToSummaryScreen(true)
        }*/


    }

    fun createPathForPrev(){

        unAnsweredList!!.clear()
        unAnsweredList!!.add(3)
        unAnsweredList!!.add(2)
        unAnsweredList!!.add(1)
        unAnsweredList!!.add(0)
        Log.d("createPath", unAnsweredList!!.size.toString() + "!")
        //position++
        countQuestion--
        btn_hint!!.visibility = View.INVISIBLE
        quizTimer!!.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                quizTimerCount--
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
        if (countQuestion < totalQuestion!!) {
            bank = Bank()
            reviewModel = ReviewModel()
            answerList = ArrayList()
            optionsWithAnswerList = ArrayList()
            perQuizTimer = Timer()
            perQuizTimer!!.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    perQuizTimerCount--
                }

            }, 1000, 1000)
            isOption1Wrong = false
            isOption2Wrong = false
            isOption3Wrong = false
            isOption4Wrong = false
            countInt--
            isAnswerCorrect = false
            btn_next!!.visibility = View.INVISIBLE
            val count = "$countInt of $totalQuestion"

            //tv_count.text = Utils.ofSize(count,countInt.toString().length)
            tv_count1.text = "$countInt"
            tv_count2.text = "$totalQuestion"



            addDot(countInt, totalQuestion!!)

            val paths: String = databaseHandler!!.getQuizQuestionPath(topicName)
            var ans:List<String> = paths.split(",")
            var ans1:List<String> = ans.get((countInt - 1)).split("~")
            type = ans1[1].toInt()
            loadDataInWebView(ans1[0])

            var answers:String = databaseHandler!!.getQuizAnswers(topicName);


            var anslist:List<String> = answers.split(",")
            Log.e("test question activity","next.....countInt..."+countInt);
            Log.e("test question activity","next.....ans..."+anslist);

            if(anslist.get((countInt - 1)).equals("1")){

                if(countInt == totalQuestion){
                    next_btn.isEnabled = false
                    next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_inactive_btn));
                    txt_next.setTextColor(getResources().getColor(R.color.submit_inactive_color))
                    ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.arrow_tint)));


                }else{
                    next_btn.isEnabled = true
                    next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_active_btn));
                    txt_next.setTextColor(getResources().getColor(R.color.button_close_text))
                    ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.button_close_text)));

                }
            }



            /*questionsItem = arrayMap!!.get(listWithUniqueString!!.get(positionList!![countQuestion]))
            if (questionsItem!!.size > 1) {
                randomPosition = Random.nextInt(questionsItem!!.size)
                singleQuestionsItem = questionsItem!!.get((countInt-1))
                dbQPaths = questionsItem!!.get((countInt-1)).id
                dbQuestionBank = questionsItem!!.get((countInt-1)).bank
                dbQLevel = questionsItem!!.get((countInt-1)).level
                paths = localBlobcityPath + dbQPaths
                type = questionsItem!!.get((countInt-1)).type
                loadDataInWebView(paths)
            } else {
                singleQuestionsItem = questionsItem!!.get(0)
                dbQPaths = questionsItem!!.get(0).id
                dbQuestionBank = questionsItem!!.get(0).bank
                dbQLevel = questionsItem!!.get(0).level
                paths = localBlobcityPath + dbQPaths
                type = questionsItem!!.get(0).type
                loadDataInWebView(paths)
            }*/
        }
       /* if (countQuestion >= totalQuestion!!) {
            navigateToSummaryScreen(true)
        }*/


        /*var answers:String = databaseHandler!!.getQuizAnswers(topicName);


        var ans:List<String> = answers.split(",")
        Log.e("test question activity","prev.....countInt..."+countInt);
        Log.e("test question activity","prev.....ans..."+ans);

        if(ans.get((countInt - 1)).equals("1")){
            var questionanswers:String = databaseHandler!!.getQuizQuestionAnswers(topicName);
            var queans:List<String> = questionanswers.split(",")
            var ans1:List<String> = queans.get((countInt - 1)).split("~")
            Log.e("test question activity","prev.....ans1..."+ans1);
            checkwebviewborder(ans1[1].toInt(),"")
        }*/


    }

    fun addDot(countint:Int,totalquestions:Int) {
        //val layout_dot = findViewById(R.id.ll_dots) as LinearLayout
        circles = arrayOfNulls<ImageView>(totalquestions)
        //circles = arrayOfNulls<ImageView>(totalQuestion!!)
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

        if(countint == 1){
            /*next_btn.isEnabled = true
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_active_btn));
            txt_next.setTextColor(getResources().getColor(R.color.button_close_text))*/

            next_btn.visibility = View.VISIBLE
            prev_btn.visibility = View.GONE

            next_btn.isEnabled = false
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_inactive_btn));
            txt_next.setTextColor(getResources().getColor(R.color.submit_inactive_color))
            ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.arrow_tint)));



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
        }else{
            /*next_btn.isEnabled = true
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_active_btn));
            txt_next.setTextColor(getResources().getColor(R.color.button_close_text))
            ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.button_close_text)));
*/
            next_btn.visibility = View.VISIBLE
            prev_btn.visibility = View.VISIBLE

            next_btn.isEnabled = false
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_inactive_btn));
            txt_next.setTextColor(getResources().getColor(R.color.submit_inactive_color))
            ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.arrow_tint)));



            prev_btn.isEnabled = true
            prev_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_active_btn));
            txt_prev.setTextColor(getResources().getColor(R.color.button_close_text))
            ImageViewCompat.setImageTintList(left_arrow, ColorStateList.valueOf(getResources().getColor(R.color.button_close_text)));
            //left_arrow.setColorFilter(null)

        }


        //set active dot color
        // dot[2]!!.setTextColor(getResources().getColor(R.color.button_close_text))
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
        Log.e("test question activity","testPath................"+ path)
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
               showDialog()
                /*webview_hint.settings.javaScriptEnabled = true
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
                webview_hint.setBackgroundColor(0)*/
            }
        }

        var answers:String = databaseHandler!!.getQuizAnswers(topicName);


        var anslist:List<String> = answers.split(",")
        Log.e("test question activity","next.....countInt..."+countInt);
        Log.e("test question activity","next.....ans..."+anslist);

        if(anslist.get((countInt - 1)).equals("1")){
            listOfOptions!!.clear()
            var optionString:String = databaseHandler!!.getQuizOptions(topicName)

            var queans:List<String> = optionString.split(",")
            var optionsmutanslist = queans.toMutableList()

            var options1:List<String> = optionsmutanslist.get((countInt - 1)).split("~")
            Log.e("review activity","options1......."+options1)
            //var options1list = options1.toMutableList()
            var options2list:List<String> = options1[1].split("-")
            for(i in 0 until options2list.size){
                listOfOptions!!.add(options2list[i])
            }
        }else{
            Collections.shuffle(listOfOptions!!)
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
            }, 500)
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
            }, 500)
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
            }, 500)
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

            }, 500)

            opt1Path = WEBVIEW_PATH + path + "/" + listOfOptions!!.get(0)
            opt2Path = WEBVIEW_PATH + path + "/" + listOfOptions!!.get(1)


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

            if (Utils.jsoupWrapper(path + "/" + listOfOptions!!.get(0), this)) {
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
        handler.postDelayed(object : Runnable {
            override fun run() {
                webView_question!!.visibility = View.VISIBLE
                webView_question!!.startAnimation(animationFadeIn500)
            }
        }, 500)


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


        var answers:String = databaseHandler!!.getQuizAnswers(topicName);


        var ans:List<String> = answers.split(",")
        Log.e("test question activity","next.....countInt..."+countInt);
        Log.e("test question activity","next.....ans..."+ans);

        if(ans.get((countInt - 1)).equals("1")){
            var questionanswers:String = databaseHandler!!.getQuizQuestionAnswers(topicName);
            var queans:List<String> = questionanswers.split(",")
            var ans1:List<String> = queans.get((countInt - 1)).split("~")
            Log.e("test question activity","next.....ans1..."+ans1);

            checkwebviewborder(ans1[1])
        }
    }

    private fun webViewPathAndLoad(path: String, type: Int) {

        opt1Path = WEBVIEW_PATH + path + "/" + listOfOptions!!.get(0)
        opt2Path = WEBVIEW_PATH + path + "/" + listOfOptions!!.get(1)
        opt3Path = WEBVIEW_PATH + path + "/" + listOfOptions!!.get(2)
        opt4Path = WEBVIEW_PATH + path + "/" + listOfOptions!!.get(3)
        Log.d("webViewPathAndLoad", opt1Path + " ! " + opt2Path)

        var optionString:String = databaseHandler!!.getQuizOptions(topicName)

        var queans:List<String> = optionString.split(",")
        var optionsmutanslist = queans.toMutableList()
        Log.e("test question activity","check answer new.....queansmutanslist..."+optionsmutanslist);
        //var optionsmutanslist1 = optionsmutanslist.split("~")
        Log.e("test question activity","check answer new.....queansmutanslist.get((countInt - 1))..."+optionsmutanslist.get((countInt - 1)));

        var options1:List<String> = optionsmutanslist.get((countInt - 1)).split("~")

        Log.e("test question activity","check answer new.....ans1..."+options1);
        var mutanslist1 = options1.toMutableList()

        var options = StringBuilder()
        for (i in 0 until listOfOptions!!.size){
            if(options.length == 0){
                options.append(listOfOptions!!.get(i))
            }else{
                options.append("-"+listOfOptions!!.get(i))
            }
        }


        Log.e("test question activity","selected option......."+options)
        mutanslist1[1] = options.toString()
        Log.e("test question activity","check answer new.....mutanslist1..."+mutanslist1);
        optionsmutanslist[(countInt - 1)] = mutanslist1.get(0)+"~"+mutanslist1.get(1)

        Log.e("test question activity","check answer new.....queansmutanslist...final....."+optionsmutanslist);
        var optionstringBuilder = StringBuilder()
        for(i in 0 until optionsmutanslist.size){
            if(optionstringBuilder.length == 0){
                optionstringBuilder.append(optionsmutanslist.get(i))
            }else{
                optionstringBuilder.append(","+optionsmutanslist.get(i))
            }
        }

        databaseHandler!!.updatequizplayoptions(topicName,optionstringBuilder.toString())


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

    private fun checkAnswerNew(optionClicked: Int, answer: String) {
        Log.e("test question activity","check answer new.....optionClicked..."+optionClicked);
        Log.e("test question activity","check answer new.....answer..."+answer);
        Log.e("test question activity","check answer new.....listOfOptions..."+listOfOptions!!.size);

        if(countInt == totalQuestion){
            next_btn.isEnabled = false
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_inactive_btn));
            txt_next.setTextColor(getResources().getColor(R.color.submit_inactive_color))
            ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.arrow_tint)));


        }else{
            next_btn.isEnabled = true
            next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_active_btn));
            txt_next.setTextColor(getResources().getColor(R.color.button_close_text))
            ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.button_close_text)));

        }

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
        var answers:String = databaseHandler!!.getQuizAnswers(topicName);
        var questionanswers:String = databaseHandler!!.getQuizQuestionAnswers(topicName);

        var ans:List<String> = answers.split(",")
        Log.e("test question activity","check answer new.....countInt..."+countInt);
        Log.e("test question activity","check answer new.....ans..."+ans);
        var mutanslist = ans.toMutableList()
        mutanslist[(countInt - 1)] = "1"
        Log.e("test question activity","check answer new.....after mutanslist....."+mutanslist);
        var ansstringBuilder = StringBuilder()
        for(i in 0 until mutanslist.size){
            if(ansstringBuilder.length == 0){
                ansstringBuilder.append(mutanslist.get(i))
            }else{
                ansstringBuilder.append(","+mutanslist.get(i))
            }
        }

        var queans:List<String> = questionanswers.split(",")
        var queansmutanslist = queans.toMutableList()
        Log.e("test question activity","check answer new.....queansmutanslist..."+queansmutanslist);

        Log.e("test question activity","check answer new.....queansmutanslist.get((countInt - 1))..."+queansmutanslist.get((countInt - 1)));

        var ans1:List<String> = queansmutanslist.get((countInt - 1)).split("~")

        Log.e("test question activity","check answer new.....ans1..."+ans1);
        var mutanslist1 = ans1.toMutableList()
        var selectedOption : String? = null
        if (listOfOptions!!.get(optionClicked).contains("opt1")) {
            selectedOption = "opt1"
        }
        if (listOfOptions!!.get(optionClicked).contains("opt2")) {
            selectedOption = "opt2"
        }
        if (listOfOptions!!.get(optionClicked).contains("opt3")) {
            selectedOption = "opt3"
        }
        if (listOfOptions!!.get(optionClicked).contains("opt4")) {
            selectedOption = "opt4"
        }


        Log.e("test question activity","selected option......."+selectedOption)
        mutanslist1[1] = selectedOption!!
        Log.e("test question activity","check answer new.....mutanslist1..."+mutanslist1);
        queansmutanslist[(countInt - 1)] = mutanslist1.get(0)+"~"+mutanslist1.get(1)

        Log.e("test question activity","check answer new.....queansmutanslist...final....."+queansmutanslist);
        var queansstringBuilder = StringBuilder()
        for(i in 0 until queansmutanslist.size){
            if(queansstringBuilder.length == 0){
                queansstringBuilder.append(queansmutanslist.get(i))
            }else{
                queansstringBuilder.append(","+queansmutanslist.get(i))
            }
        }

        Log.e("test question activity","check answer new.....ansstringBuilder..."+ansstringBuilder.toString());

        Log.e("test question activity","check answer new.....queansstringBuilder..."+queansstringBuilder.toString());
        databaseHandler!!.updatequizplayanswers(topicName,ansstringBuilder.toString())
        databaseHandler!!.updatequizplayquestionanswers(topicName,queansstringBuilder.toString())

        var answers1:String = databaseHandler!!.getQuizAnswers(topicName);


        var finalans:List<String> = answers1.split(",")

        if(finalans.contains("0")){
            Log.e("test question","submit btn not enabled.........");
            btn_submit.isEnabled = false
            btn_submit.setBackground(ContextCompat.getDrawable(this, R.drawable.submit_inactive_background));
            //txt_prev.setTextColor(getResources().getColor(R.color.button_close_text))
        }else{
            Log.e("test question","submit enabled.........");
            btn_submit.isEnabled = true
            btn_submit.setBackground(ContextCompat.getDrawable(this, R.drawable.submit_active_background));
            //txt_prev.setTextColor(getResources().getColor(R.color.button_close_text))
        }


        //checkwebviewborder(optionClicked,answer)
        unAnsweredList!!.remove(optionClicked)
        if (listOfOptions!!.size > 2) {
            if (optionClicked == 0) {
                Log.e("change question","check answer...0.");
                setCorrectBackground(webView_option1!!)
            }
            if (optionClicked == 1) {
                Log.e("change question","check answer...1.");
                setCorrectBackground(webView_option2!!)
            }
            if (optionClicked == 2) {
                Log.e("change question","check answer...2.");
                setCorrectBackground(webView_option3!!)
            }
            if (optionClicked == 3) {
                Log.e("change question","check answer...3..");
                setCorrectBackground(webView_option4!!)
            }
            setInactiveBackgroundNew();
        }else{
            if (optionClicked == 0) {
                Log.e("change question","check answer...0.");
                setCorrectBackground(webView_option1!!)
            }
            if (optionClicked == 1) {
                Log.e("change question","check answer...1.");
                setCorrectBackground(webView_option2!!)
            }
            setInactiveBackgroundNew()
        }

    }

    private fun checkwebviewborder(answer: String){
        //Log.e("test question actvitiy","..checkwebviewborder....optionClicked..."+optionClicked)
        /*next_btn.isEnabled = true
        next_btn.setBackground(ContextCompat.getDrawable(this, R.drawable.prev_next_active_btn));
        txt_next.setTextColor(getResources().getColor(R.color.button_close_text))
        ImageViewCompat.setImageTintList(right_arrow, ColorStateList.valueOf(getResources().getColor(R.color.button_close_text)));
*/
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
    }


    private fun checkAnswer(optionClicked: Int, answer: String) {
        //btn_hint!!.visibility = View.VISIBLE
        Log.e("Test Question activity","listofoptions........"+listOfOptions+".........listofoptions size....."+ listOfOptions!!.size);
        if (!isLifeZero) {
            if (!isAnswerCorrect) {
                Log.d("2", "!" + isAnswerCorrect.toString())
                if (countQuestion == 0) {
                    isFirstAnswerGiven = true
                }
                dbAttempts++
                if (listOfOptions!!.size > 2) {
                    Log.d("listOfOptions", "!" + listOfOptions!!.size.toString())
                    if (listOfOptions!!.get(optionClicked).contains("opt1")) {
                        if (countQuestion + 1 == totalQuestion) {
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

                     //   availableLife--
                        checkLife(availableLife)

                    }
                } else {
                    Log.e("Test Question activity","questionsItem........"+questionsItem+".........questionsItem size....."+ questionsItem!!.size+"....random position..."+randomPosition);
                    if (questionsItem!!.size > 1) {
                        if (answer.equals(questionsItem!!.get(randomPosition).text, true)) {
                            isAnswerCorrect = true
                            isDbCorrectAnswer = "true"
                            if (countQuestion + 1 == totalQuestion) {
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

                          //  availableLife--
                            checkLife(availableLife)

                        }
                    } else {
                        if (answer.equals(questionsItem!!.get(0).text, true)) {
                            isAnswerCorrect = true
                            isDbCorrectAnswer = "true"
                            if (countQuestion + 1 == totalQuestion) {
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

                           // availableLife--
                            checkLife(availableLife)

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
            sound = sharedPrefs?.getBooleanPrefVal(this, SOUNDS) ?: true
            if(!sound){
                /*var soundID: Int = 0
                var volume: Float = 1.0f

                // Getting the user sound settings
                val audioManager = this.getSystemService(AUDIO_SERVICE) as AudioManager
                val actualVolume = audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
                val maxVolume = audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
                volume = actualVolume / maxVolume
                //mediaPlayer = MediaPlayer.create(this,R.raw.select_high_correct)
                //mediaPlayer.start()
                var soundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 1)
                soundID = soundPool.load(this, R.raw.select_high_correct, 1)
                soundPool.setOnLoadCompleteListener(SoundPool.OnLoadCompleteListener { soundPool, sampleId, status ->
                    //loaded = true
                    soundPool.play(soundID, volume, volume, 1, 0, 1f);
                })*/

                if (Utils.loaded) {
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound");
                     //Toast.makeText(this,"right answer", Toast.LENGTH_SHORT).show()
                }



            }
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
            sound = sharedPrefs?.getBooleanPrefVal(this, SOUNDS) ?: true
            var soundID: Int = 0
            var volume: Float = 1.0f
            if(!sound){
                // Getting the user sound settings
               /* val audioManager = this.getSystemService(AUDIO_SERVICE) as AudioManager
                val actualVolume = audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
                val maxVolume = audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
                volume = actualVolume / maxVolume
               // mediaPlayer = MediaPlayer.create(this,R.raw.bounce_high_wrong)
               // mediaPlayer.start()
                var soundPool = SoundPool(10, AudioManager.STREAM_MUSIC, 1)
                soundID = soundPool.load(this, R.raw.bounce_high_wrong, 1)
                soundPool.setOnLoadCompleteListener(SoundPool.OnLoadCompleteListener { soundPool, sampleId, status ->
                    //loaded = true
                    soundPool.play(soundID, volume, volume, 1, 0, 1f);
                })*/
                if (Utils.loaded) {
                    //Utils.soundPool2.play(Utils.soundID2, Utils.volume, Utils.volume, 1, 0, 1f);
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound");
                    // Toast.makeText(this,"end", Toast.LENGTH_SHORT).show()
                }




            }
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
        webView.setBackgroundResource(R.drawable.option_correct_curved_border)
        /*handler.postDelayed(object : Runnable {
            override fun run() {
                isHandlerExecuted = true
                webView.setBackgroundResource(R.drawable.option_correct_curved_border)
            }

        }, 1500)*/
        //webView.setBackgroundResource(R.drawable.option_correct_green_border)
       // Utils.transition(applicationContext, webView, true)
        //webView!!.loadUrl("javascript:document.getElementsByTagName('html')[0].innerHTML+='<style>*{color:#cdcdcd}</style>';")

    }

    private fun setInactiveBackgroundNew() {
        Log.e("test question activity","unAnsweredList......"+unAnsweredList!!.size);
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
                    if(!isDialogOpen){
                        isDialogOpen = true
                        wrongAnswerAlertDialog()
                    }

                }

            }

        }, 500)
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
                .load(R.drawable.inactive_heart)
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
                .load(R.drawable.inactive_heart)
                .into(iv_life1)
            isLifeZero = true
            /*btn_next.text = "DONE"
            btn_next.visibility = View.VISIBLE*/
            /*navigateToSummaryScreen(false)*/
        }
    }

    private fun navigateToSummaryScreenNew(){
        /*val bundle = Bundle()
        bundle.putString("Category", "Quiz")
        bundle.putString("Action", "QuizSubmit")
        bundle.putString("Label", topicName)
        firebaseAnalytics?.logEvent("QuizPlay", bundle)*/

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, topicName)
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Quiz")
        // bundle.putString("Label", "TestGo")
        firebaseAnalytics?.logEvent("QuizSubmit", bundle)

        val intent = Intent(this, QuizSummaryActivityNew::class.java)
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
        intent.putExtra(CARD_NO, readyCardNumber)
        intent.putExtra("DISPLAY_NO", displayno)
        intent.putExtra("LAST_PLAYED", lastplayed)
        intent.putExtra("comingfrom", comingfrom)

        startActivity(intent)
        finish()
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
        intent.putExtra(CARD_NO, readyCardNumber)
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

        val tv_quit = dialogView.findViewById(R.id.tv_quit1) as Button
        val tv_return = dialogView.findViewById(R.id.tv_return1) as Button
        tv_quit.setOnClickListener {
            if (countQuestion >= 0) {
                if (isFirstAnswerGiven) {
                    addAllDataInDb(false, true)
                }
            }
            /*val bundle = Bundle()
            bundle.putString("Category", "Quiz")
            bundle.putString("Action", "QuizQuit")
            bundle.putString("Label", topicName)
            firebaseAnalytics?.logEvent("QuizPlay", bundle)*/


            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, topicName)
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Quiz")
            // bundle.putString("Label", "TestGo")
            firebaseAnalytics?.logEvent("QuizQuit", bundle)
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
                .load(R.drawable.inactive_heart
                )
                .into(view)
        }

    }
}
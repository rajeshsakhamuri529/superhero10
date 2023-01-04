package com.yomplex.simple.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yomplex.simple.R
import com.yomplex.simple.Service.BooksDownloadService
import com.yomplex.simple.Service.QuickBooksDownloadService
import com.yomplex.simple.database.QuizGameDataBase
import com.yomplex.simple.model.*
import com.yomplex.simple.utils.ConstantPath
import com.yomplex.simple.utils.SharedPrefs
import com.yomplex.simple.utils.Utils
import kotlinx.android.synthetic.main.activity_open_book.*
import kotlinx.android.synthetic.main.activity_quiz_time_summary.*
import kotlinx.android.synthetic.main.layout_dialog_start_quiz.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class OpenBookActivity : BaseActivity() {

    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    var title:String=""
    var foldername:String=""
    var category:String=""
    var fragmentname:String=""
    private var mPDialog: ProgressDialog? = null
    override var layoutID: Int = R.layout.activity_open_book
    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 1000 //3 seconds
    var databaseHandler: QuizGameDataBase?= null
    var dialog: Dialog? = null;

    var jsonStringBasic: String? =""
    var courseId: String?=""
    var courseName: String?=""
    var localPath: String?= null
    var gradeTitle: String?= null
    private var totalQuestion: Int? = null
    lateinit var circles: Array<ImageView?>
    private var branchesItemList:List<BranchesItem>?=null
    lateinit var testQuiz: TestQuiz
    var newcount:Int = 0
    lateinit var challenge: Challenge
    var newfolderName: String? = null
    var newreaddata: String? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    internal val mRunnable: Runnable = Runnable {
        //if(!isDataFromFirebase){
            showProgressDialog("Please wait...");
        //}

    }
    override fun onResume() {
        super.onResume()
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, "BookView " + title)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "OpenBookActivity")
        }
    }
    override fun initView() {
        appBarID.elevation = 20F
        sharedPrefs = SharedPrefs()
        firebaseAnalytics = Firebase.analytics
        Log.e("open book activity", "initView...");
        //Initialize the Handler
      //  mDelayHandler = Handler()
        //Navigate with delay
      //  mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
        fragmentname = intent.getStringExtra("fragmentname")
        //gradeTitle = arguments!!.getString(ConstantPath.TITLE_TOPIC)!!
        showProgressDialog("Please wait...");
        databaseHandler = QuizGameDataBase(this@OpenBookActivity);
        backRL.setOnClickListener {
            sound = sharedPrefs?.getBooleanPrefVal(this!!, ConstantPath.SOUNDS) ?: true
            if(!sound) {
                //MusicManager.getInstance().play(context, R.raw.amount_low);
                // Is the sound loaded already?
                if (Utils.loaded) {
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound...volume..." + Utils.volume);
                    //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                }
                // finish()
                val i = Intent(this, DashBoardActivity::class.java)
                if(fragmentname.equals("books")){
                    i.putExtra("fragment", "books")
                }else{
                    i.putExtra("fragment", "quickbooks")
                }

                startActivity(i)

            }else{
                val i = Intent(this, DashBoardActivity::class.java)
                if(fragmentname.equals("books")){
                    i.putExtra("fragment", "books")
                }else{
                    i.putExtra("fragment", "quickbooks")
                }
                startActivity(i)
                //finish()
            }
        }

        title = intent.getStringExtra("title")

        tv_title.text = title
        category = intent.getStringExtra("category")

        foldername = intent.getStringExtra("foldername")
        webView!!.setOnLongClickListener {
            true
        }
        webView!!.setLongClickable(false)
        // Below line prevent vibration on Long click
        webView!!.setHapticFeedbackEnabled(false);
        webView!!.settings.javaScriptEnabled = true

       //webView!!.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
       // webView!!.setScrollbarFadingEnabled(true);
      //  webView!!.setVerticalScrollBarEnabled(true);
        val dirpath = File((getCacheDir())!!.absolutePath)
        var dirFile1: File? = null
        if(fragmentname.equals("books")){
            dirFile1 = File(dirpath, "Books/" + category + "/" + foldername + "/index.html")
        }else{
            dirFile1 = File(dirpath, "QuickBooks/" + category + "/" + foldername + "/index.html")
        }


        //var bookpath = File()
        Log.e("open book activity", "dirFile1.absolutePath......" + dirFile1.absolutePath)
        webView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                hideProgressDialog()
            }
        })
        if(dirFile1.exists()){
            webView!!.loadUrl(ConstantPath.WEBVIEW_FILE_PATH + dirFile1.absolutePath)
        }else{
            if(fragmentname.equals("books")){
                downloadServiceFromBackground(this@OpenBookActivity, db)
            }else{
                downloadServiceFromBackground1(this@OpenBookActivity, db)
            }

            webView!!.loadUrl(ConstantPath.WEBVIEW_PATH + "Books1/" + category + "/" + foldername + "/index.html")
        }


        webView.addJavascriptInterface(object : Any() {
            @JavascriptInterface // For API 17+
            fun performClick(strl: String) {
                //stringVariable = strl
                var count = databaseHandler!!.getCoursesCountNew(category)
                Log.e("open book activity", "strl......." + strl);
                //Toast.makeText(this@OpenBookActivity, strl, Toast.LENGTH_SHORT).show()
                showDialogorQuiz(category, count, "normal");
            }
        }, "quiz")

    }

    private fun showDialogorQuiz(topicName: String, count: Int, coming: String) {
        dialog = Dialog(this@OpenBookActivity)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(true)
        dialog!!.setTitle("Info")
        dialog!!.setContentView(R.layout.layout_dialog_start_quiz)

        val quiztitle = dialog!!.findViewById(R.id.tv_quiz_title) as TextView
        val quetxt = dialog!!.findViewById(R.id.quetxt) as TextView

        val startbtn = dialog!!.findViewById(R.id.btn_start1) as Button
        val backbtn = dialog!!.findViewById(R.id.btn_back) as ConstraintLayout

        val totalLL = dialog!!.findViewById(R.id.totalLL) as RelativeLayout
        val tv_correct = dialog!!.findViewById(R.id.tv_correct) as TextView
        val ll_answers = dialog!!.findViewById(R.id.ll_answers) as LinearLayout
        val summaryTxt = dialog!!.findViewById(R.id.summaryTxt) as TextView
        val btn_review = dialog!!.findViewById(R.id.btn_review) as Button

        quiztitle.setText(topicName)
        if(coming.equals("quizsubmit")){
            totalLL.visibility = View.VISIBLE
            btn_review.visibility = View.VISIBLE
            startbtn.visibility = View.GONE
            quetxt.visibility = View.GONE


            testQuiz = databaseHandler!!.getQuizTopicsForTimerLastPlayed(topicName!!.toLowerCase())
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val currentDate = sdf.format(Date())

            var questionanswers:String = databaseHandler!!.getQuizQuestionAnswersFinal(testQuiz.title, currentDate, testQuiz.lastplayed, testQuiz.testtype);
            var queans:List<String> = questionanswers.split(",")

            circles = arrayOfNulls<ImageView>(totalQuestion!!)
            ll_answers.removeAllViews()

            for(i in 0 until totalQuestion!!){
                var ans1:List<String> = queans.get(i).split("~")
                if(ans1[1].equals("opt1")){
                    val params = LinearLayout.LayoutParams(getResources().getDimension(R.dimen._18sdp).toInt(), getResources().getDimension(R.dimen._18sdp).toInt())
                    if(i != 0){
                        params.leftMargin = getResources().getDimension(R.dimen._17sdp).toInt()
                    }

                    circles[i] = ImageView(this)
                    //isallcorrect = true
                    newcount++
                    Glide.with(this)
                            .load(R.drawable.ic_tick_green)
                            .into(circles[i]!!)
                    //ImageViewCompat.setImageTintList(circles[i]!!, ColorStateList.valueOf(getResources().getColor(R.color.right_tick)));
                    circles[i]!!.layoutParams = params
                    ll_answers.addView(circles!![i])
                }else{
                    val params = LinearLayout.LayoutParams(getResources().getDimension(R.dimen._18sdp).toInt(), getResources().getDimension(R.dimen._18sdp).toInt())
                    if(i != 0){
                        params.leftMargin = getResources().getDimension(R.dimen._17sdp).toInt()
                    }

                    circles[i] = ImageView(this)
                    //isallcorrect = false

                    Glide.with(this)
                            .load(R.drawable.wrong_ans_circle)
                            .into(circles[i]!!)

                    circles[i]!!.layoutParams = params
                    ll_answers.addView(circles!![i])
                }
            }

            var recordexistcount = databaseHandler!!.getQuizScore(getWeekOfYear(), topicName!!.toLowerCase())
            Log.e("test summary", "recordexistcount....." + recordexistcount);
            if(recordexistcount < 0){
                var quizScore:QuizScore
                quizScore = QuizScore("" + getWeekOfYear(), "" + newcount, topicName!!.toLowerCase())
                databaseHandler!!.insertQuizPlayScore(quizScore)
            }else{
                if(newcount > recordexistcount){
                    databaseHandler!!.updateQuizPlayScore(getWeekOfYear(), topicName!!.toLowerCase(), newcount)
                }

            }

            if(newcount == totalQuestion){
                // firebaseAnalytics.setCurrentScreen(this, "TestSuccess", null /* class override */)
                //successRL.background = resources.getDrawable(R.drawable.quiz_time_success)
                tv_correct.setTextColor(resources.getColor(R.color.button_close_text))

                val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                var recordcount = databaseHandler!!.getChallengeForDate(format.format(Utils.date), testQuiz.testtype)

                if(recordcount == 0){


                    challenge = Challenge(format.format(Utils.date), -1, -1, newcount, 1, testQuiz.testtype)
                    databaseHandler!!.insertChallenge(challenge)
                }else{
                    var teststatus = databaseHandler!!.getChallengeForTestStatus(format.format(Utils.date), testQuiz.testtype)
                    if(teststatus != 1){
                        databaseHandler!!.updateChallengeTest(format.format(Utils.date), newcount, 1, testQuiz.testtype)
                    }

                }

                var weeklycount = databaseHandler!!.getChallengeForWEEKLY(format.format(getWeekStartDate()), format.format(getWeekEndDate()), testQuiz.testtype)
                if(weeklycount == 0){
                    databaseHandler!!.insertChallengeWeekly(format.format(getWeekStartDate()), format.format(getWeekEndDate()), 1, testQuiz.testtype)
                }else{
                    var passcount = databaseHandler!!.getChallengeWeeklystatus(format.format(getWeekStartDate()), format.format(getWeekEndDate()), testQuiz.testtype)
                    databaseHandler!!.updateChallengeweeklystatus(format.format(getWeekStartDate()), format.format(getWeekEndDate()), (passcount + 1), testQuiz.testtype)

                }



            }else{
                //  firebaseAnalytics.setCurrentScreen(this, "TestFail", null /* class override */)
                //successRL.background = resources.getDrawable(R.drawable.quiz_time_failure)
                tv_correct.setTextColor(resources.getColor(R.color.test_fail))

                val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                var recordcount = databaseHandler!!.getChallengeForDate(format.format(Utils.date), testQuiz.testtype)

                if(recordcount == 0){
                    var testStatus:Int = -1
                    if(count >= 2){
                        testStatus = 1
                    }else{
                        testStatus = 0
                    }

                    challenge = Challenge(format.format(Utils.date), -1, -1, newcount, testStatus, testQuiz.testtype)
                    databaseHandler!!.insertChallenge(challenge)
                }else{
                    var teststatus = databaseHandler!!.getChallengeForTestStatus(format.format(Utils.date), testQuiz.testtype)
                    if(teststatus != 1){
                        var testStatus:Int = -1
                        if(count >= 2){
                            testStatus = 1
                        }else{
                            testStatus = 0
                        }
                        databaseHandler!!.updateChallengeTest(format.format(Utils.date), newcount, testStatus, testQuiz.testtype)
                    }

                }

            }

            if(newcount == 0){
                tv_correct.text = ""+newcount
                totalLL.background = resources.getDrawable(R.drawable.test_summary_0_0)
                summaryTxt.text = "oops!"
                tv_correct.setTextColor(resources.getColor(R.color.seriously))
                summaryTxt.setTextColor(resources.getColor(R.color.seriously))
            }else if(newcount == 1){
                tv_correct.text = ""+newcount
                totalLL.background = resources.getDrawable(R.drawable.test_summary_0_0)
                summaryTxt.text = "better than 0"
                tv_correct.setTextColor(resources.getColor(R.color.not_good))
                summaryTxt.setTextColor(resources.getColor(R.color.not_good))
            }else if(newcount == 2){
                tv_correct.text = ""+newcount
                totalLL.background = resources.getDrawable(R.drawable.test_summary_2_2)
                summaryTxt.text = "not bad"
                tv_correct.setTextColor(resources.getColor(R.color.not_bad))
                summaryTxt.setTextColor(resources.getColor(R.color.not_bad))
            }else if(newcount == 3){
                tv_correct.text = ""+newcount
                totalLL.background = resources.getDrawable(R.drawable.test_summary_3_3)
                summaryTxt.text = "pretty good!"
                tv_correct.setTextColor(resources.getColor(R.color.good))
                summaryTxt.setTextColor(resources.getColor(R.color.good))

            }else if(newcount == 4){
                tv_correct.text = ""+newcount
                tv_correct.setTextColor(resources.getColor(R.color.white))
                totalLL.background = resources.getDrawable(R.drawable.test_summary_4_4)
                summaryTxt.text = "superrr!"
                summaryTxt.setTextColor(resources.getColor(R.color.perfect))

            }


        }else{
            totalLL.visibility = View.GONE
            btn_review.visibility = View.GONE
            startbtn.visibility = View.VISIBLE
            quetxt.visibility = View.VISIBLE

            summaryTxt.text = "ready?"
            summaryTxt.setTextColor(resources.getColor(R.color.ready))
        }


        startbtn.setOnClickListener {
            sound = sharedPrefs?.getBooleanPrefVal(this@OpenBookActivity, ConstantPath.SOUNDS) ?: true
            if(!sound){
                // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                //  mediaPlayer.start()
                if (Utils.loaded) {
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound...volume..." + Utils.volume);
                    //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                }
            }
            newcount = 0
            loadQuiz(topicName, count)
            dialog!!.dismiss()
        }

        btn_review.setOnClickListener {
            sound = sharedPrefs?.getBooleanPrefVal(this@OpenBookActivity, ConstantPath.SOUNDS) ?: true
            if(!sound){
                // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                //  mediaPlayer.start()
                if (Utils.loaded) {
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound...volume..." + Utils.volume);
                    //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                }
            }

            val sdf = SimpleDateFormat("dd-MM-yyyy")
            val currentDate = sdf.format(Date())



            val intent = Intent(this, TestReviewActivity::class.java)
            intent.putExtra(ConstantPath.DYNAMIC_PATH, jsonStringBasic)
            intent.putExtra(ConstantPath.COURSE_ID, courseId)
            intent.putExtra(ConstantPath.TOPIC_ID, "")
            intent.putExtra(ConstantPath.COURSE_NAME, courseName)
            intent.putExtra(ConstantPath.TOPIC_LEVEL, "")
            intent.putExtra(ConstantPath.TOPIC_NAME, topicName)
            intent.putExtra(ConstantPath.LEVEL_COMPLETED, "")
            // intent.putExtra(ConstantPath.TOPIC_POSITION, position!!)
            intent.putExtra(ConstantPath.FOLDER_PATH, localPath)
            intent.putExtra(ConstantPath.FOLDER_NAME, newfolderName)
            intent.putExtra(ConstantPath.TITLE_TOPIC, "")
            intent.putExtra(ConstantPath.CARD_NO, "")
            intent.putExtra(ConstantPath.QUIZ_COUNT, totalQuestion)

            intent.putExtra("title", testQuiz.title)
            intent.putExtra("playeddate", currentDate)
            intent.putExtra("comingfrom", coming)
            intent.putExtra("lastplayed", testQuiz.lastplayed)
            intent.putExtra("readdata", newreaddata)

            startActivity(intent)

            //dialog!!.dismiss()
        }




        //buttonEffect(btn_gotIt,false)
        // alertDialog = dialogBuilder.create()
        backbtn.setOnClickListener {
            sound = sharedPrefs?.getBooleanPrefVal(this@OpenBookActivity, ConstantPath.SOUNDS) ?: true
            if(!sound){
                // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                //  mediaPlayer.start()
                if (Utils.loaded) {
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound...volume..." + Utils.volume);
                    //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                }
            }
            dialog!!.dismiss()
        }


        dialog!!.show()

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        var valueInPixels = resources.getDimension(R.dimen._40sdp)
        var valueInPixels1 = resources.getDimension(R.dimen._100sdp)

        var layoutParams =  WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog!!.getWindow()!!.getAttributes());
        layoutParams.width = (width - valueInPixels).toInt();
        layoutParams.height = (height - valueInPixels1).toInt();
        dialog!!.getWindow()!!.setAttributes(layoutParams);
      //  val window: Window = dialog!!.getWindow()!!
      //  window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)


    }

    fun getWeekOfYear(): Int{
        val calendar = Calendar.getInstance()
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(Utils.date)
        return calendar.get(Calendar.WEEK_OF_YEAR)
    }

    fun getWeekStartDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.setTime(Utils.date)
        while (calendar.get(Calendar.DAY_OF_WEEK) !== Calendar.MONDAY) {
            calendar.add(Calendar.DATE, -1)
        }
        return calendar.time
    }

    fun getWeekEndDate(): Date {
        val calendar = Calendar.getInstance()
        calendar.setTime(Utils.date)
        if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY){
            calendar.add(Calendar.DATE, 7)
        }else {
            while (calendar.get(Calendar.DAY_OF_WEEK) !== Calendar.MONDAY) {
                calendar.add(Calendar.DATE, 1)
            }
        }

        calendar.add(Calendar.DATE, -1)
        return calendar.time
    }


    private fun loadQuiz(topicName: String, count: Int){
        var filename = ""
        var firestoreversionkey=""
        var originalfilename = topicName
        if (topicName.equals("CALCULUS 1")) {
            filename = "jee-calculus-1"
            firestoreversionkey = "Calculus1Version"
        } else if (topicName.equals("CALCULUS 2")) {
            filename = "jee-calculus-2"
            firestoreversionkey = "Calculus2Version"
        } else if (topicName.equals("ALGEBRA")) {
            filename = "ii-algebra"
            firestoreversionkey = "AlgebraVersion"
        } else if (topicName.equals("OTHER")) {
            filename = "other"
            firestoreversionkey = "BasicVersion"
        } else if (topicName.equals("GEOMETRY")) {
            filename = "iii-geometry"
            firestoreversionkey = "GeometryVersion"
        }
        Log.e("test fragment", "on click filename...." + filename)
        var topicname = topicName.replace("\\s".toRegex(), "")

        var downloadstatus:Int = -1
        var testcontentlist: List<TestDownload>? = databaseHandler!!.gettestContent()
        for(i in 0 until testcontentlist!!.size) {
            if (testcontentlist.get(i).testtype.equals(topicname.toLowerCase())) {
                downloadstatus = testcontentlist.get(i).testdownloadstatus
                break
            }
        }

        if(downloadstatus == 1){
            // Toast.makeText(context,"download status 1...read from local...",Toast.LENGTH_SHORT).show()
            val dirFile = File(getCacheDir(), topicname.toLowerCase() + "/" + filename)
            val size = File(getCacheDir(), topicname.toLowerCase() + "/" + filename)
                    .walkTopDown()
                    .map { it.length() }
                    .sum() // in bytes
            Log.e("test fragment", "folder size......." + size);
            Log.e("test fragment", "dirFile path......." + dirFile.absolutePath);
            if(dirFile.isDirectory()){
                var files = dirFile.list();
                Log.e("test fragment", "files size......." + files.size);
                if (files.size == 0) {
                    //directory is empty
                    Log.e("test fragment", "files.size......empty.");
                    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                    val isConnected: Boolean = activeNetwork?.isConnected == true

                    databaseHandler!!.updatetestcontentdownloadstatus(0, topicname.toLowerCase())
                    if(isNetworkConnected()) {
                        downloadServiceFromBackground(this@OpenBookActivity, db)
                    }
                    readFileFromAssetsNew(topicname.toLowerCase(), filename, originalfilename)

                }else{
                    Log.e("test fragment", "files.size.....not...empty.");
                    readFileLocally(topicname.toLowerCase(), filename, originalfilename, count)
                    //readFileFromAssetsNew(topicname.toLowerCase(),filename,originalfilename)



                }
            }else{
                databaseHandler!!.updatetestcontentdownloadstatus(0, topicname.toLowerCase())
                if(isNetworkConnected()) {
                    downloadServiceFromBackground(this@OpenBookActivity, db)
                }
                readFileFromAssetsNew(topicname.toLowerCase(), filename, originalfilename)
            }




        }else{
            //  Toast.makeText(context,"download status 0...read from assets...",Toast.LENGTH_SHORT).show()
            // databaseHandler!!.updatetestcontentdownloadstatus(0,topicname.toLowerCase())
            if(isNetworkConnected()) {
                downloadServiceFromBackground(this@OpenBookActivity, db)
            }
            //readFileLocally(topicname.toLowerCase(),filename,originalfilename,count)
            readFileFromAssetsNew(topicname.toLowerCase(), filename, originalfilename)
            //gotoStartScreenThroughAssets(topicname,originalfilename)
        }




    }

    private fun readFileFromAssetsNew(topicname: String, filename: String, originalfilename: String){
        //Toast.makeText(activity,"content read from assets",Toast.LENGTH_SHORT).show()
        val courseJsonString = loadJSONFromAsset(topicname + "/" + filename + "/" + "Courses.json")
        Log.d("courseJsonString", courseJsonString + "!");
        /*val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( assetTestCoursePath + "topic.json")*/
        val gsonFile = Gson()
        val courseType = object : TypeToken<List<CoursesResponseModel>>() {}.type
        val courseResponseModel: ArrayList<CoursesResponseModel> = gsonFile
                .fromJson(courseJsonString, courseType)
        courseId = courseResponseModel[0].id
        courseName = courseResponseModel[0].syllabus.title
        Log.e("test fragment", "readFileFromAssets.....courseName...." + courseName);
        // tv_class.text = courseName
        // tv_class_board.text = courseResponseModel[0].syllabus.displayTitle
        localPath = "$topicname/$filename/$courseName/"
        Log.e("test fragment", "readFileFromAssets.....localPath...." + localPath);
        // val jsonString = readFromFile(localPath +"topic.json")
        val jsonString = loadJSONFromAsset(localPath + "topic.json")
        Log.d("jsonString", jsonString);
        val topicType = object : TypeToken<TopicResponseModel>() {}.type
        val topicResponseModel: TopicResponseModel = gsonFile.fromJson(jsonString, topicType)

        branchesItemList = topicResponseModel.branches
        sharedPrefs?.setIntPrefVal(this@OpenBookActivity, ConstantPath.TOPIC_SIZE, branchesItemList!!.size)




        var playCount = databaseHandler!!.getPlayCountPlayRecord(filename)


        var folderPath = localPath+playCount.getTopic()
        Log.e("test fragment", "testQuiz.folderPath......" + folderPath)
        jsonStringBasic = loadJSONFromAsset("$folderPath/${playCount.getLevel()}.json")
        Log.e("test fragment", "jsonStringBasic......" + jsonStringBasic)


        Log.e("start test", "on click...topicname...." + topicname);
        databaseHandler!!.deleteAllQuizTopicsLatPlayed(topicname!!.toLowerCase())

        databaseHandler!!.insertquiztopiclastplayed(playCount.getTopic(), 0, playCount.getLevel(), topicname!!.toLowerCase());
        /*var filename = ""
        if (originalfilename.equals("CALCULUS 1")) {
            filename = "jee-calculus-1"
            //firestoreversionkey = "Calculus1Version"
        } else if (originalfilename.equals("CALCULUS 2")) {
            filename = "jee-calculus-2"
            //firestoreversionkey = "Calculus2Version"
        } else if (originalfilename.equals("ALGEBRA")) {
            filename = "ii-algebra"
            //firestoreversionkey = "AlgebraVersion"
        } else if (originalfilename.equals("OTHER")) {
            filename = "other"
            //firestoreversionkey = "BasicVersion"
        } else if (originalfilename.equals("GEOMETRY")) {
            filename = "iii-geometry"
            //firestoreversionkey = "GeometryVersion"
        }*/
        /*Log.e("start test","readdata......"+readdata)
        if(readdata.equals("files")){
            var playCount = databaseHandler!!.getPlayCountPlayRecord(filename)
            Log.e("start test","playCount.getCourse()......"+playCount.getCourse())
            Log.e("start test","playCount.getTopic()......"+playCount.getTopic())
            Log.e("start test","playCount.getLevel()......"+playCount.getLevel())
            databaseHandler!!.updatePlayCount(playCount.getPlaycount()+1,playCount.getCourse(),playCount.getTopic(),playCount.getLevel())
        }else{*/
           // var playCount = databaseHandler!!.getPlayCountPlayRecord(filename)
            databaseHandler!!.updatePlayCount(playCount.getPlaycount() + 1, playCount.getCourse(), playCount.getTopic(), playCount.getLevel())
       // }

        val intent = Intent(this!!, TestQuizActivity::class.java)
        //intent.putExtra(ConstantPath.TOPIC, topic)
        intent.putExtra(ConstantPath.TOPIC_NAME, topicname)
        intent.putExtra(ConstantPath.FOLDER_NAME, playCount.getTopic())
        intent.putExtra(ConstantPath.DYNAMIC_PATH, jsonStringBasic)
        intent.putExtra(ConstantPath.COURSE_ID, courseId)
        intent.putExtra(ConstantPath.COURSE_NAME, courseName)
        intent.putExtra(ConstantPath.TOPIC_ID, "")
        // intent.putExtra(ConstantPath.TOPIC_POSITION, dbPosition)
        intent.putExtra(ConstantPath.FOLDER_PATH, localPath)
        intent.putExtra(ConstantPath.TITLE_TOPIC, "")
        intent.putExtra("LAST_PLAYED", playCount.getLevel())
        intent.putExtra("comingfrom", "books")
        intent.putExtra(ConstantPath.TOPIC_LEVEL, "")
        intent.putExtra(ConstantPath.LEVEL_COMPLETED, "")
        intent.putExtra(ConstantPath.CARD_NO, "")
        intent.putExtra("readdata", "assets")
        // intent.putExtra("DISPLAY_NO", topic.displayNo)
        intent.putExtra("topicnameoriginal", originalfilename)
        startActivity(intent)

        //gotoStartScreenThroughAssets(topicname,originalfilename)
        /*val intent = Intent(this@OpenBookActivity, StartTestActivity::class.java)
        //intent.putExtra(ConstantPath.TOPIC, topic)
        intent.putExtra(ConstantPath.TOPIC_NAME, topicname)
        intent.putExtra("topicnameoriginal", originalfilename)
        intent.putExtra(ConstantPath.FOLDER_NAME, playCount.getTopic())
        intent.putExtra(ConstantPath.DYNAMIC_PATH, jsonStringBasic)
        intent.putExtra(ConstantPath.COURSE_ID, courseId)
        intent.putExtra(ConstantPath.COURSE_NAME, courseName)
        intent.putExtra(ConstantPath.TOPIC_ID, "")
        //intent.putExtra(ConstantPath.TOPIC_POSITION, topic.displayNo)
        intent.putExtra(ConstantPath.FOLDER_PATH, localPath)
        intent.putExtra(ConstantPath.TITLE_TOPIC, gradeTitle!!)
        intent.putExtra("LAST_PLAYED", playCount.getLevel())
        intent.putExtra("comingfrom", "Test")
        intent.putExtra("readdata", "assets")
        intent.putExtra(ConstantPath.TOPIC_LEVEL, "")
        intent.putExtra(ConstantPath.LEVEL_COMPLETED, "")
        intent.putExtra(ConstantPath.CARD_NO, "")
        startActivity(intent)*/




    }

    private fun readFileLocally(topicname: String, filename: String, originalfilename: String, count: Int) {
        //Toast.makeText(activity,"content read from local storage",Toast.LENGTH_SHORT).show()
        val dirFile = File(getCacheDir(), topicname + "/" + filename)
        Log.e("test fragment", "dir file....." + dirFile.absolutePath)
        val courseJsonString = Utils.readFromFile(dirFile.absolutePath + "/Courses.json")
        //val courseJsonString = readFromFile("$localBlobcityPath/Courses.json")
        Log.d("courseJsonString....1", courseJsonString + "!");
        //val courseJsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( ConstantPath.localBlobcityPath1 + "Courses.json")
        //val courseJsonString = readFromFile("$localBlobcityPath/Courses.json")
        //Log.d("courseJsonString",courseJsonString+"!");
        /*val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( assetTestCoursePath + "topic.json")*/
        val gsonFile = Gson()
        val courseType = object : TypeToken<List<CoursesResponseModel>>() {}.type
        try {
            val courseResponseModel: ArrayList<CoursesResponseModel> = gsonFile.fromJson(courseJsonString, courseType)
            courseId = courseResponseModel[0].id
            courseName = courseResponseModel[0].syllabus.title

        } catch (e: Exception) {

        }
        // tv_class.text = courseName
        // tv_class_board.text = courseResponseModel[0].syllabus.displayTitle
        localPath = "${dirFile.absolutePath}/$courseName/"
        // val jsonString = readFromFile(localPath +"topic.json")
        val jsonString = Utils.readFromFile(localPath + "topic.json")
        //Log.d("jsonString",jsonString);
        val topicType = object : TypeToken<TopicResponseModel>() {}.type
        val topicResponseModel: TopicResponseModel = gsonFile.fromJson(jsonString, topicType)

        branchesItemList = topicResponseModel.branches
        sharedPrefs?.setIntPrefVal(this@OpenBookActivity, ConstantPath.TOPIC_SIZE, branchesItemList!!.size)

        //gotoStartScreen(topicname,originalfilename)

        var playCount = databaseHandler!!.getPlayCountPlayRecord(filename)
        Log.e("testfragment", "playCount.getCourse()......" + playCount.getCourse())
        Log.e("testfragment", "playCount.getTopic()......" + playCount.getTopic())
        Log.e("testfragment", "playCount.getLevel()......" + playCount.getLevel())

        var folderPath = localPath + playCount.getTopic()
        Log.e("test fragment", "testQuiz.folderPath......" + folderPath)
        var pathexist = File(folderPath)
        if (pathexist.exists()) {
            jsonStringBasic = Utils.readFromFile("$folderPath/${playCount.getLevel()}.json")
            Log.e("test fragment", "jsonStringBasic......" + jsonStringBasic)

            //databaseHandler!!.updatePlayCount(playCount.getPlaycount()+1,playCount.getCourse(),playCount.getTopic(),playCount.getLevel())



            Log.e("start test", "on click...topicname...." + topicname);
            databaseHandler!!.deleteAllQuizTopicsLatPlayed(topicname!!.toLowerCase())

            databaseHandler!!.insertquiztopiclastplayed(playCount.getTopic(), 0, playCount.getLevel(), topicname!!.toLowerCase());
            /*var filename = ""
            if (originalfilename.equals("CALCULUS 1")) {
                filename = "jee-calculus-1"
                //firestoreversionkey = "Calculus1Version"
            } else if (originalfilename.equals("CALCULUS 2")) {
                filename = "jee-calculus-2"
                //firestoreversionkey = "Calculus2Version"
            } else if (originalfilename.equals("ALGEBRA")) {
                filename = "ii-algebra"
                //firestoreversionkey = "AlgebraVersion"
            } else if (originalfilename.equals("OTHER")) {
                filename = "other"
                //firestoreversionkey = "BasicVersion"
            } else if (originalfilename.equals("GEOMETRY")) {
                filename = "iii-geometry"
                //firestoreversionkey = "GeometryVersion"
            }*/
            //Log.e("start test","readdata......"+readdata)
            //if(readdata.equals("files")){
               // var playCount = databaseHandler!!.getPlayCountPlayRecord(filename)
                Log.e("start test", "playCount.getCourse()......" + playCount.getCourse())
                Log.e("start test", "playCount.getTopic()......" + playCount.getTopic())
                Log.e("start test", "playCount.getLevel()......" + playCount.getLevel())
                databaseHandler!!.updatePlayCount(playCount.getPlaycount() + 1, playCount.getCourse(), playCount.getTopic(), playCount.getLevel())
            /*}else{
                var playCount = databaseHandler!!.getPlayCountPlayRecord(filename)
                databaseHandler!!.updatePlayCount(playCount.getPlaycount()+1,playCount.getCourse(),playCount.getTopic(),playCount.getLevel())
            }*/

            /*val intent = Intent(this@OpenBookActivity, StartTestActivity::class.java)
            //intent.putExtra(ConstantPath.TOPIC, topic)
            intent.putExtra(ConstantPath.TOPIC_NAME, topicname)
            intent.putExtra("topicnameoriginal", originalfilename)
            intent.putExtra(ConstantPath.FOLDER_NAME, playCount.getTopic())
            intent.putExtra(ConstantPath.DYNAMIC_PATH, jsonStringBasic)
            intent.putExtra(ConstantPath.COURSE_ID, courseId)
            intent.putExtra(ConstantPath.COURSE_NAME, courseName)
            intent.putExtra(ConstantPath.TOPIC_ID, "")
            //intent.putExtra(ConstantPath.TOPIC_POSITION, topic.displayNo)
            intent.putExtra(ConstantPath.FOLDER_PATH, localPath)
            intent.putExtra(ConstantPath.TITLE_TOPIC, gradeTitle!!)
            intent.putExtra("LAST_PLAYED", playCount.getLevel())
            intent.putExtra("comingfrom", "Test")
            intent.putExtra("readdata", "files")
            intent.putExtra(ConstantPath.TOPIC_LEVEL, "")
            intent.putExtra(ConstantPath.LEVEL_COMPLETED, "")
            intent.putExtra(ConstantPath.CARD_NO, "")
            startActivity(intent)*/



            val intent = Intent(this!!, TestQuizActivity::class.java)
            //intent.putExtra(ConstantPath.TOPIC, topic)
            intent.putExtra(ConstantPath.TOPIC_NAME, topicname)
            intent.putExtra(ConstantPath.FOLDER_NAME, playCount.getTopic())
            intent.putExtra(ConstantPath.DYNAMIC_PATH, jsonStringBasic)
            intent.putExtra(ConstantPath.COURSE_ID, courseId)
            intent.putExtra(ConstantPath.COURSE_NAME, courseName)
            intent.putExtra(ConstantPath.TOPIC_ID, "")
            // intent.putExtra(ConstantPath.TOPIC_POSITION, dbPosition)
            intent.putExtra(ConstantPath.FOLDER_PATH, localPath)
            intent.putExtra(ConstantPath.TITLE_TOPIC, "")
            intent.putExtra("LAST_PLAYED", playCount.getLevel())
            intent.putExtra("comingfrom", "books")
            intent.putExtra(ConstantPath.TOPIC_LEVEL, "")
            intent.putExtra(ConstantPath.LEVEL_COMPLETED, "")
            intent.putExtra(ConstantPath.CARD_NO, "")
            intent.putExtra("readdata", "files")
            // intent.putExtra("DISPLAY_NO", topic.displayNo)
            intent.putExtra("topicnameoriginal", originalfilename)
            //startActivity(intent)
            startActivityForResult(intent, 100)


        } else {

            if (count == 0) {
                databaseHandler!!.updateCourseExist(originalfilename, 1)
                databaseHandler!!.updatetestcontentdownloadstatus(0, topicname)
                if (isNetworkConnected()) {
                    downloadServiceFromBackground(this@OpenBookActivity, db)
                }

                nocontentDialog("Oops! Something went wrong. Please wait while we update this Topic.")
            } else {
                if (isNetworkConnected()) {
                    databaseHandler!!.updateCourseExist(originalfilename, 1)
                    nocontentDialog("Give us another minute.. your content is being updated!")
                } else {
                    nocontentDialog("No internet connection " + getString(R.string.emoji_disappointment) + " Please connect to the internet and try again.")

                }
            }

            //readFileFromAssetsNew(topicname.toLowerCase(),filename,originalfilename)
        }
    }

    private fun nocontentDialog(msg: String) {
        val dialogBuilder = AlertDialog.Builder(this@OpenBookActivity, R.style.mytheme)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.content_not_available, null)
        dialogBuilder.setView(dialogView)

        val tv_message = dialogView.findViewById(R.id.tv_message) as TextView
        val tv_return = dialogView.findViewById(R.id.tv_return1) as Button

        tv_message.text = msg

        val alertDialog = dialogBuilder.create()

        /*val map = takeScreenShot(this);

        val fast = fastblur(map, 10);
        val draw = BitmapDrawable(getResources(), fast);*/
        tv_return.setOnClickListener {
            sound = sharedPrefs?.getBooleanPrefVal(this@OpenBookActivity, ConstantPath.SOUNDS) ?: true
            if(!sound){
                // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                //  mediaPlayer.start()
                if (Utils.loaded) {
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound...volume..." + Utils.volume);
                    //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                }
            }
            alertDialog.dismiss()
            val i = Intent(this@OpenBookActivity, DashBoardActivity::class.java)
            i.putExtra("fragment", "tests")
            startActivity(i)
            overridePendingTransition(0, 0)


        }
        //alertDialog.getWindow().setBackgroundDrawable(draw);
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()
    }

    private fun downloadServiceFromBackground(
            mainActivity: Activity, db: FirebaseFirestore
    ) {
        BooksDownloadService.enqueueWork(mainActivity, db)
    }
    private fun downloadServiceFromBackground1(
            mainActivity: Activity, db: FirebaseFirestore
    ) {
        QuickBooksDownloadService.enqueueWork(mainActivity, db)
    }

    fun showProgressDialog(loadText: String) {
        hideProgressDialog()
        try {
            mPDialog = ProgressDialog.show(
                    ContextThemeWrapper(this@OpenBookActivity, R.style.DialogCustom),
                    "",
                    loadText
            )
            mPDialog!!.setCancelable(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    fun hideProgressDialog() {
        try {
            if (mPDialog != null && mPDialog!!.isShowing()) {
                mPDialog!!.dismiss()
                mPDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        sound = sharedPrefs?.getBooleanPrefVal(this!!, ConstantPath.SOUNDS) ?: true
        if(!sound) {
            //MusicManager.getInstance().play(context, R.raw.amount_low);
            // Is the sound loaded already?
            if (Utils.loaded) {
                Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                Log.e("Test", "Played sound...volume..." + Utils.volume);
                //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
            }
        }
        val intent = Intent(this, DashBoardActivity::class.java)
        if(fragmentname.equals("books")){
            intent.putExtra("fragment", "books")
        }else{
            intent.putExtra("fragment", "quickbooks")
        }
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            Log.e("open book activity", "on activity result....")

            var coming = data!!.getStringExtra("coming")
            if(coming.equals("quizclose")){
                var originaltopicname = data!!.getStringExtra("topicnameoriginal")
                var count = databaseHandler!!.getCoursesCountNew(originaltopicname)

                showDialogorQuiz(originaltopicname, count, "normal");
            }else{
                var originaltopicname = data!!.getStringExtra("topicnameoriginal")
                Log.e("open book activity", "on activity result...coming...." + coming)
                Log.e("open book activity", "on activity result....originaltopicname..." + originaltopicname)

                var count = databaseHandler!!.getCoursesCountNew(originaltopicname)
                totalQuestion = data!!.getIntExtra(ConstantPath.QUIZ_COUNT, 0)
                newfolderName = data!!.getStringExtra(ConstantPath.FOLDER_NAME)
                newreaddata = data!!.getStringExtra("readdata")


                showDialogorQuiz(originaltopicname, count, coming);
            }



        }
    }
}
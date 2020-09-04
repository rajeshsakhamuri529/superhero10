package com.blobcity.fragment

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.R
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.blobcity.activity.DashBoardActivity
import com.blobcity.activity.SettingsActivity
import com.blobcity.activity.StartQuizActivityNew
import com.blobcity.activity.StartQuizTimerActivity
import com.blobcity.database.QuizGameDataBase
import com.blobcity.model.*
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.CustomTypefaceSpan
import com.blobcity.utils.SharedPrefs
import com.blobcity.utils.Utils
import com.bumptech.glide.Glide
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.home_fragment.view.*
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.text.ParseException
import java.util.*

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import kotlin.random.Random


class HomeFragment: Fragment(),View.OnClickListener {

    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    var jsonStringBasic: String? =""
    var courseId: String?=""
    var courseName: String?=""
    var localPath: String?= null
    var gradeTitle: String?= null
    private var branchesItemList:List<BranchesItem>?=null

    var challengelist: List<Challenge>? = null
    var databaseHandler: QuizGameDataBase?= null
    val days = arrayOfNulls<String>(6)
    val dayofweek = arrayOfNulls<String>(6)
    lateinit var circles: Array<TextView?>
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var isdownload:Boolean = false
    var version : String = ""
    var url : String = ""
    internal var mYear: Int = 0
    internal var mMonth:Int = 0
    internal var mDay:Int = 0
    private var randomPosition: Int = -1
    private var randomPos: Int = -1
    private var positionList: ArrayList<String>? = ArrayList()
    var alertDialog: AlertDialog? = null
    private lateinit var firebaseAnalytics: FirebaseAnalytics
     // variable to track event time
    var mLastClickTime:Long = 0;
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseHandler = QuizGameDataBase(context);
        sharedPrefs = SharedPrefs()
        gradeTitle = arguments!!.getString(ConstantPath.TITLE_TOPIC)!!
        view.home.elevation = 15F
        val firebasesettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        db.firestoreSettings = firebasesettings

        firebaseAnalytics = FirebaseAnalytics.getInstance(activity!!)

        firebaseAnalytics.setCurrentScreen(activity!!, "Home", null /* class override */)

       // quizRL.setOnClickListener(this)
       // testRL.setOnClickListener(this)
        view.dateRL.setOnClickListener(this)
        view.multi_quiz_go_btn.setOnClickListener(this)
        view.go_btn_multi_test.setOnClickListener(this)
        view.settings.setOnClickListener(this)
        positionList!!.add("basic.json")
        positionList!!.add("intermediate.json")
        Log.e("home fragment","sharedPrefs!!.getBooleanPrefVal(activity!!, \"isfirsttimeinstalled\")..."+sharedPrefs!!.getBooleanPrefVal(activity!!, "isfirsttimeinstalled"))
        /*if(!sharedPrefs!!.getBooleanPrefVal(activity!!, "isfirsttimeinstalled")){
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val calendar = Calendar.getInstance()
            //calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

            for (i in 0..5) {
                days[i] = format.format(calendar.time)
                calendar.add(Calendar.DATE, 1)
                Log.e("Days$i", "date :" + days[i])
                val simpleDateformat = SimpleDateFormat("E")
                var d = format.parse(days[i])
                Log.e("home fragment","day of week......"+simpleDateformat.format(d))
                dayofweek[i] = simpleDateformat.format(d)

            }
        }else{



        }
        Log.e("home fragment","day of week......"+dayofweek.size)*/
        view.multi_quiz_go_btn.isEnabled = true
        view.go_btn_multi_test.isEnabled = true


        val format = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

        view.tv_date.setText(""+format.format(Utils.date))
        //Utils.date = Date()
        var count = databaseHandler!!.getChallengeForDate(format.format(Date()))
        Log.e("home fragment","cahllenge......"+count)


        val sourceString = "Get a perfect Score in a QUIZ"
        view.quizscoretxt.setText(Html.fromHtml(sourceString))
        val face1: Typeface = Typeface.createFromAsset(activity!!.assets,"fonts/lato_bold.ttf")
        val face2: Typeface = Typeface.createFromAsset(activity!!.assets,"fonts/lato_black.ttf")

       // Typeface font = Typeface.createFromAsset(getAssets(), "Akshar.ttf");
       // Typeface font2 = Typeface.createFromAsset(getAssets(), "bangla.ttf");
        var  SS = SpannableStringBuilder("Score 2 or more in a TEST ");


        SS.setSpan (CustomTypefaceSpan("", face1), 0, 5,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        //SS.setSpan (face1, 0, 4, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        SS.setSpan (CustomTypefaceSpan("", face2), 5, 7,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        SS.setSpan (CustomTypefaceSpan("", face1), 7, 25,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        view.testscoretxt1.setText(SS);

        //val sourceString1 = "Score <b>2</b> or more in a TEST"
        //testscoretxt.setText(Html.fromHtml(sourceString1))


       /* val sourceString2 = "Click on <b>GO</b> to launch a random <b>QUIZ</b>. Or, go to the <b>Quiz Tab</b>, and selects a Topic."
        success_failure_txt.setText(Html.fromHtml(sourceString2))*/

        //val sourceString3 = "Score a <b>4</b> in four TESTS this week!"
        //bottom_txt.setText(Html.fromHtml(sourceString3))

        //val format = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        Log.e("home fragment","getWeekStartDate......"+format.format(getWeekStartDate()))
        Log.e("home fragment","getWeekEndDate......"+getWeekEndDate())

        var quizstatus = databaseHandler!!.getChallengeForQuizStatus(format.format(Utils.date))
        var teststatus = databaseHandler!!.getChallengeForTestStatus(format.format(Utils.date))
        if(quizstatus == 1){
            view.multi_quiz_go_btn.visibility = View.GONE
            view.multi_quiz_tick.visibility = View.VISIBLE
        }

        if(teststatus == 1){
            view.go_btn_multi_test.visibility = View.GONE
            view.multi_test_tick.visibility = View.VISIBLE
        }
        challengelist = databaseHandler!!.getChallengeFrombetweenDates(format.format(getWeekStartDate()),format.format(getWeekEndDate()))

        for(i in 0 until (challengelist as MutableList<Challenge>?)!!.size){
            var challenge = challengelist!!.get(i)

            val calendar = Calendar.getInstance()
            calendar.setTime(format.parse(challenge.date))
            var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            Log.e("home fragment","......day of week....."+dayOfWeek)
            updateTodayChallengeDone(dayOfWeek)
        }
        /*if(quizstatus == 1 && teststatus == 1){
            val calendar = Calendar.getInstance()
            calendar.setTime(Utils.date)
            var dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            Log.e("home fragment","......day of week....."+dayOfWeek)
            updateTodayChallengeDone(dayOfWeek)
       }*/



        var testcount = databaseHandler!!.getChallengeWeeklystatus(format.format(getWeekStartDate()),format.format(getWeekEndDate()))
        val face: Typeface = Typeface.createFromAsset(activity!!.assets,"fonts/lato_black.ttf")
        if(testcount >= 4){
            var  SS = SpannableStringBuilder("Awesome.. 4 x 4 challenge done! ");


            SS.setSpan (CustomTypefaceSpan("", face2), 0, 9,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //SS.setSpan (face1, 0, 4, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
           // SS.setSpan (CustomTypefaceSpan("", face1), 9, 15,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            SS.setSpan (CustomTypefaceSpan("", face1), 9, 31,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            view.bottom_txt1.setText(SS);
            circles = arrayOfNulls<TextView>(4)
            view.ll_test_status.removeAllViews()
            for(i in 0 until 4){
                val params = LinearLayout.LayoutParams(getResources().getDimension(R.dimen._20sdp).toInt(), getResources().getDimension(R.dimen._20sdp).toInt())
                if(i != 0){
                    params.leftMargin = getResources().getDimension(R.dimen._17sdp).toInt()
                }

                circles[i] = TextView(activity)

               /* Glide.with(this)
                    .load(R.drawable.four_test_success)
                    .into(circles[i]!!)

                circles[i]!!.layoutParams = params*/

                circles[i]!!.text = "4"
                circles[i]!!.gravity = Gravity.CENTER
                circles[i]!!.setTextColor(resources.getColor(R.color.white))
                circles[i]!!.background = resources.getDrawable(R.drawable.four_test_success)
                /*Glide.with(this)
                    .load(R.drawable.empty_circle)
                    .into(circles[i]!!)*/
                circles[i]!!.typeface = face
                circles[i]!!.layoutParams = params
                view.ll_test_status.addView(circles!![i])
            }
        }else{

            var  SS = SpannableStringBuilder("Score a 4 in four TESTS this week! ");


            SS.setSpan (CustomTypefaceSpan("", face1), 0, 7,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            //SS.setSpan (face1, 0, 4, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            SS.setSpan (CustomTypefaceSpan("", face2), 7, 9,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            SS.setSpan (CustomTypefaceSpan("", face1), 9, 34,Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            view.bottom_txt1.setText(SS);
            circles = arrayOfNulls<TextView>(4)
            for(i in 0 until 4) {
                if((i+1) <= testcount){
                    val params = LinearLayout.LayoutParams(
                        getResources().getDimension(R.dimen._20sdp).toInt(),
                        getResources().getDimension(R.dimen._20sdp).toInt()
                    )
                    if (i != 0) {
                        params.leftMargin = getResources().getDimension(R.dimen._17sdp).toInt()
                    }

                    circles[i] = TextView(activity)
                    circles[i]!!.text = "4"
                    circles[i]!!.gravity = Gravity.CENTER
                    circles[i]!!.setTextColor(resources.getColor(R.color.white))
                    circles[i]!!.background = resources.getDrawable(R.drawable.four_test_success)
                    /*Glide.with(this)
                        .load(R.drawable.empty_circle)
                        .into(circles[i]!!)*/
                    circles[i]!!.typeface = face
                    circles[i]!!.layoutParams = params
                    view.ll_test_status.addView(circles!![i])
                }else{
                    val params = LinearLayout.LayoutParams(
                        getResources().getDimension(R.dimen._20sdp).toInt(),
                        getResources().getDimension(R.dimen._20sdp).toInt()
                    )
                    if (i != 0) {
                        params.leftMargin = getResources().getDimension(R.dimen._17sdp).toInt()
                    }

                    circles[i] = TextView(activity)
                    circles[i]!!.text = ""
                    circles[i]!!.gravity = Gravity.CENTER
                    circles[i]!!.setTextColor(resources.getColor(R.color.white))
                    circles[i]!!.background = resources.getDrawable(R.drawable.four_test_failure)
                    /*Glide.with(this)
                        .load(R.drawable.empty_circle)
                        .into(circles[i]!!)*/
                    circles[i]!!.typeface = face
                    circles[i]!!.layoutParams = params
                    view.ll_test_status.addView(circles!![i])
                }

            }

        }



    }

    private fun updateTodayChallengeDone(dayofweek:Int){
        when(dayofweek) {
            1 -> {
                //println("Invalid number")
                view!!.image7.background = resources.getDrawable(R.drawable.day_of_week_success)
                view!!.text7.setTextColor(resources.getColor(R.color.white))
            }

            2 -> {
                //println("Number too low")
                view!!.image1.background = resources.getDrawable(R.drawable.day_of_week_success)
                view!!.text1.setTextColor(resources.getColor(R.color.white))
            }
            3 -> {
               // println("Number correct")
                view!!.image2.background = resources.getDrawable(R.drawable.day_of_week_success)
                view!!.text2.setTextColor(resources.getColor(R.color.white))

            }
            4 -> {
                //println("Number too high, but acceptable")
                view!!.image3.background = resources.getDrawable(R.drawable.day_of_week_success)
                view!!.text3.setTextColor(resources.getColor(R.color.white))
            }
            5 -> {
                view!!.image4.background = resources.getDrawable(R.drawable.day_of_week_success)
                view!!.text4.setTextColor(resources.getColor(R.color.white))
            }
            6 -> {
                view!!.image5.background = resources.getDrawable(R.drawable.day_of_week_success)
                view!!.text5.setTextColor(resources.getColor(R.color.white))
            }
            7 -> {
                view!!.image6.background = resources.getDrawable(R.drawable.day_of_week_success)
                view!!.text6.setTextColor(resources.getColor(R.color.white))
            }

        }
    }


    private fun showCalendarForProcessingDate() {
        // Get Current Date
        val c = Calendar.getInstance()
        mYear = c.get(Calendar.YEAR)
        mMonth = c.get(Calendar.MONTH)
        mDay = c.get(Calendar.DAY_OF_MONTH)
        //processingdateETID.setText(mDay+"/"+(mMonth+1)+"/"+mYear);
        val datePickerDialog = DatePickerDialog(activity!!,DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                //cardViewCVID.setVisibility(View.GONE)
                //processingdateETID.setText(dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                try {
                    val birthDate =
                        sdf.parse(year.toString() + "-" + (monthOfYear + 1) + "-" +dayOfMonth)
                    view!!.tv_date.setText(sdf.format(birthDate))
                    Log.e("home fragment...","....dayOfMonth...."+dayOfMonth)
                    Log.e("home fragment...","....(monthOfYear + 1)...."+(monthOfYear + 1))
                    Log.e("home fragment...","....year...."+year)
                    Log.e("home fragment...","....birthDate...."+birthDate)
                    val sdf1 = SimpleDateFormat("yyyy-MM-dd")
                    Utils.date = birthDate

                    val i = Intent(activity!!, DashBoardActivity::class.java)
                    //i.putExtra("fragment", "pdf")
                    startActivity(i)
                    //calculateAge(birthDate,"member2",member2age);
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                //getAge(year,monthOfYear+1,dayOfMonth,"member2",member2age);
            }, mYear, mMonth, mDay
        )
        c.add(Calendar.YEAR, -10)
        datePickerDialog.datePicker.minDate = c.timeInMillis
        //datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()

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

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (v!!.id){

            R.id.quizRL -> {
                sound = sharedPrefs?.getBooleanPrefVal(activity!!, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    //  mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                }
                quizbuttonAction()
            }
            R.id.testRL -> {
                sound = sharedPrefs?.getBooleanPrefVal(activity!!, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    //  mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                }
                testbuttonAction()
            }
            R.id.dateRL -> {
                showCalendarForProcessingDate()
            }
            R.id.multi_quiz_go_btn -> {
                sound = sharedPrefs?.getBooleanPrefVal(activity!!, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    //  mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                }
                view!!.multi_quiz_go_btn.isEnabled = false
                quizbuttonAction()
            }
            R.id.go_btn_multi_test -> {
                sound = sharedPrefs?.getBooleanPrefVal(activity!!, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    //  mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                }
                view!!.go_btn_multi_test.isEnabled = false
                testbuttonAction()
            }
            R.id.settings -> {
                sound = sharedPrefs?.getBooleanPrefVal(activity!!, ConstantPath.SOUNDS) ?: true
                if(!sound){
                    // mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
                    //  mediaPlayer.start()
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..."+ Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                }
                val intent = Intent(context!!, SettingsActivity::class.java)
                startActivity(intent)
            }





        }

    }

    fun quizbuttonAction(){
        Log.e("home fragment","quiz button action.....")
        gotoStartScreen1()


    }

    fun testbuttonAction(){
        Log.e("home fragment","test button action.....")

        if(Utils.isOnline(activity)){
            var downloadstatus = databaseHandler!!.gettesttopicdownloadstatus()
            if(downloadstatus == 1){
                // test_btn.isEnabled = false
                if(isdownload){
                    downloaddialog("We’re downloading the latest Tests. Please try again in a few minutes.")
                }else {
                    view!!.progress_bar.visibility = View.VISIBLE
                    view!!.txt_next.visibility = View.GONE
                    view!!.right_arrow.visibility = View.GONE
                    val docRef =
                        db.collection("testcontentdownload").document("gVBcBjqHQBLjvrUGwkos")
                    docRef.get().addOnSuccessListener { document ->
                        if (document != null) {
                            Log.e(
                                "grade activity",
                                "DocumentSnapshot data: ${document.data}"
                            )
                            version = document.data!!.get("TestContentVersion").toString()
                            url = document.data!!.get("TestContentUrl").toString()

                            var dbversion = databaseHandler!!.gettesttopicversion()
                            if (dbversion != version) {
                                view!!.progress_bar.visibility = View.GONE
                                view!!.txt_next.visibility = View.VISIBLE
                                view!!.right_arrow.visibility = View.VISIBLE
                                downloaddialog("We’re downloading the latest Tests. Please try again in a few minutes.")
                                //showDataFromBackground(activity!!,url,version, mServiceResultReceiver!!)
                                downdata(url)
                            } else {
                                //   test_btn.isEnabled = true
                                try{
                                    view!!.progress_bar.visibility = View.GONE
                                    view!!.txt_next.visibility = View.VISIBLE
                                    view!!.right_arrow.visibility = View.VISIBLE
                                }catch (e:Exception){

                                }

                                gotoStartScreen()
                            }

                        } else {
                            Log.e("grade activity", "No such document")
                            //navigateToDashboard("GRADE 6")

                        }
                    }
                        .addOnFailureListener { exception ->
                            Log.e("grade activity", "get failed with ", exception)
                            //navigateToDashboard("GRADE 6")

                        }

                }

            }else{

                var dburl = databaseHandler!!.gettesttopicurl()
                if(dburl == null){
                    //  test_btn.isEnabled = false
                    if(isdownload){
                        downloaddialog("We’re downloading the latest Tests. Please try again in a few minutes.")
                    }else {


                        view!!.progress_bar.visibility = View.VISIBLE
                        view!!.txt_next.visibility = View.GONE
                        view!!.right_arrow.visibility = View.GONE
                        val docRef = db.collection("testcontentdownload").document("gVBcBjqHQBLjvrUGwkos")
                        docRef.get().addOnSuccessListener { document ->
                            if (document != null) {
                                Log.e("grade activity", "DocumentSnapshot data: ${document.data}")
                                version = document.data!!.get("TestContentVersion").toString()
                                url = document.data!!.get("TestContentUrl").toString()

                                view!!.progress_bar.visibility = View.GONE
                                view!!.txt_next.visibility = View.VISIBLE
                                view!!.right_arrow.visibility = View.VISIBLE
                                databaseHandler!!.insertTESTCONTENTDOWNLOAD(version,url,0)
                                //sharedPrefs.setBooleanPrefVal(this@GradeActivity, ConstantPath.IS_FIRST_TIME, true)
                                //if(hasPermissions(this@GradeActivity, *PERMISSIONS)){
                                // var url = databaseHandler!!.gettesttopicurl()
                                downloaddialog("We’re downloading the latest Tests. Please try again in a few minutes.")
                                downdata(url)
                                //showDataFromBackground(activity!!,url,version, mServiceResultReceiver!!)
                                //navigateToDashboard("GRADE 6")
                                /*if(Utils.isOnline(this@GradeActivity)){
                                    val task = MyAsyncTask(this@GradeActivity)
                                    task.execute(url)
                                }else{
                                    Toast.makeText(this@GradeActivity,"Internet is required!",Toast.LENGTH_LONG).show();
                                }*/




                            } else {
                                Log.e("grade activity", "No such document")
                                //navigateToDashboard("GRADE 6")

                            }
                        }
                            .addOnFailureListener { exception ->
                                Log.e("grade activity", "get failed with ", exception)
                                //navigateToDashboard("GRADE 6")

                            }
                    }

                }else{
                    view!!.progress_bar.visibility = View.GONE
                    view!!.txt_next.visibility = View.VISIBLE
                    view!!.right_arrow.visibility = View.VISIBLE
                    var url = databaseHandler!!.gettesttopicurl()
                    var version = databaseHandler!!.gettesttopicversion()
                    downdata(url)
                    downloaddialog("We’re downloading the latest Tests. Please try again in a few minutes.")
                    //showDataFromBackground(activity!!,url,version, mServiceResultReceiver!!)
                }


            }
        }else{
            var downloadstatus = databaseHandler!!.gettesttopicdownloadstatus()
            if(downloadstatus == 1){
                gotoStartScreen()
            }else{
                downloaddialog("Please connect to the internet to download the latest tests.")
            }

        }
    }

    private fun downdata(url:String){
        val dirpath = File((activity!!.getExternalFilesDir(null))!!.absolutePath)

        val downloadId = PRDownloader.download(url, dirpath.absolutePath, "/testcontent.rar")
            .build()
            .setOnStartOrResumeListener {
                Log.e("downdata", "onStartOrResume.....")
                isdownload = true

            }
            .setOnPauseListener { Log.e("downdata", "onPause.....") }
            .setOnCancelListener { Log.e("downdata", "onCancel.....") }
            .setOnProgressListener { progress ->
                Log.e(
                    "downdata",
                    "onProgress.....$progress")
                isdownload = true
            }
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    Log.e("downdata", "onDownloadComplete.....")
                    try {
                        val dirFile = File(activity!!.getExternalFilesDir(null), "test")
                        FileUtils.deleteDirectory(dirFile)
                    } catch (e: Exception) {

                    }

                    val iszip = Utils.unpackZip(dirpath.absolutePath, "/testcontent.rar")
                    if (iszip) {
                        try{
                            val dirFile = File(activity!!.getExternalFilesDir(null), "testcontent.rar")
                            dirFile.delete()
                        }catch (e: Exception) {

                        }

                        databaseHandler!!.updatetestcontentversion(version)
                        databaseHandler!!.updatetestcontentdownloadstatus(1)

                        if(alertDialog != null){
                            alertDialog!!.dismiss()
                        }
                        //  test_btn.isEnabled = true
                        isdownload = false
                        //readFileLocally()
                        gotoStartScreen()
                    }

                }

                override fun onError(error: Error) {
                    Log.e("downdata", "onerror.....$error")
                    // JobService.enqueueWork(context1,url,version);
                    //  test_btn.isEnabled = true
                    if(alertDialog != null){
                        alertDialog!!.dismiss()
                    }
                    isdownload = false
                    Toast.makeText(activity!!,"Please check your network connection.", Toast.LENGTH_LONG).show()
                }




            })
    }

    fun downloaddialog(msg:String){
        val dialogBuilder = AlertDialog.Builder(activity!!, R.style.mytheme)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.app_download_test_update, null)
        dialogBuilder.setView(dialogView)


        val btn_upgrade = dialogView.findViewById(R.id.btn_upgrade) as Button
        val tv_message1 = dialogView.findViewById(R.id.tv_message1) as TextView
        tv_message1.text = msg
        alertDialog = dialogBuilder.create()
        alertDialog!!.setCancelable(false)
        btn_upgrade.setOnClickListener {
            alertDialog!!.dismiss()




            //navigateToSummaryScreenNew()
            // var status:Int = databaseHandler!!.updatequizplayFinalstatus(testQuiz.title,"1",currentDate,testQuiz.lastplayed);
            // var answers:Int = databaseHandler!!.updatequizplayFinalTimeTaken(testQuiz.title,timetaken.toString(),currentDate,testQuiz.lastplayed);
            // navigateToSummaryScreenNew()
        }

        //alertDialog.getWindow().setBackgroundDrawable(draw);
        alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog!!.show()
    }

    private fun readFileLocally() {
        val dirFile = File(activity!!.getExternalFilesDir(null),"test/")
        Log.e("test fragment","dir file....."+dirFile.absolutePath)
        val courseJsonString = Utils.readFromFile( dirFile.absolutePath + "/Courses.json")
        //val courseJsonString = readFromFile("$localBlobcityPath/Courses.json")
        Log.d("courseJsonString....1",courseJsonString+"!");
        //val courseJsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( ConstantPath.localBlobcityPath1 + "Courses.json")
        //val courseJsonString = readFromFile("$localBlobcityPath/Courses.json")
        //Log.d("courseJsonString",courseJsonString+"!");
        /*val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( assetTestCoursePath + "topic.json")*/
        val gsonFile = Gson()
        val courseType = object : TypeToken<List<CoursesResponseModel>>() {}.type
        val courseResponseModel: ArrayList<CoursesResponseModel> = gsonFile.fromJson(courseJsonString, courseType)
        courseId = courseResponseModel[0].id
        courseName = courseResponseModel[0].syllabus.title
        // tv_class.text = courseName
        // tv_class_board.text = courseResponseModel[0].syllabus.displayTitle
        localPath = "${dirFile.absolutePath}/$courseName/"
        // val jsonString = readFromFile(localPath +"topic.json")
        val jsonString = Utils.readFromFile( localPath + "topic.json")
        Log.d("jsonString",jsonString);
        val topicType = object : TypeToken<TopicResponseModel>() {}.type
        val topicResponseModel: TopicResponseModel = gsonFile.fromJson(jsonString, topicType )

        branchesItemList = topicResponseModel.branches
        sharedPrefs?.setIntPrefVal(context!!, ConstantPath.TOPIC_SIZE, branchesItemList!!.size)
        /*val branchesItemList2 = ArrayList<BranchesItem>()
        val index1 = branchesItemList!![0].topic.index.toString()
        tv_topic_number1.text = index1
        tv_topic_name1.text = branchesItemList!![0].topic.title

        val index2 = branchesItemList!![1].topic.index.toString()
        tv_topic_number2.text = index2
        tv_topic_name2.text = branchesItemList!![1].topic.title
        branchesItemList!!.forEachIndexed { index, branchesItem ->
            if (index>1){
                branchesItemList2.add(branchesItem)
            }
        }*/


        /*rl_chapter_one.setOnClickListener(this)
        rl_chapter_two.setOnClickListener(this)*/
    }

    private fun readFileLocally1() {
        val courseJsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( ConstantPath.localBlobcityPath + "Courses.json")
        //val courseJsonString = readFromFile("$localBlobcityPath/Courses.json")
        Log.d("courseJsonString",courseJsonString+"!");
        /*val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( assetTestCoursePath + "topic.json")*/
        val gsonFile = Gson()
        val courseType = object : TypeToken<List<CoursesResponseModel>>() {}.type
        val courseResponseModel: ArrayList<CoursesResponseModel> = gsonFile
            .fromJson(courseJsonString, courseType)
        courseId = courseResponseModel[0].id
        courseName = courseResponseModel[0].syllabus.title
        // tv_class.text = courseName
        // tv_class_board.text = courseResponseModel[0].syllabus.displayTitle
        localPath = "${ConstantPath.localBlobcityPath}$courseName/"
        // val jsonString = readFromFile(localPath +"topic.json")
        val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( localPath + "topic.json")
        Log.d("jsonString",jsonString);
        val topicType = object : TypeToken<TopicResponseModel>() {}.type
        val topicResponseModel: TopicResponseModel= gsonFile.fromJson(jsonString, topicType )

        branchesItemList = topicResponseModel.branches
        sharedPrefs?.setIntPrefVal(context!!, ConstantPath.TOPIC_SIZE, branchesItemList!!.size)
        /*val branchesItemList2 = ArrayList<BranchesItem>()
        val index1 = branchesItemList!![0].topic.index.toString()
        tv_topic_number1.text = index1
        tv_topic_name1.text = branchesItemList!![0].topic.title

        val index2 = branchesItemList!![1].topic.index.toString()
        tv_topic_number2.text = index2
        tv_topic_name2.text = branchesItemList!![1].topic.title
        branchesItemList!!.forEachIndexed { index, branchesItem ->
            if (index>1){
                branchesItemList2.add(branchesItem)
            }
        }*/


        /*rl_chapter_one.setOnClickListener(this)
        rl_chapter_two.setOnClickListener(this)*/
    }
    fun loadJSONFromAsset(path: String): String? {
        val json: String?
        try {
            val `is` = context!!.assets.open(path)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }

    fun gotoStartScreen1(){
        readFileLocally1()

        var lastplayed:String =""
        var topic: Topic
        var folderPath:String = ""

        Log.e("test fragment","branchesItemList!!.size......"+branchesItemList!!.size)
        randomPosition = Random.nextInt(branchesItemList!!.size)
        Log.e("test fragment","randomPosition......"+randomPosition)
        topic = branchesItemList!![randomPosition].topic
        folderPath = localPath+topic.folderName
        Log.e("test fragment","testQuiz.folderPath......"+folderPath)

        randomPos = Random.nextInt(positionList!!.size)
        Log.e("test fragment","randomPos......"+randomPos)
        jsonStringBasic =  loadJSONFromAsset("$folderPath/"+positionList!![randomPos])


       /* var testQuiz: TestQuiz
        testQuiz = databaseHandler!!.getQuizTopicsForTimerLastPlayed()
        Log.e("test fragment","testQuiz.lastplayed......"+testQuiz.lastplayed)
        if(testQuiz.lastplayed == null){
            topic = branchesItemList!![0].topic
            folderPath = localPath+topic.folderName
            Log.e("test fragment","testQuiz.folderPath......"+folderPath)
            jsonStringBasic =  loadJSONFromAsset("$folderPath/basic.json")
            lastplayed = "basic"

            databaseHandler!!.deleteAllQuizTopicsLatPlayed()

            databaseHandler!!.insertquiztopiclastplayed(topic.title,topic.displayNo,lastplayed);
        }else{

            if(branchesItemList!!.size == (testQuiz.serialNo).toInt()){
                topic = branchesItemList!![0].topic
                folderPath = localPath+topic.folderName
                Log.e("test fragment","testQuiz.folderPath......"+folderPath)
                jsonStringBasic =  loadJSONFromAsset("$folderPath/basic.json")
                lastplayed = "basic"
                databaseHandler!!.deleteAllQuizTopicsLatPlayed()

                databaseHandler!!.insertquiztopiclastplayed(topic.title,topic.displayNo,lastplayed);
            }else{
                topic = branchesItemList!![((testQuiz.serialNo).toInt())-1].topic
                folderPath = localPath+topic.folderName
                Log.e("test fragment","testQuiz.folderPath......"+folderPath)
                if(testQuiz.lastplayed.equals("basic")){
                    jsonStringBasic =  loadJSONFromAsset("$folderPath/intermediate.json")
                    lastplayed = "intermediate"
                    databaseHandler!!.deleteAllQuizTopicsLatPlayed()

                    databaseHandler!!.insertquiztopiclastplayed(topic.title,topic.displayNo,lastplayed);
                }else{
                    topic = branchesItemList!![((testQuiz.serialNo).toInt())].topic
                    folderPath = localPath+topic.folderName
                    Log.e("test fragment","testQuiz.folderPath......"+folderPath)
                    jsonStringBasic =  loadJSONFromAsset("$folderPath/basic.json")
                    lastplayed = "basic"
                    databaseHandler!!.deleteAllQuizTopicsLatPlayed()

                    databaseHandler!!.insertquiztopiclastplayed(topic.title,topic.displayNo,lastplayed);

                }
            }



        }*/
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "QuizGo")
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Quiz")
        //bundle.putString("Label", "QuizGo")
        firebaseAnalytics?.logEvent("PlayQuiz", bundle)



        Log.e("chapter fragment.....","jsonStringBasic......."+jsonStringBasic);
        view!!.multi_quiz_go_btn.isEnabled = true

        val intent = Intent(context!!, StartQuizActivityNew::class.java)
        intent.putExtra(ConstantPath.TOPIC, topic)
        intent.putExtra(ConstantPath.TOPIC_NAME, topic.title)
        intent.putExtra(ConstantPath.FOLDER_NAME, topic.folderName)
        intent.putExtra(ConstantPath.DYNAMIC_PATH, jsonStringBasic)
        intent.putExtra(ConstantPath.COURSE_ID, courseId)
        intent.putExtra(ConstantPath.COURSE_NAME, courseName)
        intent.putExtra(ConstantPath.TOPIC_ID, topic.id)
        intent.putExtra(ConstantPath.TOPIC_POSITION, topic.index)
        intent.putExtra(ConstantPath.FOLDER_PATH, localPath)
        intent.putExtra(ConstantPath.TITLE_TOPIC, gradeTitle!!)
        intent.putExtra("LAST_PLAYED", lastplayed)
        intent.putExtra("comingfrom", "Home")
        intent.putExtra(ConstantPath.TOPIC_LEVEL, "")
        intent.putExtra(ConstantPath.LEVEL_COMPLETED, "")
        intent.putExtra(ConstantPath.CARD_NO, "")
        startActivity(intent)
    }



    fun gotoStartScreen(){
        readFileLocally()
        //databaseHandler!!.deleteQuizPlayRecord(topic.title)
        var lastplayed:String =""
        var topic: Topic
        var folderPath:String = ""
        var testQuiz: TestQuiz
        testQuiz = databaseHandler!!.getQuizTopicsForTimerLastPlayed()
        Log.e("test fragment","testQuiz.lastplayed......"+testQuiz.lastplayed)
        if(testQuiz.lastplayed == null){
            topic = branchesItemList!![0].topic
            folderPath = localPath+topic.folderName
            Log.e("test fragment","testQuiz.folderPath......"+folderPath)
            jsonStringBasic =  Utils.readFromFile("$folderPath/basic.json")
            lastplayed = "basic"

            databaseHandler!!.deleteAllQuizTopicsLatPlayed()

            databaseHandler!!.insertquiztopiclastplayed(topic.title,topic.displayNo,lastplayed);
        }else{

            if(branchesItemList!!.size == (testQuiz.serialNo).toInt()){
                topic = branchesItemList!![0].topic
                folderPath = localPath+topic.folderName
                Log.e("test fragment","testQuiz.folderPath......"+folderPath)
                jsonStringBasic =  Utils.readFromFile("$folderPath/basic.json")
                lastplayed = "basic"
                databaseHandler!!.deleteAllQuizTopicsLatPlayed()

                databaseHandler!!.insertquiztopiclastplayed(topic.title,topic.displayNo,lastplayed);
            }else{
                topic = branchesItemList!![((testQuiz.serialNo).toInt())-1].topic
                folderPath = localPath+topic.folderName
                Log.e("test fragment","testQuiz.folderPath......"+folderPath)
                if(testQuiz.lastplayed.equals("basic")){
                    jsonStringBasic =  Utils.readFromFile("$folderPath/intermediate.json")
                    lastplayed = "intermediate"
                    databaseHandler!!.deleteAllQuizTopicsLatPlayed()

                    databaseHandler!!.insertquiztopiclastplayed(topic.title,topic.displayNo,lastplayed);
                }else{
                    topic = branchesItemList!![((testQuiz.serialNo).toInt())].topic
                    folderPath = localPath+topic.folderName
                    Log.e("test fragment","testQuiz.folderPath......"+folderPath)
                    jsonStringBasic =  Utils.readFromFile("$folderPath/basic.json")
                    lastplayed = "basic"
                    databaseHandler!!.deleteAllQuizTopicsLatPlayed()

                    databaseHandler!!.insertquiztopiclastplayed(topic.title,topic.displayNo,lastplayed);

                }
            }



        }


        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "TestGo")
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Test")
       // bundle.putString("Label", "TestGo")
        firebaseAnalytics?.logEvent("PlayTest", bundle)




        Log.e("chapter fragment.....","jsonStringBasic......."+jsonStringBasic);
        view!!.go_btn_multi_test.isEnabled = true
        val intent = Intent(context!!, StartQuizTimerActivity::class.java)
        intent.putExtra(ConstantPath.TOPIC, topic)
        intent.putExtra(ConstantPath.TOPIC_NAME, topic.title)
        intent.putExtra(ConstantPath.FOLDER_NAME, topic.folderName)
        intent.putExtra(ConstantPath.DYNAMIC_PATH, jsonStringBasic)
        intent.putExtra(ConstantPath.COURSE_ID, courseId)
        intent.putExtra(ConstantPath.COURSE_NAME, courseName)
        intent.putExtra(ConstantPath.TOPIC_ID, "")
        intent.putExtra(ConstantPath.TOPIC_POSITION, topic.displayNo)
        intent.putExtra(ConstantPath.FOLDER_PATH, localPath)
        intent.putExtra(ConstantPath.TITLE_TOPIC, gradeTitle!!)
        intent.putExtra("LAST_PLAYED", lastplayed)
        intent.putExtra("comingfrom", "Home")
        intent.putExtra(ConstantPath.TOPIC_LEVEL, "")
        intent.putExtra(ConstantPath.LEVEL_COMPLETED, "")
        intent.putExtra(ConstantPath.CARD_NO, "")
        startActivity(intent)
    }
}
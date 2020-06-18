package com.blobcity.activity

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.ImageViewCompat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import com.blobcity.R
import com.blobcity.database.QuizGameDataBase
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.model.*
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.SharedPrefs
import com.blobcity.utils.Utils
import com.blobcity.viewmodel.TopicStatusVM
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_quiz_time_summary.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class QuizTimeSummaryActivity : BaseActivity(), View.OnClickListener {


    var reviewModelList: ArrayList<ReviewModel>? = null
    var topicLevel: String? = ""
    var topicName: String? = ""
    var topicId: String? = ""
    private var totalQuestion: Int? = null
    var topicStatusVM: TopicStatusVM? = null
    var complete: String? = null
    var position: Int? = null
    var dPath: String? = null
    var paths: String? = null
    var courseId: String? = null
    var courseName: String? = null
    var folderName: String? = null
    var level_status: Boolean? = null
    var readyCardNumber = 0
    var gradeTitle: String? = null
    var topicStatusModelList: ArrayList<TopicStatusEntity>? = null
    var topicStatusModelList2: ArrayList<TopicStatusEntity>? = null

    var databaseHandler: QuizGameDataBase?= null
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    lateinit var circles: Array<ImageView?>
    var isallcorrect:Boolean = false
    var displayno:Int = 0
    var lastplayed:String = ""
    var count:Int = 0
    var localPath: String?= null
    private var branchesItemList:List<BranchesItem>?=null
    var jsonStringBasic: String? =""
    lateinit var testQuiz: TestQuiz
    override var layoutID: Int = R.layout.activity_quiz_time_summary

    override fun initView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorbottomnav));
        }
        sharedPrefs = SharedPrefs()
        databaseHandler = QuizGameDataBase(this);
        reviewModelList = intent.getSerializableExtra(ConstantPath.REVIEW_MODEL) as ArrayList<ReviewModel>?
        topicLevel = intent.getStringExtra(ConstantPath.TOPIC_LEVEL)
        topicName = intent.getStringExtra(ConstantPath.TOPIC_NAME)
        topicId = intent.getStringExtra(ConstantPath.TOPIC_ID)
        totalQuestion = intent.getIntExtra(ConstantPath.QUIZ_COUNT, 0)
        complete = intent.getStringExtra(ConstantPath.LEVEL_COMPLETED)
        position = intent.getIntExtra(ConstantPath.TOPIC_POSITION, -1)
        dPath = intent.getStringExtra(ConstantPath.DYNAMIC_PATH)
        paths = intent.getStringExtra(ConstantPath.FOLDER_PATH)
        courseId = intent.getStringExtra(ConstantPath.COURSE_ID)
        courseName = intent.getStringExtra(ConstantPath.COURSE_NAME)
        folderName = intent.getStringExtra(ConstantPath.FOLDER_NAME)
        gradeTitle = intent.getStringExtra(ConstantPath.TITLE_TOPIC)
        level_status = intent.getBooleanExtra(ConstantPath.IS_LEVEL_COMPLETE, false)
        readyCardNumber = intent.getIntExtra(ConstantPath.CARD_NO, -1)
        displayno = intent.getIntExtra("DISPLAY_NO", -1)
        lastplayed = intent.getStringExtra("LAST_PLAYED")

        testQuiz = databaseHandler!!.getQuizTopicsForTimerLastPlayed()


        val sdf = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = sdf.format(Date())



        //tv_quiz_title.text = topicName

        var questionanswers:String = databaseHandler!!.getQuizQuestionAnswersFinal(testQuiz.title,currentDate,testQuiz.lastplayed);
        var queans:List<String> = questionanswers.split(",")


        //btn_start.text = "PLAY AGAIN"
        btn_review.visibility = View.VISIBLE
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
                count++
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
        tv_correct.text = ""+count
        tv_total.text = ""+totalQuestion
        if(count == totalQuestion){

            successRL.background = resources.getDrawable(R.drawable.quiz_time_success)
            tv_correct.setTextColor(resources.getColor(R.color.button_close_text))
        }else{
            successRL.background = resources.getDrawable(R.drawable.quiz_time_failure)
            tv_correct.setTextColor(resources.getColor(R.color.test_fail))

        }
        var time:String = databaseHandler!!.getQuiztimetakens(testQuiz.title,currentDate,testQuiz.lastplayed);
        if(time.equals("0")){
            tv_time.text = "1 min"
        }else{
            tv_time.text = time+" mins"
        }

        btn_topics!!.setOnClickListener(this)
        btn_new_test!!.setOnClickListener(this)
        btn_play_next!!.setOnClickListener(this)
        btn_review!!.setOnClickListener(this)



    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.btn_topics -> {
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
                val intent = Intent(this, DashBoardActivity::class.java)
                intent.putExtra("fragment","tests")
                startActivity(intent)
                finish()
            }
            R.id.btn_new_test -> {
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
                readFileLocally()
                //playNextBtnAction(position!!)

            }

            R.id.btn_review -> {
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
                val sdf = SimpleDateFormat("dd-MM-yyyy")
                val currentDate = sdf.format(Date())

                val intent = Intent(this, QuizTimeReviewActivity::class.java)
                intent.putExtra(ConstantPath.DYNAMIC_PATH, dPath)
                intent.putExtra(ConstantPath.COURSE_ID, courseId)
                intent.putExtra(ConstantPath.TOPIC_ID, topicId)
                intent.putExtra(ConstantPath.COURSE_NAME, courseName)
                intent.putExtra(ConstantPath.TOPIC_LEVEL, topicLevel)
                intent.putExtra(ConstantPath.TOPIC_NAME, topicName)
                intent.putExtra(ConstantPath.LEVEL_COMPLETED, complete)
                intent.putExtra(ConstantPath.TOPIC_POSITION, position!!)
                intent.putExtra(ConstantPath.FOLDER_PATH, paths)
                intent.putExtra(ConstantPath.FOLDER_NAME, folderName)
                intent.putExtra(ConstantPath.TITLE_TOPIC, gradeTitle!!)
                intent.putExtra(ConstantPath.CARD_NO, readyCardNumber)
                intent.putExtra(ConstantPath.QUIZ_COUNT, totalQuestion)

                intent.putExtra("title", testQuiz.title)
                intent.putExtra("playeddate", currentDate)
                intent.putExtra("lastplayed", testQuiz.lastplayed)


                startActivity(intent)
                //finish()
            }

        }
    }

    private fun readFileLocally() {
        val dirFile = File(getExternalFilesDir(null),"test/")
        val courseJsonString = Utils.readFromFile( dirFile.absolutePath + "/Courses.json")
        //val courseJsonString = loadJSONFromAsset( ConstantPath.localBlobcityPath1 + "Courses.json")
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
        localPath = "${dirFile.absolutePath}/$courseName/"
        // val jsonString = readFromFile(localPath +"topic.json")
        val jsonString = Utils.readFromFile( localPath + "topic.json")
        Log.d("jsonString",jsonString);
        val topicType = object : TypeToken<TopicResponseModel>() {}.type
        val topicResponseModel: TopicResponseModel = gsonFile.fromJson(jsonString, topicType )

        branchesItemList = topicResponseModel.branches
        sharedPrefs?.setIntPrefVal(this!!, ConstantPath.TOPIC_SIZE, branchesItemList!!.size)

        gotoStartScreen()
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

    private fun playNextBtnAction(position:Int){
        val dirFile = File(getExternalFilesDir(null),"test/")
        val courseJsonString = Utils.readFromFile( dirFile.absolutePath + "/Courses.json")
        //val courseJsonString = loadJSONFromAsset( ConstantPath.localBlobcityPath1 + "Courses.json")
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
        localPath = "${dirFile.absolutePath}/$courseName/"
        // val jsonString = readFromFile(localPath +"topic.json")
        val jsonString = Utils.readFromFile( localPath + "topic.json")
        Log.d("jsonString",jsonString);
        val topicType = object : TypeToken<TopicResponseModel>() {}.type
        val topicResponseModel: TopicResponseModel = gsonFile.fromJson(jsonString, topicType )

        branchesItemList = topicResponseModel.branches


        if((position+1) <= branchesItemList!!.size){
            var topic = branchesItemList!![(position+1)].topic
            databaseHandler!!.deleteQuizPlayRecord(topic.title)

            var lastplayed:String =""
            var lastdisplayed = databaseHandler!!.getQuizTopicsLastPlayed(topic.title)
            val folderPath = localPath+topic.folderName
            if(lastdisplayed.equals("NA") || lastdisplayed.equals("intermediate")){
                jsonStringBasic = loadJSONFromAsset("$folderPath/basic.json")
                lastplayed = "basic"
            }else{
                jsonStringBasic = loadJSONFromAsset("$folderPath/intermediate.json")
                lastplayed = "intermediate"
            }

            val intent = Intent(this!!, StartQuizActivityNew::class.java)
            intent.putExtra(ConstantPath.TOPIC, topic)
            intent.putExtra(ConstantPath.TOPIC_NAME, topic.title)
            intent.putExtra(ConstantPath.FOLDER_NAME, topic.folderName)
            intent.putExtra(ConstantPath.DYNAMIC_PATH, jsonStringBasic)
            intent.putExtra(ConstantPath.COURSE_ID, courseId)
            intent.putExtra(ConstantPath.COURSE_NAME, courseName)
            intent.putExtra(ConstantPath.TOPIC_ID, topicId)
            intent.putExtra(ConstantPath.TOPIC_POSITION, position)
            intent.putExtra(ConstantPath.FOLDER_PATH, localPath)
            intent.putExtra(ConstantPath.TITLE_TOPIC, gradeTitle!!)
            intent.putExtra("LAST_PLAYED", lastplayed)
            intent.putExtra(ConstantPath.TOPIC_LEVEL, "")
            intent.putExtra(ConstantPath.LEVEL_COMPLETED, "")
            intent.putExtra(ConstantPath.CARD_NO, "")
            startActivity(intent)
            finish()



        }else{

            var topic = branchesItemList!![0].topic
            databaseHandler!!.deleteQuizPlayRecord(topic.title)

            var lastplayed:String =""
            var lastdisplayed = databaseHandler!!.getQuizTopicsLastPlayed(topic.title)
            val folderPath = localPath+topic.folderName
            if(lastdisplayed.equals("NA") || lastdisplayed.equals("intermediate")){
                jsonStringBasic = loadJSONFromAsset("$folderPath/basic.json")
                lastplayed = "basic"
            }else{
                jsonStringBasic = loadJSONFromAsset("$folderPath/intermediate.json")
                lastplayed = "intermediate"
            }

            val intent = Intent(this!!, StartQuizActivityNew::class.java)
            intent.putExtra(ConstantPath.TOPIC, topic)
            intent.putExtra(ConstantPath.TOPIC_NAME, topic.title)
            intent.putExtra(ConstantPath.FOLDER_NAME, topic.folderName)
            intent.putExtra(ConstantPath.DYNAMIC_PATH, jsonStringBasic)
            intent.putExtra(ConstantPath.COURSE_ID, courseId)
            intent.putExtra(ConstantPath.COURSE_NAME, courseName)
            intent.putExtra(ConstantPath.TOPIC_ID, topicId)
            intent.putExtra(ConstantPath.TOPIC_POSITION, position)
            intent.putExtra(ConstantPath.FOLDER_PATH, localPath)
            intent.putExtra(ConstantPath.TITLE_TOPIC, gradeTitle!!)
            intent.putExtra("LAST_PLAYED", lastplayed)
            intent.putExtra(ConstantPath.TOPIC_LEVEL, "")
            intent.putExtra(ConstantPath.LEVEL_COMPLETED, "")
            intent.putExtra(ConstantPath.CARD_NO, "")
            startActivity(intent)
            finish()



        }




    }


    fun gotoStartScreen(){

        //databaseHandler!!.deleteQuizPlayRecord(topic.title)
        var lastplayed:String =""
        var topic:Topic
        var folderPath:String = ""
        var testQuiz:TestQuiz
        testQuiz = databaseHandler!!.getQuizTopicsForTimerLastPlayed()
        Log.e("test fragment","testQuiz.lastplayed......"+testQuiz.lastplayed)
        if(testQuiz.lastplayed == null){
            topic = branchesItemList!![0].topic
            folderPath = localPath+topic.folderName
            jsonStringBasic =  Utils.readFromFile("$folderPath/basic.json")
            lastplayed = "basic"

            databaseHandler!!.deleteAllQuizTopicsLatPlayed()

            databaseHandler!!.insertquiztopiclastplayed(topic.title,topic.displayNo,lastplayed);
        }else{

            if(branchesItemList!!.size == (testQuiz.serialNo).toInt()){
                topic = branchesItemList!![0].topic
                folderPath = localPath+topic.folderName
                jsonStringBasic =  Utils.readFromFile("$folderPath/basic.json")
                lastplayed = "basic"
                databaseHandler!!.deleteAllQuizTopicsLatPlayed()

                databaseHandler!!.insertquiztopiclastplayed(topic.title,topic.displayNo,lastplayed);
            }else{
                topic = branchesItemList!![((testQuiz.serialNo).toInt())-1].topic
                folderPath = localPath+topic.folderName
                if(testQuiz.lastplayed.equals("basic")){
                    jsonStringBasic =  Utils.readFromFile("$folderPath/intermediate.json")
                    lastplayed = "intermediate"
                    databaseHandler!!.deleteAllQuizTopicsLatPlayed()

                    databaseHandler!!.insertquiztopiclastplayed(topic.title,topic.displayNo,lastplayed);
                }else{
                    topic = branchesItemList!![((testQuiz.serialNo).toInt())].topic
                    folderPath = localPath+topic.folderName

                    jsonStringBasic =  Utils.readFromFile("$folderPath/basic.json")
                    lastplayed = "basic"
                    databaseHandler!!.deleteAllQuizTopicsLatPlayed()

                    databaseHandler!!.insertquiztopiclastplayed(topic.title,topic.displayNo,lastplayed);

                }
            }



        }




        Log.e("chapter fragment.....","jsonStringBasic......."+jsonStringBasic);

        val intent = Intent(this!!, StartQuizTimerActivity::class.java)
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
        intent.putExtra(ConstantPath.TOPIC_LEVEL, "")
        intent.putExtra(ConstantPath.LEVEL_COMPLETED, "")
        intent.putExtra(ConstantPath.CARD_NO, "")
        startActivity(intent)
    }
}
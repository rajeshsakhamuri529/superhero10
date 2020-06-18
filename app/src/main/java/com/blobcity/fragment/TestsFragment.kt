package com.blobcity.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.R
import com.blobcity.activity.DashBoardActivity
import com.blobcity.activity.QuizTimeReviewActivity
import com.blobcity.activity.StartQuizActivityNew
import com.blobcity.activity.StartQuizTimerActivity
import com.blobcity.adapter.ChaptersAdapter
import com.blobcity.adapter.TestsAdapter
import com.blobcity.database.QuizGameDataBase
import com.blobcity.interfaces.TestQuizReviewClickListener
import com.blobcity.model.*
import com.blobcity.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.tests_challenge.*
import java.io.File

class TestsFragment: Fragment(),View.OnClickListener, TestQuizReviewClickListener {



    private var testItemList:ArrayList<TestsModel>?=null
    lateinit var testmodel:TestsModel
    var adapter: TestsAdapter?= null
    var databaseHandler: QuizGameDataBase?= null
    lateinit var  mSoundManager: SoundManager;
    var jsonStringBasic: String? =""
    var courseId: String?=""
    var courseName: String?=""
    var localPath: String?= null
    private var branchesItemList:List<BranchesItem>?=null
    var testquizlist: List<TestQuizFinal>? = null
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    var gradeTitle: String?= null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tests_challenge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gradeTitle = arguments!!.getString(ConstantPath.TITLE_TOPIC)!!
        tests.elevation = 15F
        sharedPrefs = SharedPrefs()
        databaseHandler = QuizGameDataBase(context);


        test_btn.setOnClickListener(this)

        readFileLocally()


        testquizlist = databaseHandler!!.getTestQuizList()

        if(testquizlist!!.size == 0){
            tv_no_review.visibility = View.VISIBLE
            rcv_tests.visibility = View.GONE
        }else{
            rcv_tests.visibility = View.VISIBLE
            tv_no_review.visibility = View.GONE


            adapter = TestsAdapter(context!!, testquizlist!!,this)


            rcv_tests.addItemDecoration(VerticalSpaceItemDecoration(40));
            //rcv_chapter.addItemDecoration(itemDecorator)
            //rcv_chapter.addItemDecoration(DividerItemDecoration(context,))
            rcv_tests.adapter = adapter
        }

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

    override fun onClick(v: View?) {
        when (v!!.id){
           R.id.test_btn -> {
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
               gotoStartScreen()
           }
        }
    }

    override fun onClick(topic: TestQuizFinal) {
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
        gotoReviewScreen(topic)
    }

    fun gotoReviewScreen(topic: TestQuizFinal){
        val intent = Intent(activity, QuizTimeReviewActivity::class.java)
        intent.putExtra("title", topic.title)
        intent.putExtra("playeddate", topic.pdate)
        intent.putExtra("lastplayed", topic.typeofPlay)
        intent.putExtra(ConstantPath.QUIZ_COUNT, topic.totalQuestions)

        startActivity(intent)
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




        Log.e("chapter fragment.....","jsonStringBasic......."+jsonStringBasic);

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
        intent.putExtra(ConstantPath.TOPIC_LEVEL, "")
        intent.putExtra(ConstantPath.LEVEL_COMPLETED, "")
        intent.putExtra(ConstantPath.CARD_NO, "")
        startActivity(intent)
    }
}
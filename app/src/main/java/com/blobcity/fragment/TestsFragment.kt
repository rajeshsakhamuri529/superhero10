package com.blobcity.fragment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.blobcity.R
import com.blobcity.Service.JobService
import com.blobcity.Service.ProgressJobService
import com.blobcity.Service.ProgressJobService.SHOW_RESULT
import com.blobcity.Service.ServiceResultReceiver
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
import com.downloader.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.tests_challenge.*
import org.apache.commons.io.FileUtils
import java.io.File

class TestsFragment: Fragment(),View.OnClickListener, TestQuizReviewClickListener,
    ServiceResultReceiver.Receiver {



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
    var version : String = ""
    var url : String = ""
    var alertDialog:AlertDialog? = null
    private var mServiceResultReceiver: ServiceResultReceiver? = null
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var isdownload:Boolean = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tests_challenge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gradeTitle = arguments!!.getString(ConstantPath.TITLE_TOPIC)!!
        tests.elevation = 15F
        sharedPrefs = SharedPrefs()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        db.firestoreSettings = settings

        mServiceResultReceiver = ServiceResultReceiver(Handler())
        mServiceResultReceiver?.setReceiver(this)
        databaseHandler = QuizGameDataBase(context);


        test_btn.setOnClickListener(this)


        var downloadstatus = databaseHandler!!.gettesttopicdownloadstatus()
        if(downloadstatus == 1) {
            readFileLocally()
        }

        testquizlist = databaseHandler!!.getTestQuizList()

        if(testquizlist!!.size == 0){
            tv_no_review.visibility = View.VISIBLE
            rcv_tests.visibility = View.GONE
        }else{
            rcv_tests.visibility = View.VISIBLE
            tv_no_review.visibility = View.GONE


            adapter = TestsAdapter(context!!, testquizlist!!,this)


            rcv_tests.addItemDecoration(VerticalSpaceItemDecoration(30));
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

    override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
        when (resultCode) {
            SHOW_RESULT -> if (resultData != null) {
                //showData(resultData.getString("data"))
                Log.e("test fragment","onReceiveResult....."+resultData.getString("data"))
                if (resultData.getString("data") == "success") {
                    readFileLocally()
                    gotoStartScreen()
                }else if(resultData.getString("data") == "failure"){

                }
            }
        }
    }
    private fun showDataFromBackground(
        mainActivity: Activity,
        url:String,
        version:String,
        mResultReceiver: ServiceResultReceiver
    ) {
        ProgressJobService.enqueueWork(mainActivity, url,version, mResultReceiver)
    }

    private fun downdata(url:String){
        val dirpath = File((activity!!.getExternalFilesDir(null)).absolutePath)

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
                        val dirFile = File(activity!!.getExternalFilesDir(null), "testcontent.rar")
                        dirFile.delete()
                        databaseHandler!!.updatetestcontentversion(version)
                        databaseHandler!!.updatetestcontentdownloadstatus(1)

                        if(alertDialog != null){
                           alertDialog!!.dismiss()
                        }
                      //  test_btn.isEnabled = true
                        isdownload = false
                        readFileLocally()
                        gotoStartScreen()
                    }

                }

                override fun onError(error: Error) {
                    Log.e("downdata", "onerror.....$error")
                    // JobService.enqueueWork(context1,url,version);
                  //  test_btn.isEnabled = true
                    isdownload = false
                    Toast.makeText(activity,"Please check your network connection.",Toast.LENGTH_LONG).show()
                }




            })
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

               if(Utils.isOnline(activity)){
                   var downloadstatus = databaseHandler!!.gettesttopicdownloadstatus()
                   if(downloadstatus == 1){
                      // test_btn.isEnabled = false
                       if(isdownload){
                           downloaddialog("We’re downloading the latest Tests. Please try again in a few minutes.")
                       }else {
                           progress_bar.visibility = View.VISIBLE
                           txt_next.visibility = View.GONE
                           right_arrow.visibility = View.GONE
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
                                       progress_bar.visibility = View.GONE
                                       txt_next.visibility = View.VISIBLE
                                       right_arrow.visibility = View.VISIBLE
                                       downloaddialog("We’re downloading the latest Tests. Please try again in a few minutes.")
                                       //showDataFromBackground(activity!!,url,version, mServiceResultReceiver!!)
                                       downdata(url)
                                   } else {
                                       //   test_btn.isEnabled = true
                                       progress_bar.visibility = View.GONE
                                       txt_next.visibility = View.VISIBLE
                                       right_arrow.visibility = View.VISIBLE
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


                           progress_bar.visibility = View.VISIBLE
                           txt_next.visibility = View.GONE
                           right_arrow.visibility = View.GONE
                           val docRef = db.collection("testcontentdownload").document("gVBcBjqHQBLjvrUGwkos")
                           docRef.get().addOnSuccessListener { document ->
                               if (document != null) {
                                   Log.e("grade activity", "DocumentSnapshot data: ${document.data}")
                                   version = document.data!!.get("TestContentVersion").toString()
                                   url = document.data!!.get("TestContentUrl").toString()

                                   progress_bar.visibility = View.GONE
                                   txt_next.visibility = View.VISIBLE
                                   right_arrow.visibility = View.VISIBLE
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
                           progress_bar.visibility = View.GONE
                           txt_next.visibility = View.VISIBLE
                           right_arrow.visibility = View.VISIBLE
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

               /*var downloadstatus = databaseHandler!!.gettesttopicdownloadstatus()
               if(downloadstatus == 1){
                   readFileLocally()
                   gotoStartScreen()
               }else{

               }*/


           }
        }
    }
    private fun downloadDataFromBackground(
        mainActivity: Activity,
        url: String, version:String
    ) {
        JobService.enqueueWork(mainActivity, url,version)
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
        alertDialog!!.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog!!.show()
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
        intent.putExtra("comingfrom", "Test")
        intent.putExtra(ConstantPath.TOPIC_LEVEL, "")
        intent.putExtra(ConstantPath.LEVEL_COMPLETED, "")
        intent.putExtra(ConstantPath.CARD_NO, "")
        startActivity(intent)
    }
}
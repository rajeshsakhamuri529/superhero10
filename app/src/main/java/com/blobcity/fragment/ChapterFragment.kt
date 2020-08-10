package com.blobcity.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.R
import com.blobcity.adapter.ChaptersAdapter
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.interfaces.TopicClickListener
import com.blobcity.model.BranchesItem
import com.blobcity.model.CoursesResponseModel
import com.blobcity.model.Topic
import com.blobcity.model.TopicResponseModel
import com.blobcity.utils.ConstantPath.*
import com.blobcity.viewmodel.TopicStatusVM
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.chapter_layout.view.*
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.blobcity.activity.*
import com.blobcity.database.QuizGameDataBase
import com.blobcity.utils.*
import com.blobcity.utils.Utils.*
import com.google.firebase.analytics.FirebaseAnalytics
import java.io.IOException
import java.nio.charset.Charset


class ChapterFragment: Fragment(), TopicClickListener {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var branchesItemList:List<BranchesItem>?=null
    var courseId: String?=""
    var courseName: String?=""
    var topicStatusModelList: ArrayList<TopicStatusEntity>?=null
    var adapter: ChaptersAdapter?= null
    var topicStatusVM: TopicStatusVM?= null
    var localPath: String?= null
    var gradeTitle: String?= null

    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    var databaseHandler: QuizGameDataBase?= null
    lateinit var  mSoundManager: SoundManager;
    var jsonStringBasic: String? =""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chapter_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gradeTitle = arguments!!.getString(TITLE_TOPIC)!!
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        sharedPrefs = SharedPrefs()
        databaseHandler = QuizGameDataBase(context);
        readFileLocally()

        view.topics.elevation = 15F

        firebaseAnalytics = FirebaseAnalytics.getInstance(activity!!)

        firebaseAnalytics.setCurrentScreen(activity!!, "Quiz", null /* class override */)
        /*val itemDecorator = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.rv_divider)!!)*/

        //val itemDecorator = DividerItemDecorations(context!!.getDrawable(R.drawable.rv_divider), false,false)
        //itemDecorator.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.rv_divider)!!)

       val itemDecorator = DividerItem(ContextCompat.getDrawable(context!!, R.drawable.rv_divider))
        //itemDecorator.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.rv_divider)!!)

        topicStatusVM!!.getAllTopicStatus(gradeTitle!!).observe(this,
            object : Observer<List<TopicStatusEntity>> {
                override fun onChanged(t: List<TopicStatusEntity>?) {
                    topicStatusModelList = ArrayList()
                    topicStatusModelList!!.addAll(t!!)
                    for (item in topicStatusModelList!!){
                        if (topicStatusModelList != null){
                            if (topicStatusModelList!!.size > 0){
                                for (branchItem in branchesItemList!!) {
                                    val branchId = branchItem.id
                                    branchItem.basic = 0
                                    branchItem.intermediate = 0
                                    branchItem.advance = 0
                                    for (topicStatusModels in topicStatusModelList!!) {
                                        val id = topicStatusModels.topicId
                                        val level = topicStatusModels.topicLevel

                                        if (id!!.contains(branchId)) {
                                            if (level!!.contains("basic")) {
                                                branchItem.basic = 1
                                            }
                                            if (level.contains("intermediate")) {
                                                branchItem.intermediate = 1
                                            }
                                            if (level.contains("advance")) {
                                                branchItem.advance = 1
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                    adapter = ChaptersAdapter(context!!, branchesItemList!!, this@ChapterFragment)
                    view.rcv_chapter.addItemDecoration(VerticalSpaceItemDecoration(40));
                    //rcv_chapter.setNestedScrollingEnabled(false);
                    view.rcv_chapter.setHasFixedSize(true);
                    //rcv_chapter.addItemDecoration(itemDecorator)
                    //rcv_chapter.addItemDecoration(DividerItemDecoration(context,))
                    view.rcv_chapter.adapter = adapter
                    databaseHandler!!.deleteAllQuizTopics()
                    for(i in 0 until branchesItemList!!.size){
                        var no = branchesItemList!![i].topic.displayNo.toString()
                        var title = branchesItemList!![i].topic.title
                        var lastplayed = "NA"
                        databaseHandler!!.insertquiztopics(title,no,lastplayed)
                    }


                }

            })
        view.rcv_chapter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)
                val totalItemCount = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                Log.d("lastVisible",lastVisible.toString()+"!")
                adapter!!.lastItem(
                    (view.rcv_chapter
                        .findViewHolderForAdapterPosition(lastVisible)
                            as ChaptersAdapter.ChaptersViewHolder)
                )
            }
        })

    }

    private fun readFileLocally() {
        val courseJsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( localBlobcityPath + "Courses.json")
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
        localPath = "$localBlobcityPath$courseName/"
       // val jsonString = readFromFile(localPath +"topic.json")
        val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( localPath + "topic.json")
        Log.d("jsonString",jsonString);
        val topicType = object : TypeToken<TopicResponseModel>() {}.type
        val topicResponseModel: TopicResponseModel= gsonFile.fromJson(jsonString, topicType )

        branchesItemList = topicResponseModel.branches
        sharedPrefs?.setIntPrefVal(context!!,TOPIC_SIZE, branchesItemList!!.size)
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
    private fun playSomeSound() {
		if (mSoundManager != null) {
			mSoundManager.play(R.raw.amount_low);
            Thread.sleep(100)
		}
	}
    private fun withLogin(topic: Topic, topicId: String, position: Int){
        sound = sharedPrefs?.getBooleanPrefVal(context!!, SOUNDS) ?: true
        // mediaPlayer = MediaPlayer.create(activity,R.raw.amount_low)

        //int maxSimultaneousStreams = 3;
        /*mSoundManager = SoundManager(context, 5);
        mSoundManager.start();
        mSoundManager.load(R.raw.select_high_correct);
        mSoundManager.load(R.raw.select_high_correct);*/
        //mSoundManager.load(R.raw.my_sound_3);
        if(!sound){
            //MusicManager.getInstance().play(context, R.raw.amount_low);
            // Is the sound loaded already?
            if (loaded) {
                soundPool.play(soundID, volume, volume, 1, 0, 1f);
                Log.e("Test", "Played sound...volume..."+ volume);
                //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
            }
            // getPlayer(context).start()
            // getPlayer(context).setOnCompletionListener {
            //    Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
            //   mediaPlayer.release()
            //  Thread.sleep(100)
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

            Log.e("chapter fragment.....","jsonStringBasic......."+jsonStringBasic);

            val intent = Intent(context!!, StartQuizActivityNew::class.java)
            intent.putExtra(TOPIC, topic)
            intent.putExtra(TOPIC_NAME, topic.title)
            intent.putExtra(FOLDER_NAME, topic.folderName)
            intent.putExtra(DYNAMIC_PATH, jsonStringBasic)
            intent.putExtra(COURSE_ID, courseId)
            intent.putExtra(COURSE_NAME, courseName)
            intent.putExtra(TOPIC_ID, topicId)
            intent.putExtra(TOPIC_POSITION, position)
            intent.putExtra(FOLDER_PATH, localPath)
            intent.putExtra(TITLE_TOPIC, gradeTitle!!)
            intent.putExtra("LAST_PLAYED", lastplayed)
            intent.putExtra("comingfrom", "Topics")
            intent.putExtra(TOPIC_LEVEL, "")
            intent.putExtra(LEVEL_COMPLETED, "")
            intent.putExtra(CARD_NO, "")
            startActivity(intent)

            // }
        }else{
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

            Log.e("chapter fragment.....","jsonStringBasic......."+jsonStringBasic);

            val intent = Intent(context!!, StartQuizActivityNew::class.java)
            intent.putExtra(TOPIC, topic)
            intent.putExtra(TOPIC_NAME, topic.title)
            intent.putExtra(FOLDER_NAME, topic.folderName)
            intent.putExtra(DYNAMIC_PATH, jsonStringBasic)
            intent.putExtra(COURSE_ID, courseId)
            intent.putExtra(COURSE_NAME, courseName)
            intent.putExtra(TOPIC_ID, topicId)
            intent.putExtra(TOPIC_POSITION, position)
            intent.putExtra(FOLDER_PATH, localPath)
            intent.putExtra(TITLE_TOPIC, gradeTitle!!)
            intent.putExtra("LAST_PLAYED", lastplayed)
            intent.putExtra("comingfrom", "Topics")
            intent.putExtra(TOPIC_LEVEL, "")
            intent.putExtra(LEVEL_COMPLETED, "")
            intent.putExtra(CARD_NO, "")
            startActivity(intent)
        }
    }

    private fun callIntent(topic: Topic, topicId: String, position: Int){

        if((sharedPrefs?.getBooleanPrefVal(context!!, ISNOTLOGIN) ?: true)){
            withLogin(topic, topicId, position)
        }else{
            sound = sharedPrefs?.getBooleanPrefVal(context!!, SOUNDS) ?: true
            if(position >  1){
                if(!sound) {
                    //MusicManager.getInstance().play(context, R.raw.amount_low);
                    // Is the sound loaded already?
                    if (loaded) {
                        soundPool.play(soundID, volume, volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..." + volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent(context!!, SignInActivity::class.java)
                    intent.putExtra(TOPIC, topic)
                    intent.putExtra(COURSE_ID, courseId)
                    intent.putExtra(COURSE_NAME, courseName)
                    intent.putExtra(TOPIC_ID, topicId)
                    intent.putExtra(TOPIC_POSITION, position)
                    intent.putExtra(FOLDER_PATH, localPath)
                    intent.putExtra(TITLE_TOPIC, gradeTitle!!)
                    startActivity(intent)
                }else{
                    val intent = Intent(context!!, SignInActivity::class.java)
                    intent.putExtra(TOPIC, topic)
                    intent.putExtra(COURSE_ID, courseId)
                    intent.putExtra(COURSE_NAME, courseName)
                    intent.putExtra(TOPIC_ID, topicId)
                    intent.putExtra(TOPIC_POSITION, position)
                    intent.putExtra(FOLDER_PATH, localPath)
                    intent.putExtra(TITLE_TOPIC, gradeTitle!!)
                    startActivity(intent)
                }

            }else{
                withLogin(topic, topicId, position)
            }
        }

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

    override fun onClick(topic: Topic, topicId: String, position: Int) {
       /* val bundle = Bundle()
        bundle.putString("Category", "Quiz")
        bundle.putString("Action", "Topic")
        bundle.putString("Label", topic.title)
        firebaseAnalytics?.logEvent("Quiz", bundle)*/

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, topic.title)
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Quiz")
        // bundle.putString("Label", "TestGo")
        firebaseAnalytics?.logEvent("Topic", bundle)


        callIntent(topic, topicId, position)
        //val intent = Intent(context!!, QuizSummaryActivityNew::class.java)
        //startActivity(intent)
    }


}
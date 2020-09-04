package com.blobcity.activity


import android.content.Intent
import android.os.Build

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.blobcity.R
import com.blobcity.model.TopicOneBasicResponseModel
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.SharedPrefs
import com.blobcity.utils.Utils
import com.blobcity.viewmodel.TopicStatusVM
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_start_quiz_new.*
import android.widget.LinearLayout

import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProviders
import com.blobcity.model.Topic
import com.google.firebase.analytics.FirebaseAnalytics


class StartQuizActivityNew : BaseActivity(), View.OnClickListener {


    var courseId: String? = ""
    var topicId: String? = ""
    var topicLevel: String? = ""
    var complete: String? = ""
    var topicStatusVM: TopicStatusVM? = null
    var dynamicPath: String? = null
    var folderName: String? = null
    var gradeTitle: String? = null
    var readyCardNumber = 0
    var courseName: String? = ""
    var topicName: String? = ""
    var lastplayed:String = ""
    var comingfrom:String = ""
    var dbPosition: Int? = null
    var oPath: String? = null
    private var totalQuestion: Int? = null
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    lateinit var topic: Topic
    lateinit var circles: Array<ImageView?>
    var mLastClickTime:Long = 0;
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override var layoutID: Int = R.layout.activity_start_quiz_new

    override fun initView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorbottomnav));
        }
        sharedPrefs = SharedPrefs()
        topic = intent.getSerializableExtra(ConstantPath.TOPIC) as Topic
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        dynamicPath = intent.getStringExtra(ConstantPath.DYNAMIC_PATH)
        courseId = intent.getStringExtra(ConstantPath.COURSE_ID)
        topicId = intent.getStringExtra(ConstantPath.TOPIC_ID)
        courseName = intent.getStringExtra(ConstantPath.COURSE_NAME)
        topicLevel = intent.getStringExtra(ConstantPath.TOPIC_LEVEL)
        topicName = intent.getStringExtra(ConstantPath.TOPIC_NAME)
        complete = intent.getStringExtra(ConstantPath.LEVEL_COMPLETED)
        dbPosition = intent.getIntExtra(ConstantPath.TOPIC_POSITION, -1)
        oPath = intent.getStringExtra(ConstantPath.FOLDER_PATH)
        folderName = intent.getStringExtra(ConstantPath.FOLDER_NAME)
        gradeTitle = intent.getStringExtra(ConstantPath.TITLE_TOPIC)
        readyCardNumber = intent.getIntExtra(ConstantPath.CARD_NO, -1)

        comingfrom = intent.getStringExtra("comingfrom")
        try{
            lastplayed = intent.getStringExtra("LAST_PLAYED")
        }catch (e:Exception){

        }
        val gsonFile = Gson()
        val questionResponseModel = gsonFile.fromJson(dynamicPath, TopicOneBasicResponseModel::class.java)
        val questionsItems = questionResponseModel.questions
        val listWithDuplicateKeys = ArrayList<String>()

        totalQuestion = questionResponseModel.questionCount

        if(comingfrom.equals("Home")){
            txt_prev.text = "BACK"
        }else{
            txt_prev.text = "BACK"
        }

        if(topic.displayNo < 10){
            title_no.text = "0"+topic.displayNo
        }else{
            title_no.text = ""+topic.displayNo
        }

        tv_quiz_title.text = topic.title
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        circles = arrayOfNulls<ImageView>(totalQuestion!!)
        ll_answers.removeAllViews()
        for(i in 0 until totalQuestion!!){
            val params = LinearLayout.LayoutParams(getResources().getDimension(R.dimen._18sdp).toInt(), getResources().getDimension(R.dimen._18sdp).toInt())
            if(i != 0){
                params.leftMargin = getResources().getDimension(R.dimen._17sdp).toInt()
            }

            circles[i] = ImageView(this)

            Glide.with(this)
                .load(R.drawable.empty_circle)
                .into(circles[i]!!)

            circles[i]!!.layoutParams = params
            ll_answers.addView(circles!![i])
        }

        btn_topics!!.setOnClickListener(this)
        btn_start!!.setOnClickListener(this)




    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime()
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
                finish()
            }
            R.id.btn_start -> {
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

                /*val bundle = Bundle()
                bundle.putString("Category", "Quiz")
                bundle.putString("Action", "Launch")
                bundle.putString("Label", topic.title)
                firebaseAnalytics?.logEvent("QuizLaunch", bundle)*/

                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, topic.title)
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Quiz")
                // bundle.putString("Label", "TestGo")
                firebaseAnalytics?.logEvent("Launch", bundle)

                val intent = Intent(this!!, TestQuestionActivity::class.java)
                intent.putExtra(ConstantPath.TOPIC, topic)
                intent.putExtra(ConstantPath.TOPIC_NAME, topic.title)
                intent.putExtra(ConstantPath.FOLDER_NAME, topic.folderName)
                intent.putExtra(ConstantPath.DYNAMIC_PATH, dynamicPath)
                intent.putExtra(ConstantPath.COURSE_ID, courseId)
                intent.putExtra(ConstantPath.COURSE_NAME, courseName)
                intent.putExtra(ConstantPath.TOPIC_ID, topicId)
                intent.putExtra(ConstantPath.TOPIC_POSITION, dbPosition)
                intent.putExtra(ConstantPath.FOLDER_PATH, oPath)
                intent.putExtra(ConstantPath.TITLE_TOPIC, gradeTitle!!)
                intent.putExtra("LAST_PLAYED", lastplayed)
                intent.putExtra("comingfrom", comingfrom)
                intent.putExtra(ConstantPath.TOPIC_LEVEL, "")
                intent.putExtra(ConstantPath.LEVEL_COMPLETED, "")
                intent.putExtra(ConstantPath.CARD_NO, "")
                intent.putExtra("DISPLAY_NO", topic.displayNo)
                startActivity(intent)


            }
        }
    }

}

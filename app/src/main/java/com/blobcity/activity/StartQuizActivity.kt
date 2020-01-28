package com.blobcity.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.blobcity.R
import com.blobcity.model.TopicOneBasicResponseModel
import com.blobcity.utils.*
import com.blobcity.utils.ConstantPath.*
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_quiz_summary.*
import kotlinx.android.synthetic.main.activity_start_quiz.*
import kotlinx.android.synthetic.main.activity_start_quiz.tv_chapter_title
import kotlinx.android.synthetic.main.activity_start_quiz.tv_quiz_level
import java.util.*
import kotlin.collections.ArrayList

class StartQuizActivity : BaseActivity(),View.OnClickListener {

    override var layoutID: Int = R.layout.activity_start_quiz
    private lateinit var mediaPlayer: MediaPlayer
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    lateinit var  mSoundManager: SoundManager;
    override fun initView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val path = intent.getStringExtra(DYNAMIC_PATH)
        val courseId = intent.getStringExtra(COURSE_ID)
        val topicId = intent.getStringExtra(TOPIC_ID)
        val courseName = intent.getStringExtra(COURSE_NAME)
        val topicLevel = intent.getStringExtra(TOPIC_LEVEL)
        val topicName = intent.getStringExtra(TOPIC_NAME)
        val complete = intent.getStringExtra(LEVEL_COMPLETED)
        val position: Int = intent.getIntExtra(TOPIC_POSITION, -1)
        val paths: String = intent.getStringExtra(FOLDER_PATH)
        val folderName: String = intent.getStringExtra(FOLDER_NAME)
        val gradeTitle: String = intent.getStringExtra(TITLE_TOPIC)
        var level = ""
        var readyCardNumber = 0
        val rndImageNumber = Random()
        sharedPrefs = SharedPrefs()
        Log.e("path", path)

        val gsonFile = Gson()
        val questionResponseModel = gsonFile.fromJson(path, TopicOneBasicResponseModel::class.java)

        if (topicLevel.equals("basic")){
            level = "Quiz I"
            readyCardNumber=(rndImageNumber.nextInt(3)+1)
            Glide.with(this@StartQuizActivity)
                .load(Uri.parse(WEBVIEW_PATH+ localQuizReadyCardsPath+"ready-"+readyCardNumber+".png"))
                .into(iv_lock_card)
        }

        if (topicLevel.equals("intermediate")){
            level = "Quiz II"
            readyCardNumber=(rndImageNumber.nextInt(3)+1)
            Glide.with(this@StartQuizActivity)
                .load(Uri.parse(WEBVIEW_PATH+ localQuizReadyCardsPath+"ready-"+readyCardNumber+".png"))
                .into(iv_lock_card)
        }

        if (topicLevel.equals("advanced")){
            level = "Super Quiz"
            readyCardNumber=(rndImageNumber.nextInt(3)+1)
            Glide.with(this@StartQuizActivity)
                .load(Uri.parse(WEBVIEW_PATH+ localSuperQuizReadyCardsPath+"ready-"+readyCardNumber+".png"))
                .into(iv_lock_card)
        }

        tv_quiz_count.text = questionResponseModel.questionCount.toString()
        tv_chapter_title.text = topicName
        tv_quiz_level.text = level

        if (questionResponseModel.questionCount <= 4){
            iv_life3.visibility = View.GONE
        }
        buttonEffect(btn_start)
        btn_start.setOnClickListener {
            sound = sharedPrefs?.getBooleanPrefVal(this, SOUNDS) ?: true
          //  mediaPlayer = MediaPlayer.create(this,R.raw.amount_low)
            if(sound){
                if (Utils.loaded) {
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound");
                   // Toast.makeText(this,"end", Toast.LENGTH_SHORT).show()
                }
              //  playSomeSound()
               //Utils.getPlayer(this).start()
               // Utils.getPlayer(this).setOnCompletionListener {

                   // mediaPlayer.release()
               //     Thread.sleep(100)
                    val intent = Intent(this, TestQuestionActivity::class.java)
                    intent.putExtra(DYNAMIC_PATH, path)
                    intent.putExtra(COURSE_ID, courseId)
                    intent.putExtra(TOPIC_ID, topicId)
                    intent.putExtra(COURSE_NAME, courseName)
                    intent.putExtra(TOPIC_NAME, topicName)
                    intent.putExtra(TOPIC_LEVEL, topicLevel)
                    intent.putExtra(LEVEL_COMPLETED, complete)
                    intent.putExtra(TOPIC_POSITION, position)
                    intent.putExtra(FOLDER_PATH, paths)
                    intent.putExtra(FOLDER_NAME, folderName)
                    intent.putExtra(TITLE_TOPIC, gradeTitle)
                    intent.putExtra(CARD_NO, readyCardNumber)
                    startActivity(intent)
                    finish()

             //   }
            } else {
                val intent = Intent(this, TestQuestionActivity::class.java)
                intent.putExtra(DYNAMIC_PATH, path)
                intent.putExtra(COURSE_ID, courseId)
                intent.putExtra(TOPIC_ID, topicId)
                intent.putExtra(COURSE_NAME, courseName)
                intent.putExtra(TOPIC_NAME, topicName)
                intent.putExtra(TOPIC_LEVEL, topicLevel)
                intent.putExtra(LEVEL_COMPLETED, complete)
                intent.putExtra(TOPIC_POSITION, position)
                intent.putExtra(FOLDER_PATH, paths)
                intent.putExtra(FOLDER_NAME, folderName)
                intent.putExtra(TITLE_TOPIC, gradeTitle)
                intent.putExtra(CARD_NO, readyCardNumber)
                startActivity(intent)
                finish()
            }


        }
        iv_cancel.setOnClickListener(this)

        val displayMetrics=DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayWidth=displayMetrics.widthPixels
        iv_lock_card.layoutParams.width=((0.7*displayWidth).toInt())

    }

    private fun playSomeSound() {
        if (mSoundManager != null) {
            mSoundManager.play(R.raw.amount_low);
        }
    }

    override fun onPause() {
        super.onPause()
        if (mSoundManager != null) {
            mSoundManager.cancel();
            // mSoundManager = null;
        }
    }
    override fun onResume() {
        super.onResume()
        mSoundManager = SoundManager(this, 2);
        mSoundManager.start();
        mSoundManager.load(R.raw.amount_low);
        mSoundManager.load(R.raw.amount_low);
    }
    fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(Color.parseColor("#FF790BF8"), PorterDuff.Mode.SRC_ATOP)
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
    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.iv_cancel -> {
                Log.d("startQuiz","1")
                finish()
            }
        }
    }
}
package com.blobcity.activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.os.Handler

import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import com.blobcity.R
import com.blobcity.model.ChallengeModel
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.SharedPrefs
import com.blobcity.utils.Utils
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_challenge_summary.*
import kotlinx.android.synthetic.main.activity_challenge_summary.btn_play_again
import kotlinx.android.synthetic.main.activity_challenge_summary.iv_cancel_quiz_summary
import kotlinx.android.synthetic.main.activity_challenge_summary.iv_card_back
import kotlinx.android.synthetic.main.activity_challenge_summary.iv_card_front
import kotlinx.android.synthetic.main.activity_challenge_summary.tv_completion_status
import kotlinx.android.synthetic.main.activity_challenge_summary.tv_quiz_level



import java.util.*


class ChallengeSummaryActivity : BaseActivity(), View.OnClickListener  {


    val handler = Handler()
    private var mSetRightOut: AnimatorSet? = null
    private var mSetLeftIn: AnimatorSet? = null
    val rndImageNumber = Random()
    var size = 0
    var challenge: ChallengeModel? = null
    private var isAnswerCorrect: Boolean = false
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    var readyCardNumber = 1
    override var layoutID: Int = R.layout.activity_challenge_summary

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun initView() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorbottomnav));
        }

        challenge = intent.getSerializableExtra("challenge") as? ChallengeModel
        isAnswerCorrect = intent.getBooleanExtra("isanswercorrect",false)

        if(challenge?.serialno!!.toInt() < 10){
            tv_quiz_level.text = "#0"+challenge?.serialno
        }else{
            tv_quiz_level.text = "#"+challenge?.serialno
        }
        changeCameraDistance()
        loadAnimations()
        sharedPrefs = SharedPrefs()
        Log.e("challenge summary","is answer correct...."+isAnswerCorrect);

        Glide.with(this@ChallengeSummaryActivity)
            .load(Uri.parse(ConstantPath.WEBVIEW_PATH + ConstantPath.localSuperQuizReadyCardsPath + "ready-" + readyCardNumber + ".png"))
            .into(iv_card_front)

        //tv_chapter_title.text = challenge?.challengetitle
        if(isAnswerCorrect!!){
            size = 1
        }else{
            size = 0
        }
        btn_play_again.setOnClickListener(this)
        btn_done.setOnClickListener(this)
        iv_cancel_quiz_summary.setOnClickListener(this)
        buttonEffect(btn_play_again)
        buttonEffect(btn_done)
        //val answer_status = "$size / $totalQuestion"
       // tv_answer_status1.text = "$size"

      //  tv_answer_status2.text = "1"
        tv_completion_status.visibility = View.INVISIBLE
        if (isAnswerCorrect!!) {
            /*if (topicLevel!!.contains("basic") || topicLevel!!.contains("intermediate")) {
                Glide.with(this@ChallengeSummaryActivity)
                    .load(
                        Uri.parse(
                            ConstantPath.WEBVIEW_PATH + ConstantPath.localQuizSuccessCardsPath + "success-" + (rndImageNumber.nextInt(
                                4
                            ) + 1) + ".png"
                        )
                    )
                    .into(iv_card_back)
                flipAnimation()
            } else {*/
                Glide.with(this@ChallengeSummaryActivity)
                    .load(
                        Uri.parse(
                            ConstantPath.WEBVIEW_PATH + ConstantPath.localSuperQuizSuccessCardsPath + "success-" + (rndImageNumber.nextInt(
                                15
                            ) + 1) + ".png"
                        )
                    )
                    .into(iv_card_back)
                flipAnimation()
            //}
         //   tv_answer_status1.setTextColor(resources.getColor(R.color.level_completed))
            tv_completion_status.text = "CHALLENGE COMPLETED!"
            tv_completion_status.setTextColor(resources.getColor(R.color.level_completed))
        } else {
            /*if (topicLevel!!.contains("basic") || topicLevel!!.contains("intermediate")) {
                Glide.with(this@ChallengeSummaryActivity)
                    .load(
                        Uri.parse(
                            ConstantPath.WEBVIEW_PATH + ConstantPath.localQuizFailCardsPath + "fail-" + (rndImageNumber.nextInt(
                                4
                            ) + 1) + ".png"
                        )
                    )
                    .into(iv_card_back)
                flipAnimation()
            } else {*/
                Glide.with(this@ChallengeSummaryActivity)
                    .load(
                        Uri.parse(
                            ConstantPath.WEBVIEW_PATH + ConstantPath.localSuperQuizFailCardsPath + "fail-" + (rndImageNumber.nextInt(
                                3
                            ) + 1) + ".png"
                        )
                    )
                    .into(iv_card_back)
                flipAnimation()
           // }
         //   tv_answer_status1.setTextColor(resources.getColor(R.color.level_failed))
            tv_completion_status.text = "CHALLENGE FAILED"
            tv_completion_status.setTextColor(resources.getColor(R.color.level_failed))
        }

        val displayMetrics= DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayWidth=displayMetrics.widthPixels
        iv_card_back.layoutParams.width=((0.7*displayWidth).toInt())
        iv_card_front.layoutParams.width=((0.7*displayWidth).toInt())


    }

    override fun onClick(v: View?) {
        val intent: Intent
        when (v!!.id) {
            R.id.btn_play_again -> {

                sound = sharedPrefs?.getBooleanPrefVal(this!!, ConstantPath.SOUNDS) ?: true
                if(!sound) {
                    //MusicManager.getInstance().play(context, R.raw.amount_low);
                    // Is the sound loaded already?
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..." + Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                    val intent = Intent(this, ChallengeQuestionActivity::class.java)
                    intent.putExtra("challenge", challenge)
                    startActivity(intent)
                    finish()
                }else{
                    val intent = Intent(this, ChallengeQuestionActivity::class.java)
                    intent.putExtra("challenge", challenge)
                    startActivity(intent)
                    finish()
                }




            }
            R.id.btn_done -> {
                sound = sharedPrefs?.getBooleanPrefVal(this!!, ConstantPath.SOUNDS) ?: true
                if(!sound) {
                    //MusicManager.getInstance().play(context, R.raw.amount_low);
                    // Is the sound loaded already?
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..." + Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                    //finish()
                    val i = Intent(this, DashBoardActivity::class.java)
                    i.putExtra("fragment", "dailychallenge")
                    startActivity(i)
                }else{
                    //finish()
                    val i = Intent(this, DashBoardActivity::class.java)
                    i.putExtra("fragment", "dailychallenge")
                    startActivity(i)
                }
            }
            R.id.iv_cancel_quiz_summary -> {
                sound = sharedPrefs?.getBooleanPrefVal(this!!, ConstantPath.SOUNDS) ?: true
                if(!sound) {
                    //MusicManager.getInstance().play(context, R.raw.amount_low);
                    // Is the sound loaded already?
                    if (Utils.loaded) {
                        Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                        Log.e("Test", "Played sound...volume..." + Utils.volume);
                        //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                    }
                    //finish()
                    val i = Intent(this, DashBoardActivity::class.java)
                    i.putExtra("fragment", "dailychallenge")
                    startActivity(i)
                }else{
                    //finish()
                    val i = Intent(this, DashBoardActivity::class.java)
                    i.putExtra("fragment", "dailychallenge")
                    startActivity(i)
                }
            }
        }
    }

    fun buttonEffect(button: View) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.background.setColorFilter(Color.parseColor("#FFBEBCBC"), PorterDuff.Mode.SRC_ATOP)
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
    private fun changeCameraDistance() {
        val distance = 7000
        val scale = resources.displayMetrics.density * distance
        iv_card_front!!.setCameraDistance(scale)
        iv_card_back!!.setCameraDistance(scale)
    }
    @SuppressLint("ResourceType")
    private fun loadAnimations() {
        //zoominAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        //zoomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        mSetRightOut = AnimatorInflater.loadAnimator(this, R.anim.out_animation) as AnimatorSet
        mSetLeftIn = AnimatorInflater.loadAnimator(this, R.anim.in_animation) as AnimatorSet
    }
    private fun flipAnimation() {
        handler.postDelayed({
            //iv_card_front.startAnimation(zoomOutAnimation)
            mSetRightOut!!.setTarget(iv_card_front)
            iv_card_back.visibility= View.VISIBLE
            tv_completion_status.visibility = View.VISIBLE
            mSetLeftIn!!.setTarget(iv_card_back)
            //iv_card_back.startAnimation(zoominAnimation)
            mSetRightOut!!.start()
            mSetLeftIn!!.start()
        }, 1000)
    }
}

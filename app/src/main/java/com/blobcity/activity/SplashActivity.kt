package com.blobcity.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.blobcity.R
import com.blobcity.fragment.ChapterFragment
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_dashboard.*

class SplashActivity : BaseActivity() {

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {

            val intent = Intent(applicationContext, GradeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override var layoutID: Int = R.layout.activity_splash

    override fun initView() {
        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
    }


    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }

}

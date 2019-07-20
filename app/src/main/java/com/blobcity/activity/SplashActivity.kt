package com.blobcity.activity

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import com.blobcity.R
import com.blobcity.fragment.ChapterFragment
import com.blobcity.utils.ForceUpdateChecker
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_dashboard.*

class SplashActivity : BaseActivity(), ForceUpdateChecker.OnUpdateNeededListener {

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        }
    }

    override var layoutID: Int = R.layout.activity_splash

    override fun initView() {
        Log.d("onCreate","Splash")
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

     override fun onUpdateNeeded(updateUrl: String) {
        val dialog = AlertDialog.Builder(this)
                .setTitle("New version available")
                .setMessage("Please, update app to new version to continue.")
            .setPositiveButton("Update", DialogInterface.OnClickListener { dialog, which ->
                redirectStore(updateUrl);

            })
            .setNegativeButton("No ",DialogInterface.OnClickListener { dialog, which ->
                finish();
            })
            .setCancelable(false)
            .setOnDismissListener {
                finish()
            }
            .create()


        dialog.show();
    }

    fun redirectStore(updateUrl : String) {
        var intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent)
        finish()
    }
}

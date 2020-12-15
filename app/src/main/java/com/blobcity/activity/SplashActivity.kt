package com.blobcity.activity

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build

import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle

import android.util.Log
import com.blobcity.R
import com.blobcity.fragment.ChapterFragment
import com.blobcity.utils.ForceUpdateChecker
import com.blobcity.utils.SharedPrefs
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_dashboard.*

import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.appcompat.app.AlertDialog


class SplashActivity : BaseActivity(), ForceUpdateChecker.OnUpdateNeededListener {

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 1000 //1 seconds
    var sharedPrefs: SharedPrefs? = null
    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        }
    }
    var value = ""
    override var layoutID: Int = R.layout.activity_splash

    override fun initView() {

        Log.d("onCreate","Splash")
        /*val action: String? = intent?.action
        val data: Uri? = intent?.data
        var extras:Bundle? = intent.extras

        if(intent.hasExtra("screen")){
            if(extras != null){
                value = extras.getString("screen")!!

            }
        }

        Log.e("splash activity","value......"+value);
        Log.e("splash activity","action......"+action);
        Log.e("splash activity","data......"+data.toString());
        sharedPrefs = SharedPrefs()
        sharedPrefs!!.setPrefVal(this,"action", action!!)
        if(value != ""){
            Log.e("splash activity","value...if..."+value);
            sharedPrefs!!.setPrefVal(this,"data", value)
        }else{
            Log.e("splash activity","value...else..."+value);
            sharedPrefs!!.setPrefVal(this,"data", data.toString())
        }*/



        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorbottomnav));
        }
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e("splash screen","on new intent....isTaskRoot..."+isTaskRoot);


        /*val action: String? = intent?.action
        val data: Uri? = intent?.data
        var extras:Bundle? = intent?.extras

        if(intent!!.hasExtra("screen")){
            if(extras != null){
                value = extras.getString("screen")!!

            }
        }

        Log.e("splash activity","value......"+value);
        Log.e("splash activity","on new intent...action......"+action);
        Log.e("splash activity","on new intent....data......"+data.toString());
        sharedPrefs = SharedPrefs()
        sharedPrefs!!.setPrefVal(this,"action", action!!)
        if(value != ""){
            sharedPrefs!!.setPrefVal(this,"data", value)
        }else{
            sharedPrefs!!.setPrefVal(this,"data", data.toString())
        }*/


        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
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

package com.blobcity.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.WindowManager
import com.blobcity.R
import com.blobcity.database.DatabaseHandler
import com.blobcity.database.QuizGameDataBase

import com.blobcity.utils.ConstantPath
import com.blobcity.utils.SharedPrefs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.setting_layout.*

class SettingsActivity : BaseActivity(), View.OnClickListener {


    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    var notification: Boolean = false
    val TAG: String = "Settings Fragment"
    var databaseHandler: QuizGameDataBase?= null

    var databaseHandler1: DatabaseHandler? = null
    private lateinit var auth: FirebaseAuth

    private var mGoogleSignInClient: GoogleSignInClient? = null

    override var layoutID: Int = R.layout.setting_layout

    override fun initView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorbottomnav));
        }
        try {
            var currentVersion = packageManager.getPackageInfo(packageName, 0).versionName
            versionname.text = currentVersion
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        settings.elevation = 15f
        sharedPrefs = SharedPrefs()
        databaseHandler = QuizGameDataBase(this);
        databaseHandler1 = DatabaseHandler(this);
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        sound = sharedPrefs?.getBooleanPrefVal(this, ConstantPath.SOUNDS) ?: true
        cb_sounds_settings.isChecked = !sound
        if(!sound){
            sharedPrefs?.setBooleanPrefVal(this, ConstantPath.SOUNDS, sound)
            sound_state_tv.text = "Sound On"
        }
        else{
            sound_state_tv.text = "Sound Off"
        }
        cb_sounds_settings.setOnClickListener {
            sound = !sound
            sharedPrefs?.setBooleanPrefVal(this, ConstantPath.SOUNDS, sound)
            cb_sounds_settings.isChecked = !sound
            if(!sound){
                sound_state_tv.text = "Sound On"
            }
            else{
                sound_state_tv.text = "Sound Off"
            }
        }
        notification = sharedPrefs?.getBooleanPrefVal(this, ConstantPath.NOTIFICATION) ?: true
        cb_notifications_settings.isChecked = !notification
        if(!notification){
            sharedPrefs?.setBooleanPrefVal(this, ConstantPath.NOTIFICATION, notification)
            notification_state_tv.text = "Notifications On"
        }
        else{
            notification_state_tv.text = "Notifications Off"
        }
        cb_notifications_settings.setOnClickListener{
            notification=!notification
            sharedPrefs?.setBooleanPrefVal(this, ConstantPath.NOTIFICATION, notification)
            cb_notifications_settings.isChecked = !notification
            if(!notification){
                notification_state_tv.text = "Notifications On"
            }
            else{
                notification_state_tv.text = "Notifications Off"
            }
        }

        if((sharedPrefs?.getBooleanPrefVal(this, ConstantPath.ISNOTLOGIN) ?: true)){
            //sign in
            txt_sign_in.text = "Sign Out"
        }else{
            //not sign in
            txt_sign_in.text = "Sign In"
        }

        cl_terms_and_conditions.setOnClickListener(this)
        cl_write_to_us.setOnClickListener(this)
        signout.setOnClickListener(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onClick(v: View?) {
        val intent: Intent

        when (v!!.id) {
            R.id.cl_terms_and_conditions -> {
                intent = Intent(this@SettingsActivity, WriteToUsActivity::class.java)
                intent.putExtra("activityname", "termsandconditions")
                startActivity(intent)
            }

            R.id.cl_write_to_us -> {
                intent = Intent(this@SettingsActivity, WriteToUsActivity::class.java)
                intent.putExtra("activityname", "writetous")
                startActivity(intent)
            }

            R.id.signout -> {
                if(txt_sign_in.text.equals("Sign In")) {
                    val intent = Intent(this@SettingsActivity!!, SignInActivity::class.java)
                    startActivity(intent)
                    //txt_sign_in.text = "Sign Out"
                } else if(txt_sign_in.text.equals("Sign Out")){
                    //passtxt_sign_in.text = "Sign In"
                    showDialog(this@SettingsActivity)
                }
            }
        }
    }

    private fun showDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder!!.setTitle("Sign Out")
        builder.setMessage("Are you sure, You want to sign out?")
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            dialog.dismiss()
            sharedPrefs?.setBooleanPrefVal(context!!, ConstantPath.ISNOTLOGIN, false)
            sharedPrefs?.setBooleanPrefVal(context!!, ConstantPath.IS_FIRST_TIME, true)

            //sharedPrefs?.setBooleanPrefVal(context!!, ConstantPath.IS_LOGGED_IN, false)

            databaseHandler!!.deleteAllQuizTopicsLatPlayed()
            databaseHandler!!.deleteAllQuizPlayFinal()
            databaseHandler1!!.deleteAllRevisions()
            databaseHandler1!!.deleteAllBookStatus()
            // Firebase sign out
            auth.signOut()

            // Google sign out

            mGoogleSignInClient!!.signOut().addOnCompleteListener(this,
                OnCompleteListener<Void> {
                    //updateUI(null)
                })
            val intent = Intent(context!!, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }

}

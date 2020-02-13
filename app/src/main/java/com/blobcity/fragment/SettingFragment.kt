package com.blobcity.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.blobcity.R
import com.blobcity.activity.DashBoardActivity
import com.blobcity.activity.SignInActivity
import com.blobcity.activity.WriteToUsActivity
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.ConstantPath.NOTIFICATION
import com.blobcity.utils.ConstantPath.SOUNDS
import com.blobcity.utils.SharedPrefs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.setting_layout.*

class SettingFragment : Fragment(), View.OnClickListener {

    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    var notification: Boolean = false
    val TAG: String = "Settings Fragment"
    private lateinit var auth: FirebaseAuth

    private var mGoogleSignInClient: GoogleSignInClient? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.setting_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = SharedPrefs()

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this!!.activity!!, gso)

        sound = sharedPrefs?.getBooleanPrefVal(context!!, SOUNDS) ?: true
        cb_sounds_settings.isChecked = !sound
        if(!sound){
            sharedPrefs?.setBooleanPrefVal(context!!, SOUNDS, sound)
            sound_state_tv.text = "Sound On"
        }
        else{
            sound_state_tv.text = "Sound Off"
        }
        cb_sounds_settings.setOnClickListener {
            sound = !sound
            sharedPrefs?.setBooleanPrefVal(context!!, SOUNDS, sound)
            cb_sounds_settings.isChecked = !sound
            if(!sound){
                sound_state_tv.text = "Sound On"
            }
            else{
                sound_state_tv.text = "Sound Off"
            }
        }
        notification = sharedPrefs?.getBooleanPrefVal(context!!, NOTIFICATION) ?: true
        cb_notifications_settings.isChecked = !notification
        if(!notification){
            sharedPrefs?.setBooleanPrefVal(context!!, NOTIFICATION, notification)
            notification_state_tv.text = "Notifications On"
        }
        else{
            notification_state_tv.text = "Notifications Off"
        }
        cb_notifications_settings.setOnClickListener{
            notification=!notification
            sharedPrefs?.setBooleanPrefVal(context!!, NOTIFICATION, notification)
            cb_notifications_settings.isChecked = !notification
            if(!notification){
                notification_state_tv.text = "Notifications On"
            }
            else{
                notification_state_tv.text = "Notifications Off"
            }
        }

        if((sharedPrefs?.getBooleanPrefVal(context!!, ConstantPath.ISNOTLOGIN) ?: true)){
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

    override fun onClick(v: View?) {
        val intent: Intent

        when (v!!.id) {
            R.id.cl_terms_and_conditions -> {
                intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))
                startActivity(intent)
            }

            R.id.cl_write_to_us -> {
                intent = Intent(context, WriteToUsActivity::class.java)
                startActivity(intent)
            }

            R.id.signout -> {
                if(txt_sign_in.text.equals("Sign In")) {
                    val intent = Intent(context!!, SignInActivity::class.java)
                    startActivity(intent)
                    //txt_sign_in.text = "Sign Out"
                } else if(txt_sign_in.text.equals("Sign Out")){
                    //passtxt_sign_in.text = "Sign In"
                    showDialog(context!!)
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
            // Firebase sign out
            auth.signOut()

            // Google sign out

            mGoogleSignInClient!!.signOut().addOnCompleteListener(this,
                OnCompleteListener<Void> {
                    //updateUI(null)
                })
            val intent = Intent(context!!, DashBoardActivity::class.java)
            startActivity(intent)
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.dismiss()
        }

        builder.show()
    }



}

private fun <TResult> Task<TResult>.addOnCompleteListener(settingFragment: SettingFragment, onCompleteListener: OnCompleteListener<TResult>) {

}

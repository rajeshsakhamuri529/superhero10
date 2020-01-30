package com.blobcity.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.R
import com.blobcity.activity.WriteToUsActivity
import com.blobcity.utils.ConstantPath.NOTIFICATION
import com.blobcity.utils.ConstantPath.SOUNDS
import com.blobcity.utils.SharedPrefs
import kotlinx.android.synthetic.main.setting_layout.*

class SettingFragment : Fragment(), View.OnClickListener {

    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    var notification: Boolean = false
    val TAG: String = "Settings Fragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.setting_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = SharedPrefs()
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

        cl_terms_and_conditions.setOnClickListener(this)
        cl_write_to_us.setOnClickListener(this)
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
        }
    }
}
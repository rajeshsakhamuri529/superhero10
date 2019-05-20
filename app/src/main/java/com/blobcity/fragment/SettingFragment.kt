package com.blobcity.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.blobcity.R
import com.blobcity.utils.ConstantPath
import kotlinx.android.synthetic.main.setting_layout.*
import kotlinx.android.synthetic.main.setting_layout.view.*

class SettingFragment : Fragment() {

    var sharedPreferences: SharedPreferences? =
        null;/*getSharedPreferences(ConstantPath.ANONYMOUS_USER, Context.MODE_PRIVATE)*/
    var sound: Boolean = false
    var notification: Boolean = false
    val TAG: String = "Settings Fragment"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.setting_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = activity?.getSharedPreferences(ConstantPath.SETTINGS, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        var cb_sounds = view.cb_sounds_settings

        sound = sharedPreferences?.getBoolean("sounds", true) ?: true
        Log.d(TAG,"!"+sound)
        if (sound) {
            cb_sounds.isChecked = true
        } else {
            cb_sounds.isChecked = false
        }
        cb_sounds.setOnClickListener(View.OnClickListener {
            sound = !sound
            editor?.putBoolean("sounds", sound)
            editor?.apply()
            Log.d(TAG,"!"+sound)
            if (sound) {
                cb_sounds.isChecked = true
            } else {
                cb_sounds.isChecked = false
            }
        })

    }
}
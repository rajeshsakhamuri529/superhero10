package com.blobcity.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.R
import com.blobcity.utils.ConstantPath.SOUNDS
import com.blobcity.utils.SharedPrefs
import kotlinx.android.synthetic.main.setting_layout.view.*

class SettingFragment : Fragment() {

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
        val cb_sounds = view.cb_sounds_settings

        sound = sharedPrefs?.getBooleanPrefVal(context!!, SOUNDS) ?: true
        Log.d(TAG,"!"+sound)
        cb_sounds.isChecked = sound
        cb_sounds.setOnClickListener {
            sound = !sound
            sharedPrefs?.setBooleanPrefVal(context!!, SOUNDS, sound)
            Log.d(TAG,"!"+sound)
            cb_sounds.isChecked = sound
        }
    }
}
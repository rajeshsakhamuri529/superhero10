package com.blobcity.ViewPager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.blobcity.R
import com.blobcity.ViewPager.util.UiHelper

internal class IntroFourthFragment : Fragment() {

    private val uiHelper = UiHelper()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //view.findViewById<TextView>(R.id.notificationAlertsTextView).typeface = uiHelper.getTypeFace(
        //    TypeFaceEnum.HEADING_TYPEFACE, activity!!)
        //view.findViewById<TextView>(R.id.notificationAlertsSubTitleTextView).typeface = uiHelper.getTypeFace(TypeFaceEnum.SEMI_TITLE_TYPEFACE, activity!!)
        return layoutInflater.inflate(R.layout.intro_fourth_fragment, container, false)
    }

}
package com.blobcity.ViewPager.fragments

import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.yomplex.simple.R
import com.yomplex.simple.ViewPager.util.UiHelper


class IntroSecondFragment : Fragment() {

    private val uiHelper = UiHelper()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.intro_second_fragment, container, false)
     //   view.findViewById<TextView>(R.id.easyPaymentTextView).typeface = uiHelper.getTypeFace(
      //      TypeFaceEnum.HEADING_TYPEFACE, activity!!)
      //  view.findViewById<TextView>(R.id.easyPaymentSubTitleTextView).typeface = uiHelper.getTypeFace(TypeFaceEnum.SEMI_TITLE_TYPEFACE, activity!!)
        return view
    }
}
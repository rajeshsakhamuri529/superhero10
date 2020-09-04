package com.blobcity.activity


import android.os.Build


import android.text.Html
import android.widget.LinearLayout
import android.widget.TextView
import com.blobcity.R
import com.blobcity.viewmodel.TopicStatusVM

import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_test_question_new.*


class TestQuestionNewActivity : BaseActivity() {
    lateinit var dot: Array<TextView?>
    var topicStatusVM: TopicStatusVM? = null
    override var layoutID: Int = R.layout.activity_test_question_new

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun initView() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        addDot()
    }


    fun addDot() {
        //val layout_dot = findViewById(R.id.ll_dots) as LinearLayout
        dot = arrayOfNulls<TextView>(8)
        ll_dots.removeAllViews()
        for (i in 0 until dot!!.size)
        {
            if(i == 2){
                dot!![i] = TextView(this)
                dot!![i]?.setText(Html.fromHtml("&#9679;"))
                dot!![i]?.setTextSize(25F)
                //set default dot color
                dot!![i]?.setTextColor(getResources().getColor(R.color.button_close_text))
                ll_dots.addView(dot!![i])
            }else{
                dot!![i] = TextView(this)
                dot!![i]?.setText(Html.fromHtml("&#9675;"))
                dot!![i]?.setTextSize(25F)
               // dot!![i]?.setBackgroundResource(R.color.purple)
                //set default dot color
                dot!![i]?.setTextColor(getResources().getColor(R.color.button_close_text))
                ll_dots.addView(dot!![i])
            }

        }
        //set active dot color
       // dot[2]!!.setTextColor(getResources().getColor(R.color.button_close_text))
    }

}

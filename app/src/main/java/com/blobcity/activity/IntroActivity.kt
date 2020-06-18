package com.blobcity.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.blobcity.R
import com.blobcity.ViewPager.adapter.MyPagerAdapter
import com.blobcity.ViewPager.fragments.IntroFirstFragment
import com.blobcity.ViewPager.fragments.IntroFourthFragment
import com.blobcity.ViewPager.fragments.IntroSecondFragment
import com.blobcity.ViewPager.fragments.IntroThirdFragment
import com.blobcity.ViewPager.listener.ViewPagerListener
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.SharedPrefs
import com.blobcity.utils.Utils
import com.blobcity.viewmodel.TopicStatusVM
import kotlinx.android.synthetic.main.activity_sign_in.*

class IntroActivity : BaseActivity() {
    var topicStatusVM: TopicStatusVM?= null
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    private lateinit var pagerAdapterView: MyPagerAdapter

    override var layoutID: Int = R.layout.activity_intro
    override fun initView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        sharedPrefs = SharedPrefs()
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorbottomnav));
        }

        sharedPrefs!!.setBooleanPrefVal(this, ConstantPath.IS_FIRST_TIME, false)
        pagerAdapterView = MyPagerAdapter(supportFragmentManager)
        addPagerFragments()
        myViewPager.adapter = pagerAdapterView
        // myViewPager.setPageTransformer(true, this::zoomOutTransformation)
        //getStartedButton.typeface = uiHelper.getTypeFace(TypeFaceEnum.BUTTON_TEXT, this)
        myViewPager.addOnPageChangeListener(ViewPagerListener(this::onPageSelected))

        iv_cancel_quiz_summary.setOnClickListener {
            sound = sharedPrefs?.getBooleanPrefVal(this!!, ConstantPath.SOUNDS) ?: true

            if(!sound) {
                //MusicManager.getInstance().play(context, R.raw.amount_low);
                // Is the sound loaded already?
                if (Utils.loaded) {
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound...volume..." + Utils.volume);
                    //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                }
                val intent = Intent(this!!, DashBoardActivity::class.java)

                startActivity(intent)
            }else{
                val intent = Intent(this!!, DashBoardActivity::class.java)

                startActivity(intent)
            }

        }
        getStartedButton.setOnClickListener {
            sound = sharedPrefs?.getBooleanPrefVal(this!!, ConstantPath.SOUNDS) ?: true

            if(!sound) {
                //MusicManager.getInstance().play(context, R.raw.amount_low);
                // Is the sound loaded already?
                if (Utils.loaded) {
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound...volume..." + Utils.volume);
                    //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                }
                navigateToDashboard("GRADE 6")
            }else{
                navigateToDashboard("GRADE 6")
            }

        }
    }
    fun navigateToDashboard(title: String){
        val intent = Intent(
            this@IntroActivity,
            DashBoardActivity::class.java
        )
        // intent.putExtra(ConstantPath.TITLE_TOPIC, title)
        startActivity(intent)
    }
    private fun onPageSelected(position: Int) {
        when (position) {
            0 -> {
                firstDotImageView.setImageResource(R.drawable.selected_dot)
                secondDotImageView.setImageResource(R.drawable.unselected_dot)
                thirdDotImageView.setImageResource(R.drawable.unselected_dot)
                fourthDotImageView.setImageResource(R.drawable.unselected_dot)
            }
            1 -> {
                firstDotImageView.setImageResource(R.drawable.unselected_dot)
                secondDotImageView.setImageResource(R.drawable.selected_dot)
                thirdDotImageView.setImageResource(R.drawable.unselected_dot)
                fourthDotImageView.setImageResource(R.drawable.unselected_dot)
            }
            2 -> {
                firstDotImageView.setImageResource(R.drawable.unselected_dot)
                secondDotImageView.setImageResource(R.drawable.unselected_dot)
                thirdDotImageView.setImageResource(R.drawable.selected_dot)
                fourthDotImageView.setImageResource(R.drawable.unselected_dot)
            }
            3 -> {
                firstDotImageView.setImageResource(R.drawable.unselected_dot)
                secondDotImageView.setImageResource(R.drawable.unselected_dot)
                thirdDotImageView.setImageResource(R.drawable.unselected_dot)
                fourthDotImageView.setImageResource(R.drawable.selected_dot)
            }
        }
    }

    private fun addPagerFragments() {
        pagerAdapterView.addFragments(IntroFirstFragment())
        pagerAdapterView.addFragments(IntroSecondFragment())
        pagerAdapterView.addFragments(IntroThirdFragment())
        pagerAdapterView.addFragments(IntroFourthFragment())
    }

}

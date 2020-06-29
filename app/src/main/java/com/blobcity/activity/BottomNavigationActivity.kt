package com.blobcity.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import com.blobcity.R
import kotlinx.android.synthetic.main.activity_bottom_navigation.*

class BottomNavigationActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_chapter -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            /*R.id.nav_astra_cards -> {
                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }*/
            /*R.id.nav_settings -> {
                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }*/
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}

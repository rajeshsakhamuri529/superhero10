package com.blobcity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        rcv_dashboard.adapter = ChaptersAdapter(this)
        rl_chapter_one.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
        }
    }
}
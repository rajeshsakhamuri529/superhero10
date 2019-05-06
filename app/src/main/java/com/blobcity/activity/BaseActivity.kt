package com.blobcity.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(setLayout())
        initView()
    }

    abstract fun setLayout(): Int

    abstract fun initView()
}
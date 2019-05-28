package com.blobcity.activity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

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

    fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.
            CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnected == true
        return isConnected
    }

    fun loadJSONFromAsset(path: String): String? {
        val json: String?
        try {
            val `is` = assets.open(path)
            val size = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            json = String(buffer, Charset.forName("UTF-8"))
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }

    fun getListOfFilesFromAsset(path: String): List<String>? {
        val assetManager = assets
        try {
            return Arrays.asList(*assetManager.list(path)!!)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }
}
package com.blobcity.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.blobcity.R
import kotlinx.android.synthetic.main.activity_write_to_us.*

class WriteToUsActivity : AppCompatActivity() {

    private val url = "https://google.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_to_us)

        wv_write_to_us.webViewClient = object : WebViewClient() {

            /*override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if(url!!.startsWith("https://google.com")){
                    wv_write_to_us.loadUrl(url)
                }
                else{
                    val intent = Intent(ACTION_VIEW, Uri.parse(url))
                    view?.context?.startActivity(intent)
                }
                return super.shouldOverrideUrlLoading(view, url)
            }*/

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                view?.visibility = View.INVISIBLE
                progress_bar_wv.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                view?.visibility = View.VISIBLE
                progress_bar_wv.visibility = View.INVISIBLE
            }
        }
        wv_write_to_us.loadUrl(url)

    }

}
package com.blobcity.activity

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.webkit.*
import com.blobcity.R
import kotlinx.android.synthetic.main.activity_write_to_us.*

class WriteToUsActivity : AppCompatActivity() {

    var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_to_us)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        val activityname = intent.getStringExtra("activityname")
        if(activityname == "writetous"){
            url = "https://www.yomplex.com/#contact-us"
        } else if(activityname == "termsandconditions"){
            url = "https://www.yomplex.com/privacy.html"
        }
        wv_write_to_us.settings.javaScriptEnabled = true
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
                Log.e("write to us activity","onpage started....");
                view?.visibility = View.INVISIBLE
                progress_bar_wv.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.e("write to us activity","onPageFinished....");
                view?.visibility = View.VISIBLE
                progress_bar_wv.visibility = View.INVISIBLE
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)

                Log.e("write to us activity","onReceivedError...."+error);
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                super.onReceivedHttpError(view, request, errorResponse)
                Log.e("write to us activity","onReceivedHttpError...."+errorResponse);
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler?,
                error: SslError?
            ) {
                super.onReceivedSslError(view, handler, error)
                Log.e("write to us activity","onReceivedSslError...."+error);
            }

            override fun onRenderProcessGone(
                view: WebView?,
                detail: RenderProcessGoneDetail?
            ): Boolean {
                return super.onRenderProcessGone(view, detail)
                Log.e("write to us activity","onRenderProcessGone....");
            }




        }
        wv_write_to_us.loadUrl(url)

    }

}
package com.blobcity.activity;

import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blobcity.R;
import com.blobcity.utils.FileDownloader;

import java.io.File;
import java.io.IOException;

public class PDFViewActivity extends AppCompatActivity {

    WebView webView;
    Bundle b;
    String str_file_name = "",str_url = "";
    private ProgressDialog mProg;

  //  Toolbar toolbar;
    int MyDeviceAPI= Build.VERSION.SDK_INT;
    Drawable upArrow;


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);

        webView = (WebView) findViewById(R.id.webView);

       // toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

//        getSupportActionBar().setTitle("PDF VIEW");

        /*if(MyDeviceAPI>=21) {
            upArrow = getResources().getDrawable(R.mipmap.app_icon,null);
        }
        else
        {
            upArrow = getResources().getDrawable(R.mipmap.app_icon);
        }

        if(MyDeviceAPI>=23)
        {
            upArrow.setColorFilter(getResources().getColor(R.color.white,null), PorterDuff.Mode.SRC_ATOP);
        }
        else
        {
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }

        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
*/

        /*if(MyDeviceAPI>=23)
        {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white,null));
        }
        else
        {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        }*/
        mProg = new ProgressDialog(PDFViewActivity.this);
        mProg.setMessage("Please Wait...");
        mProg.show();
        //mProg = MyProgressDailog.showProgressDialog(PDFViewActivity.this, getResources().getString(R.string.please_wait));

        b = getIntent().getExtras();
        if(b != null)
        {
            str_file_name = b.getString("pdfUrl");

            str_url = "http://docs.google.com/gview?embedded=true&url="+ str_file_name;
            //str_url = "http://drive.google.com/viewerng/viewer?embedded=true&url="+str_file_name;
            webView.setWebViewClient(new MyBrowser());
            webView.getSettings().setBuiltInZoomControls(false);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setSupportZoom(true);
            webView.loadUrl(str_url);
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return (false);
        }
        //String url = "javascript:(function() {"+"document.querySelector('[role=\"toolbar\"]').remove();})()";
        @Override
        public void onUnhandledKeyEvent(WebView view, KeyEvent ke) {

            Log.e("Unhandled Key Event",ke.toString());
            mProg.cancel();

        }

        @Override
        public void onPageFinished(WebView view, String url) {

            mProg.cancel();
            super.onPageFinished(view, url);
            webView.loadUrl("javascript:(function() { " +
                    "document.querySelector('[role=\"toolbar\"]').remove();})()");
            //webView.loadUrl("javascript:(function() {\"+\"document.querySelector('[role=\\\"toolbar\\\"]').remove();})()");
        }

    }



}

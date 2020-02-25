package com.blobcity.activity

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.WebViewClient
import android.widget.Toast
import com.blobcity.R
import com.blobcity.utils.Utils
import kotlinx.android.synthetic.main.activity_pdfviewer.*
import java.io.File
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL

class PDFViewerActivity : BaseActivity() {
    var url: URL? = null
    var uri: Uri? = null
    lateinit var mFile: File
    override var layoutID: Int = R.layout.activity_pdfviewer

    override fun initView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val path = intent.getStringExtra("rID")
        Log.e("pdf viewer activity","....path....."+path)
        try {

            mFile = File(getExternalFilesDir(null), path+".pdf")
            if (mFile.exists()) {
                pdfView.fromFile(mFile).load()
            }else{
                Toast.makeText(this,"Please connect to the internet!",Toast.LENGTH_LONG).show()
                /*if(Utils.isOnline(this)){

                }else{*/
                    finish()
               // }

            }
        }
        catch (e1:Exception) {
            e1.printStackTrace()
        }

    }


}

package com.blobcity.activity

import android.net.Uri
import android.os.Build
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
import android.content.Intent
import android.R.attr.path
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.support.v4.content.FileProvider

import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.blobcity.fragment.RevisionFragment
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.SharedPrefs


class PDFViewerActivity : BaseActivity() {
    var url: URL? = null
    var uri: Uri? = null
    lateinit var mFile: File
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    override var layoutID: Int = R.layout.activity_pdfviewer

    override fun initView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        sharedPrefs = SharedPrefs()
        backRL.setOnClickListener {
            sound = sharedPrefs?.getBooleanPrefVal(this!!, ConstantPath.SOUNDS) ?: true
            if(!sound) {
                //MusicManager.getInstance().play(context, R.raw.amount_low);
                // Is the sound loaded already?
                if (Utils.loaded) {
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound...volume..." + Utils.volume);
                    //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                }
               // finish()
                val i = Intent(this, DashBoardActivity::class.java)
                i.putExtra("fragment", "pdf")
                startActivity(i)

            }else{
                val i = Intent(this, DashBoardActivity::class.java)
                i.putExtra("fragment", "pdf")
                startActivity(i)
                //finish()
            }
        }
        val path = intent.getStringExtra("rID")
        Log.e("pdf viewer activity","....path....."+path)
        try {

            mFile = File(getExternalFilesDir(null), path+".pdf")
            if (mFile.exists()) {
                pdfView.fromFile(mFile).autoSpacing(false)

                    .spacing(5).load()
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

        shareRL.setOnClickListener {
            sound = sharedPrefs?.getBooleanPrefVal(this!!, ConstantPath.SOUNDS) ?: true
            if(!sound) {
                //MusicManager.getInstance().play(context, R.raw.amount_low);
                // Is the sound loaded already?
                if (Utils.loaded) {
                    Utils.soundPool.play(Utils.soundID, Utils.volume, Utils.volume, 1, 0, 1f);
                    Log.e("Test", "Played sound...volume..." + Utils.volume);
                    //Toast.makeText(context,"end",Toast.LENGTH_SHORT).show()
                }
                sendFile(this)
            }else{
                sendFile(this)
            }

            /*val shareIntent = Intent()
            shareIntent.setAction(Intent.ACTION_SEND)
            shareIntent.setType("application/pdf")
            val uri = FileProvider.getUriForFile(this, "com.yomplex.simple.files", mFile)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(shareIntent, "Share using"))*/


            /*val sharingIntent = Intent(Intent.ACTION_SEND)
            val screenshotUri = Uri.parse(mFile.absolutePath)
            sharingIntent.type = "application/pdf"
            sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
            startActivity(Intent.createChooser(sharingIntent, "Share using"))*/
        }

    }

    fun sendFile(context:Context) {

        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("application/pdf")
       // val dirpath = context.getFilesDir() + File.separator + "directory"
       // val file = File(dirpath + File.separator + "file.txt")
        val uri = FileProvider.getUriForFile(context, "com.yomplex.simple.files", mFile)
        //grant permision for app with package "packegeName", eg. before starting other app via intent
        context.grantUriPermission(
            "com.yomplex.simple",
            uri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        // Workaround for Android bug.
        // grantUriPermission also needed for KITKAT,
        // see https://code.google.com/p/android/issues/detail?id=76683
        /*if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
        {
            val resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList)
            {
                val packageName = resolveInfo.activityInfo.packageName
                context.grantUriPermission(packageName, attachmentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }*/
        if (intent.resolveActivity(context.getPackageManager()) != null)
        {
            context.startActivity(intent)
        }
    }
    /*fun revokeFileReadPermission(context:Context) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
        {
            val dirpath = context.getFilesDir() + File.separator + "directory"
            val file = File(dirpath + File.separator + "file.txt")
            val uri = FileProvider.getUriForFile(context, "com.package.name.fileprovider", file)
            context.revokeUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }*/


}

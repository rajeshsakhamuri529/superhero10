package com.blobcity.activity

import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import android.view.*
import com.blobcity.database.DatabaseHandler
import com.blobcity.fragment.RevisionFragment
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.SharedPrefs
import com.google.firebase.analytics.FirebaseAnalytics


class PDFViewerActivity : BaseActivity() {
    var url: URL? = null
    var uri: Uri? = null
    lateinit var mFile: File
    var sharedPrefs: SharedPrefs? = null
    var sound: Boolean = false
    var databaseHandler: DatabaseHandler?= null
    var path:String=""
    var rid:String =""
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    override var layoutID: Int = R.layout.activity_pdfviewer

    override fun initView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorbottomnav));
        }
        appBarID.elevation = 20F
        sharedPrefs = SharedPrefs()
        databaseHandler = DatabaseHandler(this);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        firebaseAnalytics.setCurrentScreen(this, "BookName", null /* class override */)


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
        path = intent.getStringExtra("filename")
        rid = intent.getStringExtra("rID")
        Log.e("pdf viewer activity","....path....."+path)
        try {

            mFile = File(getExternalFilesDir(null), path+".pdf")
            if (mFile.exists()) {
                pdfView.fromFile(mFile).autoSpacing(false)

                    .spacing(8).load()
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

        var status = databaseHandler!!.getBookReadStatus(rid);
        if(status.equals("1")){
            tick1RL.visibility = View.VISIBLE
        }else{
            tick1RL.visibility = View.INVISIBLE
        }


        frameLL.setOnClickListener {
            menuRL.visibility = View.GONE
            frameLL.visibility = View.GONE
            topRL.visibility = View.GONE
        }

        topRL.setOnClickListener {
            menuRL.visibility = View.GONE
            frameLL.visibility = View.GONE
            topRL.visibility = View.GONE
        }

        moreRL.setOnClickListener {
            menuRL.visibility = View.VISIBLE
            frameLL.visibility = View.VISIBLE
            topRL.visibility = View.VISIBLE
            menuRL.bringToFront()
            Log.e("pdf viewer","rid....."+rid);
            var status = databaseHandler!!.getBookReadStatus(rid);
            Log.e("pdf viewer","status....."+status);
            if(status.equals("0")){
                markread.text ="Mark as Read"
                tick.background = resources.getDrawable(R.drawable.book_read_status)
            }else{
                markread.text ="Mark as Unread"
                tick.background = resources.getDrawable(R.drawable.ic_tick_green_circle1)
            }
        }

        markreadLL.setOnClickListener {
            menuRL.visibility = View.GONE
            frameLL.visibility = View.GONE
            topRL.visibility = View.GONE
            var status = databaseHandler!!.getBookReadStatus(rid);
            Log.e("pdf viewer","status....."+status);
            if(status.equals("0")){
                tick1RL.visibility = View.VISIBLE
                databaseHandler!!.updateBookaReadStatus(rid,"1");
                markread.text ="Mark as Read"
                tick.background = resources.getDrawable(R.drawable.book_read_status)
            }else{
                tick1RL.visibility = View.INVISIBLE
                val bundle = Bundle()
                bundle.putString("Category", "Book")
                bundle.putString("Action", "Mark as Read")
                bundle.putString("Label", path)
                firebaseAnalytics?.logEvent("Book", bundle)
                databaseHandler!!.updateBookaReadStatus(rid,"0");
                markread.text ="Mark as Unread"
                tick.background = resources.getDrawable(R.drawable.ic_tick_green_circle1)
            }


        }

        shareLL.setOnClickListener {
            menuRL.visibility = View.GONE
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

   /* override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = getMenuInflater();
        inflater.inflate(R.menu.books_menu, menu);
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }*/

/*@Override
public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.user_menu, menu);
    return true;
}*/

/*@Override
public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
        case R.id.logout_menu:
            // Do whatever you want to do on logout click.
            return true;
        default:
            return super.onOptionsItemSelected(item);
    }
}*/



}

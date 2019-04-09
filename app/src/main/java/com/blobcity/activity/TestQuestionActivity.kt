package com.blobcity.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.blobcity.R
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils
import kotlinx.android.synthetic.main.activity_test_question.*
import java.util.*

class TestQuestionActivity : AppCompatActivity() {

    var questionPath: String?=""
    var opt1Path: String?=""
    var opt2Path: String?=""
    var opt3Path: String?=""
    var opt4Path: String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_question)

        val path = intent.getStringExtra(DYNAMIC_PATH)

        val listOfString = ArrayList<String>()
        for (filename in Utils.getListOfFilesFromAsset(path, this)){
            if (filename.contains("opt")){
                Log.e("original", filename)
                listOfString.add(filename)
            }
            if (filename.contains("question")){
                questionPath = "file:///android_asset/"+path+"/"+filename
            }
        }

        Collections.shuffle(listOfString)
        opt1Path = "file:///android_asset/"+path+"/"+listOfString.get(0)
        opt2Path = "file:///android_asset/"+path+"/"+listOfString.get(1)
        opt3Path = "file:///android_asset/"+path+"/"+listOfString.get(2)
        opt4Path = "file:///android_asset/"+path+"/"+listOfString.get(3)
        webView_question.setBackgroundColor(0)
        webView_option1.setBackgroundColor(resources.getColor(R.color.purple_opt_bg))
        webView_option2.setBackgroundColor(resources.getColor(R.color.purple_opt_bg))
        webView_option3.setBackgroundColor(resources.getColor(R.color.purple_opt_bg))
        webView_option4.setBackgroundColor(resources.getColor(R.color.purple_opt_bg))

        webView_question.loadUrl(questionPath)
        webView_option1.loadUrl(opt1Path)
        webView_option2.loadUrl(opt2Path)
        webView_option3.loadUrl(opt3Path)
        webView_option4.loadUrl(opt4Path)

    }
}
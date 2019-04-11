package com.blobcity.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.ArrayMap
import android.util.Log
import android.view.View
import com.blobcity.R
import com.blobcity.model.TopicOneBasicResponseModel
import com.blobcity.model.TopicOneQuestionsItem
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_quiz_level.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class QuizLevelActivity : AppCompatActivity(), View.OnClickListener {

    var jsonStringBasic: String? =""
    var jsonStringIntermediate: String? =""
    var jsonStringAdvanced: String? =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.blobcity.R.layout.activity_quiz_level)

        val folderName = intent.getStringExtra(FOLDER_NAME)
        val folderPath = assetTestCoursePath+folderName

        jsonStringBasic = loadJSONFromAsset(this,"$folderPath/basic.json")
        jsonStringIntermediate = loadJSONFromAsset(this,"$folderPath/intermediate.json")
        jsonStringAdvanced = loadJSONFromAsset(this,"$folderPath/advanced.json")

        btn_quiz1.setOnClickListener(this)
        btn_quiz2.setOnClickListener(this)
        btn_quiz3.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_quiz1 ->{
                callIntent(jsonStringBasic!!)
            }

            R.id.btn_quiz2 ->{
                callIntent(jsonStringIntermediate!!)
            }

            R.id.btn_quiz3 ->{
                callIntent(jsonStringAdvanced!!)
            }
        }
    }

    private fun callIntent(path: String){
        val intent = Intent(this, TestQuestionActivity::class.java)
        intent.putExtra(DYNAMIC_PATH, path)
        startActivity(intent)
    }
}
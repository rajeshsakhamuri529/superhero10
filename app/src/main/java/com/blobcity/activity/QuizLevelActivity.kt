package com.blobcity.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.ArrayMap
import android.util.Log
import com.blobcity.model.TopicOneBasicResponseModel
import com.blobcity.model.TopicOneQuestionsItem
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_quiz_level.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class QuizLevelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.blobcity.R.layout.activity_quiz_level)

        val folderName = intent.getStringExtra(FOLDER_NAME)
        val folderPath = assetTestCoursePath+folderName

        val jsonStringBasic = loadJSONFromAsset(this,"$folderPath/basic.json")
        val jsonStringIntermediate = loadJSONFromAsset(this,"$folderPath/intermediate.json")
        val jsonStringAdvanced = loadJSONFromAsset(this,"$folderPath/advanced.json")

        btn_quiz1.setOnClickListener {
            val intent = Intent(this, TestQuestionActivity::class.java)
            intent.putExtra(DYNAMIC_PATH, jsonStringBasic)
            startActivity(intent)
        }

        btn_quiz2.setOnClickListener {
            val intent = Intent(this, TestQuestionActivity::class.java)
            intent.putExtra(DYNAMIC_PATH, jsonStringIntermediate)
            startActivity(intent)
        }

        btn_quiz3.setOnClickListener {
            val intent = Intent(this, TestQuestionActivity::class.java)
            intent.putExtra(DYNAMIC_PATH, jsonStringAdvanced)
            startActivity(intent)
        }

        /*var notificationModelList: MutableList<TopicOneQuestionsItem>?
        val arrayMap = ArrayMap<String, List<TopicOneQuestionsItem>>()

        for (i in 0 until questionsItem.size) {

            val type = notificationModels.get(i).getType()

            if (!arrayMap.containsKey(type)) {
                notificationModelList = ArrayList()
                notificationModelList!!.add(notificationModels.get(i))
                arrayMap[type] = notificationModelList
            } else {
                notificationModelList = arrayMap[type]
                notificationModelList!!.add(notificationModels.get(i))
            }
        }*/
    }


}
package com.blobcity.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.ArrayMap
import com.blobcity.R
import com.blobcity.model.TopicOneBasicResponseModel
import com.blobcity.model.TopicOneQuestionsItem
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_quiz_level.*

class QuizLevelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_level)

        val folderName = intent.getStringExtra(FOLDER_NAME)
        val folderPath = assetTestCoursePath+folderName

        val jsonStringBasic = loadJSONFromAsset(this,"$folderPath/basic.json")
        val jsonStringIntermediate = loadJSONFromAsset(this,"$folderPath/intermediate.json")
        val jsonStringAdvanced = loadJSONFromAsset(this,"$folderPath/advanced.json")

        val gsonFile = Gson()
        val questionResponseModel = gsonFile.fromJson(jsonStringBasic, TopicOneBasicResponseModel::class.java)
        val questionsItem = questionResponseModel.questions
        val arrayMap: ArrayMap<String, List<TopicOneQuestionsItem>> = ArrayMap()
        arrayMap.contains(questionsItem.get(0).bank)

        btn_quiz1.setOnClickListener {
            val intent = Intent(this, TestQuestionActivity::class.java)
            intent.putExtra(DYNAMIC_PATH, assetOutputPath+questionsItem.get(0).id)
            startActivity(intent)
        }
    }
}
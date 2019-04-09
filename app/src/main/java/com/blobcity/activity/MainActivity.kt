package com.blobcity.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.blobcity.R
import com.blobcity.utils.ConstantPath.localBasic394Path
import com.blobcity.utils.ConstantPath.localTopicOnePath
import com.blobcity.utils.Utils.getListOfFilesFromFolder
import com.blobcity.utils.Utils.readFromFile
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listOfString = ArrayList<String>()
        for (filename in getListOfFilesFromFolder(localBasic394Path)){
            if (filename.contains("opt")){
                Log.e("original", filename)
                listOfString.add(filename)
            }
        }

        btn_quiz1.setOnClickListener {
            val intent = Intent(this, TestQuestionActivity::class.java)
            startActivity(intent)
        }
        
        Collections.shuffle(listOfString)
        for (filename in listOfString){
            Log.e("shuffle", filename)
        }

        val jsonStringBasic = readFromFile("$localTopicOnePath/basic.json")
        val jsonStringIntermediate = readFromFile("$localTopicOnePath/intermediate.json")
        val jsonStringAdvanced = readFromFile("$localTopicOnePath/advanced.json")
    }
}
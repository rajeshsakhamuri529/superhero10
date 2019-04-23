package com.blobcity.activity

import android.content.Intent
import android.view.View
import com.blobcity.R
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils.*
import kotlinx.android.synthetic.main.activity_quiz_level.*

class QuizLevelActivity : BaseActivity(), View.OnClickListener {

    var jsonStringBasic: String? =""
    var jsonStringIntermediate: String? =""
    var jsonStringAdvanced: String? =""

    override fun setLayout(): Int {
        return R.layout.activity_quiz_level
    }

    override fun initView() {
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
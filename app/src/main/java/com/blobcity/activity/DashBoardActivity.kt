package com.blobcity.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import com.blobcity.R
import com.blobcity.adapter.ChaptersAdapter
import com.blobcity.model.BranchesItem
import com.blobcity.model.TopicResponseModel
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils.*
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.util.*

class DashBoardActivity : AppCompatActivity(), PermissionListener {

    private var encryptPath = Environment.getExternalStorageDirectory().toString() + "/Encrypt/"
    private var decryptPath = Environment.getExternalStorageDirectory().toString() + "/Decrypt/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        TedPermission.with(this)
            .setPermissionListener(this)
            .setDeniedMessage("If you reject permission,you can not use this service\n"
                    + "\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check()
    }

    override fun onPermissionGranted() {
        storeFileLocally()
    }

    override fun onPermissionDenied(deniedPermissions: List<String>) {

    }

    private fun storeFileLocally() {

        val jsonString = loadJSONFromAsset(this, assetTestCoursePath+"topic.json")
        /*val jsonString = readFromFile("$localTestCoursePath/topic.json")*/
        val gsonFile = Gson()
        val topicResponseModel = gsonFile.fromJson(jsonString, TopicResponseModel::class.java)

        val branchesItemList = topicResponseModel.branches
        val branchesItemList2 = ArrayList<BranchesItem>()
        val index1 = branchesItemList[0].topic.index.toString()
        tv_topic_number1.text = index1
        tv_topic_name1.text = branchesItemList[0].topic.title

        val index2 = branchesItemList[1].topic.index.toString()
        tv_topic_number2.text = index2
        tv_topic_name2.text = branchesItemList[1].topic.title
        branchesItemList.forEachIndexed { index, branchesItem ->
            if (index>1){
                branchesItemList2.add(branchesItem)
            }
        }
        rcv_dashboard.adapter = ChaptersAdapter(this, branchesItemList2)

        rl_chapter_one.setOnClickListener {
            val intent = Intent(this, QuizLevelActivity::class.java)
            intent.putExtra(FOLDER_NAME, branchesItemList.get(0).topic.folderName)
            startActivity(intent
            )
        }
    }
}
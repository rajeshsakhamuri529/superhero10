package com.blobcity.activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.blobcity.R
import com.blobcity.adapter.ChaptersAdapter
import com.blobcity.interfaces.TopicClickListener
import com.blobcity.model.BranchesItem
import com.blobcity.model.TopicResponseModel
import com.blobcity.utils.ConstantPath.FOLDER_NAME
import com.blobcity.utils.ConstantPath.assetTestCoursePath
import com.blobcity.utils.Utils.loadJSONFromAsset
import com.google.gson.Gson
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashBoardActivity : AppCompatActivity(), PermissionListener,
    View.OnClickListener, TopicClickListener {

    private var branchesItemList:List<BranchesItem>?=null

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
        readFileLocally()
    }

    override fun onPermissionDenied(deniedPermissions: List<String>) {

    }

    private fun readFileLocally() {

        val jsonString = loadJSONFromAsset(this, assetTestCoursePath+"topic.json")
        /*val jsonString = readFromFile("$localTestCoursePath/topic.json")*/
        val gsonFile = Gson()
        val topicResponseModel = gsonFile.fromJson(jsonString, TopicResponseModel::class.java)

        branchesItemList = topicResponseModel.branches
        val branchesItemList2 = ArrayList<BranchesItem>()
        val index1 = branchesItemList!![0].topic.index.toString()
        tv_topic_number1.text = index1
        tv_topic_name1.text = branchesItemList!![0].topic.title

        val index2 = branchesItemList!![1].topic.index.toString()
        tv_topic_number2.text = index2
        tv_topic_name2.text = branchesItemList!![1].topic.title
        branchesItemList!!.forEachIndexed { index, branchesItem ->
            if (index>1){
                branchesItemList2.add(branchesItem)
            }
        }
        rcv_dashboard.adapter = ChaptersAdapter(this, branchesItemList2, this)

        rl_chapter_one.setOnClickListener(this)
        rl_chapter_two.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.rl_chapter_one ->{
                callIntent(branchesItemList!!.get(0).topic.folderName)
            }

            R.id.rl_chapter_two ->{
                callIntent(branchesItemList!!.get(1).topic.folderName)
            }
        }
    }

    private fun callIntent(path: String){
        val intent = Intent(this, QuizLevelActivity::class.java)
        intent.putExtra(FOLDER_NAME, path)
        startActivity(intent)
    }

    override fun onClick(path: String) {
        callIntent(path)
    }
}
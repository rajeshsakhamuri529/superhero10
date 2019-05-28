package com.blobcity.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.R
import com.blobcity.activity.DashBoardActivity
import com.blobcity.activity.QuizLevelActivity
import com.blobcity.adapter.ChaptersAdapter
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.interfaces.TopicClickListener
import com.blobcity.model.BranchesItem
import com.blobcity.model.CoursesResponseModel
import com.blobcity.model.Topic
import com.blobcity.model.TopicResponseModel
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.ConstantPath.assetOutputPath
import com.blobcity.utils.ConstantPath.assetTestCoursePath
import com.blobcity.viewmodel.TopicStatusVM
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import kotlinx.android.synthetic.main.chapter_layout.*

class ChapterFragment: Fragment(), TopicClickListener {

    private var branchesItemList:List<BranchesItem>?=null
    var courseId: String?=""
    var courseName: String?=""
    var databaseRefrence: DatabaseReference?= null
    var topicStatusModelList: ArrayList<TopicStatusEntity>?=null
    var adapter: ChaptersAdapter?= null
    var topicStatusVM: TopicStatusVM?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chapter_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        topicStatusVM!!.getAllTopicStatus().observe(this,
            object : Observer<List<TopicStatusEntity>> {
                override fun onChanged(t: List<TopicStatusEntity>?) {
                    topicStatusModelList = ArrayList()
                    topicStatusModelList!!.addAll(t!!)
                    for (item in topicStatusModelList!!){
                        if (topicStatusModelList != null){
                            if (topicStatusModelList!!.size > 0){
                                for (branchItem in branchesItemList!!) {
                                    val branchId = branchItem.id
                                    branchItem.basic = 0
                                    branchItem.intermediate = 0
                                    branchItem.advance = 0
                                    for (topicStatusModels in topicStatusModelList!!) {
                                        val id = topicStatusModels.topicId
                                        val level = topicStatusModels.topicLevel

                                        if (id!!.contains(branchId)) {
                                            if (level!!.contains("basic")) {
                                                branchItem.basic = 1
                                            }
                                            if (level.contains("intermediate")) {
                                                branchItem.intermediate = 1
                                            }
                                            if (level.contains("advance")) {
                                                branchItem.advance = 1
                                            }
                                        }

                                    }
                                }
                            }
                        }
                        adapter!!.notifyDataSetChanged()
                    }
                }

            })
        readFileLocally()
    }

    private fun readFileLocally() {
        val courseJsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( assetOutputPath + "Courses.json")
        val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( assetTestCoursePath + "topic.json")
        /*val jsonString = readFromFile("$localTestCoursePath/topic.json")*/
        val gsonFile = Gson()
        val topicResponseModel = gsonFile.fromJson(jsonString, TopicResponseModel::class.java)
        val courseResponseModel = gsonFile.fromJson(courseJsonString, CoursesResponseModel::class.java)
        courseId = courseResponseModel.courses[0].id
        courseName = courseResponseModel.courses[0].syllabus.title
        branchesItemList = topicResponseModel.branches

        /*val branchesItemList2 = ArrayList<BranchesItem>()
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
        }*/

        adapter = ChaptersAdapter(context!!, branchesItemList!!, this)

        rcv_chapter.adapter = adapter
        /*rl_chapter_one.setOnClickListener(this)
        rl_chapter_two.setOnClickListener(this)*/
    }

    private fun callIntent(topic: Topic, topicId: String, position: Int){
        val intent = Intent(context!!, QuizLevelActivity::class.java)
        intent.putExtra(ConstantPath.TOPIC, topic)
        intent.putExtra(ConstantPath.COURSE_ID, courseId)
        intent.putExtra(ConstantPath.COURSE_NAME, courseName)
        intent.putExtra(ConstantPath.TOPIC_ID, topicId)
        intent.putExtra(ConstantPath.TOPIC_POSITION, position)
        startActivity(intent)
    }

    override fun onClick(topic: Topic, topicId: String, position: Int) {
        callIntent(topic, topicId, position)
    }
}
package com.blobcity.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
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
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils.readFromFile
import com.blobcity.viewmodel.TopicStatusVM
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.chapter_layout.*
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import com.blobcity.utils.DividerItem
import com.blobcity.utils.DividerItemDecorations


class ChapterFragment: Fragment(), TopicClickListener {

    private var branchesItemList:List<BranchesItem>?=null
    var courseId: String?=""
    var courseName: String?=""
    var topicStatusModelList: ArrayList<TopicStatusEntity>?=null
    var adapter: ChaptersAdapter?= null
    var topicStatusVM: TopicStatusVM?= null
    var localPath: String?= null
    var gradeTitle: String?= null
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.chapter_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gradeTitle = arguments!!.getString(TITLE_TOPIC)!!
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        readFileLocally()
        /*val itemDecorator = DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.rv_divider)!!)*/

        //val itemDecorator = DividerItemDecorations(context!!.getDrawable(R.drawable.rv_divider), false,false)
        //itemDecorator.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.rv_divider)!!)

       val itemDecorator = DividerItem(ContextCompat.getDrawable(context!!, R.drawable.rv_divider))
        //itemDecorator.setDrawable(ContextCompat.getDrawable(activity!!, R.drawable.rv_divider)!!)

        topicStatusVM!!.getAllTopicStatus(gradeTitle!!).observe(this,
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
                    }
                    adapter = ChaptersAdapter(context!!, branchesItemList!!, this@ChapterFragment)

                    rcv_chapter.addItemDecoration(itemDecorator)
                    //rcv_chapter.addItemDecoration(DividerItemDecoration(context,))
                    rcv_chapter.adapter = adapter
                }

            })
        rcv_chapter.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)
                val totalItemCount = layoutManager.itemCount
                val lastVisible = layoutManager.findLastVisibleItemPosition()
                Log.d("lastVisible",lastVisible.toString()+"!")
                adapter!!.lastItem(
                    (rcv_chapter
                        .findViewHolderForAdapterPosition(lastVisible)
                            as ChaptersAdapter.ChaptersViewHolder)
                )
            }
        })

    }

    private fun readFileLocally() {
        val courseJsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( localBlobcityPath + "Courses.json")
        //val courseJsonString = readFromFile("$localBlobcityPath/Courses.json")
        Log.d("courseJsonString",courseJsonString+"!");
        /*val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( assetTestCoursePath + "topic.json")*/
        val gsonFile = Gson()
        val courseType = object : TypeToken<List<CoursesResponseModel>>() {}.type
        val courseResponseModel: ArrayList<CoursesResponseModel> = gsonFile
            .fromJson(courseJsonString, courseType)
        courseId = courseResponseModel[0].id
        courseName = courseResponseModel[0].syllabus.title
        localPath = "$localBlobcityPath$courseName/"
       // val jsonString = readFromFile(localPath +"topic.json")
        val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( localPath + "topic.json")
        Log.d("jsonString",jsonString);
        val topicType = object : TypeToken<TopicResponseModel>() {}.type
        val topicResponseModel: TopicResponseModel= gsonFile.fromJson(jsonString, topicType )

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


        /*rl_chapter_one.setOnClickListener(this)
        rl_chapter_two.setOnClickListener(this)*/
    }

    private fun callIntent(topic: Topic, topicId: String, position: Int){
        mediaPlayer = MediaPlayer.create(activity,R.raw.amount_low)
        mediaPlayer.start()
        val intent = Intent(context!!, QuizLevelActivity::class.java)
        intent.putExtra(TOPIC, topic)
        intent.putExtra(COURSE_ID, courseId)
        intent.putExtra(COURSE_NAME, courseName)
        intent.putExtra(TOPIC_ID, topicId)
        intent.putExtra(TOPIC_POSITION, position)
        intent.putExtra(FOLDER_PATH, localPath)
        intent.putExtra(TITLE_TOPIC, gradeTitle!!)
        startActivity(intent)
    }

    override fun onClick(topic: Topic, topicId: String, position: Int) {
        callIntent(topic, topicId, position)
    }


}
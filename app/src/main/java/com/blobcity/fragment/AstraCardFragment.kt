package com.blobcity.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.R
import com.blobcity.adapter.AstraCardAdapter
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.model.BranchesItem
import com.blobcity.model.CoursesResponseModel
import com.blobcity.model.TopicResponseModel
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.EqualSpacingItemDecoration
import com.blobcity.utils.Utils
import com.blobcity.viewmodel.TopicStatusVM
import com.google.gson.Gson
import kotlinx.android.synthetic.main.astra_card_layout.*

class AstraCardFragment : Fragment() {

    var topicStatusVM: TopicStatusVM?= null
    private var branchesItemList:List<BranchesItem>?=null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.astra_card_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gridLayoutManager = GridLayoutManager(context!!, 3)
        rcv_astra_card.layoutManager = gridLayoutManager
        val spacingPixel = resources.getDimensionPixelSize(R.dimen._5sdp)
        /*rcv_astra_card.addItemDecoration(EqualSpacingItemDecoration(25, EqualSpacingItemDecoration.GRID))*/
        val courseJsonString = Utils.loadJSONFromAsset(context!!, ConstantPath.assetOutputPath + "Courses.json")
        val jsonString = Utils.loadJSONFromAsset(context!!, ConstantPath.assetTestCoursePath + "topic.json")
        /*val jsonString = readFromFile("$localTestCoursePath/topic.json")*/
        val gsonFile = Gson()
        val topicResponseModel = gsonFile.fromJson(jsonString, TopicResponseModel::class.java)
        val courseResponseModel = gsonFile.fromJson(courseJsonString, CoursesResponseModel::class.java)
        branchesItemList = topicResponseModel.branches
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        topicStatusVM!!.getTopicsByLevel("advanced").observe(this,
            object : Observer<List<TopicStatusEntity>>{
            override fun onChanged(topicStatusEntityList: List<TopicStatusEntity>?) {
                val adapter = AstraCardAdapter(topicStatusEntityList!!, branchesItemList!!, context!!)
                rcv_astra_card.adapter = adapter
                val size = topicStatusEntityList.size
                val total = branchesItemList!!.size
                tv_cards_count.text = "$size of $total unlocked"
            }
        })
    }
}
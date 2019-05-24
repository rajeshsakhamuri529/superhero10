package com.blobcity.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.activity.DashBoardActivity
import com.blobcity.adapter.MyPagerAdapter
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.model.BranchesItem
import com.blobcity.model.TopicResponseModel
import com.blobcity.utils.CarouselEffectTransformer
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.InfinitePagerAdapter
import com.blobcity.utils.Utils
import com.blobcity.viewmodel.TopicStatusVM
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_card_review.*
import kotlinx.android.synthetic.main.activity_dashboard.*

class CardReviewFragment : Fragment() {
    private var branchesItemList:List<BranchesItem>?=null
    var count : Int?= null
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(com.blobcity.R.layout.activity_card_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashBoardActivity).navigation.visibility = View.GONE
        vp_card_review.clipChildren = false
        vp_card_review.pageMargin = resources.getDimensionPixelOffset(com.blobcity.R.dimen.pager_margin)
        vp_card_review.offscreenPageLimit = 3
        vp_card_review.setPageTransformer(false, CarouselEffectTransformer(activity!!))
        val jsonString = Utils.loadJSONFromAsset(activity!!, ConstantPath.assetTestCoursePath + "topic.json")
        /*val jsonString = readFromFile("$localTestCoursePath/topic.json")*/
        val gsonFile = Gson()
        val topicResponseModel = gsonFile.fromJson(jsonString, TopicResponseModel::class.java)
        branchesItemList = topicResponseModel.branches
        val listOfImages: ArrayList<Int> = ArrayList()
        for(i in 0..(branchesItemList!!.size -2)){
            listOfImages.add(com.blobcity.R.drawable.purple_card)
        }
        listOfImages.add((branchesItemList!!.size -1), com.blobcity.R.drawable.pink_card)
        val topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        topicStatusVM.getTopicsByLevel("advanced").observe(this,
            object : Observer<List<TopicStatusEntity>> {
                override fun onChanged(topicStatusEntityList: List<TopicStatusEntity>?) {

                    branchesItemList!!.forEachIndexed { index, i ->
                        for (item in topicStatusEntityList!!){
                            if (item.topicPosition == index){
                                listOfImages.set(index, com.blobcity.R.drawable.golden_card)
                            }
                        } }

                    val pagerAdapter = InfinitePagerAdapter(MyPagerAdapter(activity!!, listOfImages))

                    vp_card_review.adapter = pagerAdapter
                    val intentCount = activity!!.intent.getIntExtra("pos", 0)

                    val posi = intentCount + 1
                    val total = branchesItemList!!.size
                    vp_card_review.setCurrentItem(intentCount)
                    count = posi - 1
                    val tv_count = "$posi of $total"
                    tv_topic_count.text = tv_count
                    Log.e("intent", posi.toString())

                    vp_card_review.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                        override fun onPageScrollStateChanged(p0: Int) {

                        }

                        override fun onPageScrolled(position: Int, p1: Float, p2: Int) {
                            /*Log.e("scroll", position.toString())*/

                        }

                        override fun onPageSelected(position: Int) {
                            count = vp_card_review.currentItem + 1
                            tv_topic_count.text = "$count of $total"
                        }

                    })
                }
            })
    }
}
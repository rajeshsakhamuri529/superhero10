package com.blobcity.activity

import android.app.Activity
import android.app.SharedElementCallback
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v4.view.ViewPager
import android.transition.TransitionInflater
import android.view.View
import android.widget.LinearLayout
import com.blobcity.R
import com.blobcity.adapter.InfinitePagerAdapter
import com.blobcity.adapter.MyPagerAdapter
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.model.BranchesItem
import com.blobcity.model.CoursesResponseModel
import com.blobcity.model.ImageModel
import com.blobcity.model.TopicResponseModel
import com.blobcity.utils.CarouselEffectTransformer
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils
import com.blobcity.viewmodel.TopicStatusVM
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_card_review.*

class CardReviewActivity : BaseActivity() {

    private var branchesItemList:List<BranchesItem>?=null
    var count : Int?= null
    var sharedCount : Int ?= null
    var intentCount : Int?= null
    var topicStatusVM: TopicStatusVM?= null
    var gradeTitle: String?= null

    override fun setLayout(): Int {
        return R.layout.activity_card_review
    }

    override fun initView() {
        iv_dismiss.setOnClickListener({
            onBackPressed()
        })
        topicStatusVM = ViewModelProviders.of(this)
            .get(TopicStatusVM::class.java)

        gradeTitle = intent.getStringExtra(TITLE_TOPIC)
        vp_card_review.clipChildren = false
        vp_card_review.pageMargin = resources
            .getDimensionPixelOffset(R.dimen.pager_margin)
        vp_card_review.offscreenPageLimit = 3
        vp_card_review.setPageTransformer(false,
            CarouselEffectTransformer(this))

        val courseJsonString = Utils.readFromFile(
            "${localBlobcityPath}/Courses.json")
        /*val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( assetTestCoursePath + "topic.json")*/
        val gsonFile = Gson()
        val courseType = object : TypeToken<List<CoursesResponseModel>>() {}.type
        val courseResponseModel: ArrayList<CoursesResponseModel> = gsonFile
            .fromJson(courseJsonString, courseType)
        val courseName = courseResponseModel[0].syllabus.title
        val jsonString = Utils.readFromFile("${localBlobcityPath}/$courseName/topic.json")
        /*val jsonString = readFromFile("$localTestCoursePath/topic.json")*/
        val topicResponseModel = gsonFile
            .fromJson(jsonString, TopicResponseModel::class.java)

        branchesItemList = topicResponseModel.branches
        loadDataFromViewModel()
        prepareSharedElementTransition()
    }

    private fun loadDataFromViewModel(){
        val pathStringList: ArrayList<String> = ArrayList()
        for (imagePath in Utils
            .getListOfFilesFromFolder(loaclAstraCardPath)){
            if (imagePath.contains("png")){
                pathStringList.add(imagePath)
            }
        }
        val listOfImages: ArrayList<ImageModel> = ArrayList()
        for(i in 0..(branchesItemList!!.size -2)){
            val imageModel = ImageModel()
            imageModel.imageDrawable = R.drawable.purple_card
            imageModel.imagePath = ""
            listOfImages.add(imageModel)
        }
        val imageModel = ImageModel()
        imageModel.imageDrawable = R.drawable.pink_card
        imageModel.imagePath = ""
        listOfImages.add((branchesItemList!!.size -1), imageModel)
        topicStatusVM!!.getTopicsByLevel("advanced", gradeTitle!!).observe(this,
            object : Observer<List<TopicStatusEntity>> {
                override fun onChanged(topicStatusEntityList: List<TopicStatusEntity>?) {
                    branchesItemList!!.forEachIndexed { index, i ->
                        for (item in topicStatusEntityList!!){
                            if (item.topicPosition == index){
                                var imagepath :String
                                for (path in pathStringList){
                                    when(path){
                                        "1.png"->{
                                            imagepath = loaclAstraCardPath +path
                                            val imageModel1 = ImageModel()
                                            imageModel1.imageDrawable = 0
                                            imageModel1.imagePath = imagepath
                                            listOfImages.set(index, imageModel1)
                                        }
                                        "2.png"->{
                                            imagepath = loaclAstraCardPath +path
                                            val imageModel1 = ImageModel()
                                            imageModel1.imageDrawable = 0
                                            imageModel1.imagePath = imagepath
                                            listOfImages.set(index, imageModel1)
                                        }
                                        "3.png"->{
                                            imagepath = loaclAstraCardPath +path
                                            val imageModel1 = ImageModel()
                                            imageModel1.imageDrawable = 0
                                            imageModel1.imagePath = imagepath
                                            listOfImages.set(index, imageModel1)
                                        }
                                        "4.png"->{
                                            imagepath = loaclAstraCardPath +path
                                            val imageModel1 = ImageModel()
                                            imageModel1.imageDrawable = 0
                                            imageModel1.imagePath = imagepath
                                            listOfImages.set(index, imageModel1)
                                        }
                                        "5.png"->{
                                            imagepath = loaclAstraCardPath +path
                                            val imageModel1 = ImageModel()
                                            imageModel1.imageDrawable = 0
                                            imageModel1.imagePath = imagepath
                                            listOfImages.set(index, imageModel1)
                                        }
                                        "6.png"->{
                                            imagepath = loaclAstraCardPath +path
                                            val imageModel1 = ImageModel()
                                            imageModel1.imageDrawable = 0
                                            imageModel1.imagePath = imagepath
                                            listOfImages.set(index, imageModel1)
                                        }
                                        "7.png"->{
                                            imagepath = loaclAstraCardPath +path
                                            val imageModel1 = ImageModel()
                                            imageModel1.imageDrawable = 0
                                            imageModel1.imagePath = imagepath
                                            listOfImages.set(index, imageModel1)
                                        }
                                        "8.png"->{
                                            imagepath = loaclAstraCardPath +path
                                            val imageModel1 = ImageModel()
                                            imageModel1.imageDrawable = 0
                                            imageModel1.imagePath = imagepath
                                            listOfImages.set(index, imageModel1)
                                        }
                                        "9.png"->{
                                            imagepath = loaclAstraCardPath +path
                                            val imageModel1 = ImageModel()
                                            imageModel1.imageDrawable = 0
                                            imageModel1.imagePath = imagepath
                                            listOfImages.set(index, imageModel1)
                                        }
                                        "10.png"->{
                                            imagepath = loaclAstraCardPath +path
                                            val imageModel1 = ImageModel()
                                            imageModel1.imageDrawable = 0
                                            imageModel1.imagePath = imagepath
                                            listOfImages.set(index, imageModel1)
                                        }
                                    }
                                }
                            }
                        } }

                    val pagerAdapter = InfinitePagerAdapter(MyPagerAdapter(
                        this@CardReviewActivity, listOfImages))

                    vp_card_review.adapter = pagerAdapter
                    intentCount = intent.getIntExtra("pos", 0)
                    /*sharedCount = intentCount*/
                    val posi = intentCount!! + 1
                    val total = branchesItemList!!.size
                    vp_card_review.setCurrentItem(intentCount!!)
                    count = posi - 1
                    val tv_count = "$posi of $total"
                    tv_topic_count.text = tv_count

                    vp_card_review.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                        override fun onPageScrollStateChanged(position: Int) {
                        }

                        override fun onPageScrolled(position: Int, p1: Float, p2: Int) {
                        }

                        override fun onPageSelected(position: Int) {
                            sharedCount = vp_card_review.currentItem
                            count = vp_card_review.currentItem + 1
                            tv_topic_count.text = "$count of $total"
                            topicStatusEntityList!!.forEachIndexed { index, topicStatus ->
                                if (topicStatus.topicPosition == sharedCount) {
                                    ll_save_share.visibility = View.VISIBLE
                                    return
                                }
                                if (topicStatus.topicPosition != sharedCount) {
                                    ll_save_share.visibility = View.INVISIBLE
                                }
                            }
                        }
                    })
                    for(topicStatus in topicStatusEntityList!!) {
                        if (topicStatus.topicPosition == intentCount) {
                            ll_save_share.visibility = View.VISIBLE
                            return
                        }
                    }
                }
            })
    }

    override fun finishAfterTransition() {
        val data = Intent()
        if (sharedCount != null) {
            data.putExtra("currentPosition", sharedCount!!)
        }else{
            data.putExtra("currentPosition", intentCount)
        }
        setResult(Activity.RESULT_OK, data)
        super.finishAfterTransition()
    }

    fun prepareSharedElementTransition(){
        val transition = TransitionInflater.from(this)
            .inflateTransition(R.transition.enter_transition)
        window.sharedElementEnterTransition = transition
        setEnterSharedElementCallback(object : SharedElementCallback(){
            override fun onMapSharedElements(names: MutableList<String>?,
                                             sharedElements: MutableMap<String, View>?) {
                val view :View
                if (sharedCount != null) {
                    view = vp_card_review.findViewWithTag<LinearLayout>(sharedCount)
                }
                else {
                    view = vp_card_review.findViewWithTag<LinearLayout>(intentCount)
                }
                if (view != null) {
                    sharedElements!!.put(names!!.get(0), view.findViewById(R.id.iv_card_review))
                }
            }
        })
    }
}
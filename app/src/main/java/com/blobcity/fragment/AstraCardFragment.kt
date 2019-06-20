package com.blobcity.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearSmoothScroller
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import com.blobcity.R
import com.blobcity.activity.CardReviewActivity
import com.blobcity.activity.DashBoardActivity
import com.blobcity.adapter.AstraCardAdapter
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.interfaces.AstraCardClickListener
import com.blobcity.model.BranchesItem
import com.blobcity.model.CoursesResponseModel
import com.blobcity.model.TopicResponseModel
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.ConstantPath.TITLE_TOPIC
import com.blobcity.utils.ConstantPath.localTestCoursePath
import com.blobcity.utils.Utils
import com.blobcity.utils.Utils.readFromFile
import com.blobcity.viewmodel.TopicStatusVM
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.astra_card_layout.*

class AstraCardFragment : Fragment(), AstraCardClickListener {

    var topicStatusVM: TopicStatusVM?= null
    private var branchesItemList:List<BranchesItem>?=null
    var gridLayoutManager: GridLayoutManager ?= null
    private var mTmpReenterState: Bundle? = null
    var gradeTitle: String?= null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private val mCallback = object : android.app.SharedElementCallback() {
        override fun onMapSharedElements(names: MutableList<String>, sharedElements: MutableMap<String, View>) {
            if (mTmpReenterState != null) {
                val currentPosition = mTmpReenterState!!.getInt("currentPosition")
                //if (startingPosition != currentPosition) {

                    var newTransitionName = ""
                    var newSharedElement: View? = null

                val viewHolder : AstraCardAdapter.AstraCardViewHolder = rcv_astra_card
                    .findViewHolderForAdapterPosition(currentPosition)
                        as AstraCardAdapter.AstraCardViewHolder
                newTransitionName = "${viewHolder.iv_astra_card.transitionName}"

                //newSharedElement = rvAssets.findViewWithTag<LinearLayout>(newTransitionName)
                newSharedElement = viewHolder.iv_astra_card

                    if (newSharedElement != null) {
                        names.clear()
                        names.add(newTransitionName)
                        sharedElements.clear()
                        sharedElements[newTransitionName] = newSharedElement
                    }
                //}
                mTmpReenterState = null
            }
        }
    }

    fun setExtCallBack(){
        activity!!.setExitSharedElementCallback(mCallback)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.blobcity.R.layout.astra_card_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setExtCallBack()
        gridLayoutManager = GridLayoutManager(context!!, 3)
        rcv_astra_card.layoutManager = gridLayoutManager
        gradeTitle = arguments!!.getString(TITLE_TOPIC)!!

        /*val jsonString = (activity!! as DashBoardActivity)
            .loadJSONFromAsset( ConstantPath.assetTestCoursePath + "topic.json")*/
        /*val courseJsonString = (activity!! as DashBoardActivity).loadJSONFromAsset("${ConstantPath.localBlobcityPath}/Courses.json")*/
        val courseJsonString = (activity!! as DashBoardActivity).loadJSONFromAsset(ConstantPath.localBlobcityPath+"Courses.json")
        Log.d("AstraCards",courseJsonString+"!")
        /*val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( assetTestCoursePath + "topic.json")*/
        val gsonFile = Gson()
        val courseType = object : TypeToken<List<CoursesResponseModel>>() {}.type
        val courseResponseModel: ArrayList<CoursesResponseModel> = gsonFile
            .fromJson(courseJsonString, courseType)
        val courseName = courseResponseModel[0].syllabus.title
        val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset("${ConstantPath.localBlobcityPath}$courseName/topic.json")
        Log.d("AstraCards jsonString",jsonString+"!")
        val topicResponseModel = gsonFile.fromJson(jsonString, TopicResponseModel::class.java)
        branchesItemList = topicResponseModel.branches
        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        topicStatusVM!!.getTopicsByLevel("advanced", gradeTitle!!).observe(this,
            object : Observer<List<TopicStatusEntity>>{
            override fun onChanged(topicStatusEntityList: List<TopicStatusEntity>?) {
                Log.d("onChanged","true")
                val pathStringList: ArrayList<String> = ArrayList()
                for (imagePath in Utils.listAssetFiles(ConstantPath.loaclAstraCardPath,context)){
                    Log.d("AstraCards Frag",imagePath+"!")
                    if (imagePath.contains("png")){
                        pathStringList.add(imagePath)
                        Log.d("imagePath astra frag",imagePath)
                    }
                }
                val adapter = AstraCardAdapter(topicStatusEntityList!!, branchesItemList!!,
                    activity!!, pathStringList, this@AstraCardFragment)
                rcv_astra_card.adapter = adapter
                val size = topicStatusEntityList.size
                val total = branchesItemList!!.size
                tv_cards_count.text = "$size of $total unlocked"
            }
        })
    }

    override fun onClick(imageView: ImageView, position: Int) {
        exitTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.exit_transition)
        val intent = Intent(context, CardReviewActivity::class.java)
        val imageViewPair = Pair.create<View, String>(imageView, "ImageTransition")
        val options = ActivityOptionsCompat
            .makeSceneTransitionAnimation(activity!!, imageViewPair)
        intent.putExtra("pos", position)
        intent.putExtra(TITLE_TOPIC, gradeTitle)
        startActivity(intent,options.toBundle())
    }

    fun activityReenter( data: Intent){

        mTmpReenterState = Bundle(data.extras)
        /*scrollToPosition(mTmpReenterState!!.getInt("currentPosition"))*/
        val currentPos = mTmpReenterState!!.getInt("currentPosition")
        val smoothScroller = object : LinearSmoothScroller(activity!!){
            override fun getVerticalSnapPreference(): Int {
                return LinearSmoothScroller.SNAP_TO_START
            }
        }
        smoothScroller.targetPosition = currentPos
        gridLayoutManager!!.startSmoothScroll(smoothScroller)

        rcv_astra_card.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener{
            override fun onPreDraw(): Boolean {
                rcv_astra_card.getViewTreeObserver().removeOnPreDrawListener(this)
                rcv_astra_card.requestLayout()
                activity!!.startPostponedEnterTransition()
                return true
            }
        })
    }
}
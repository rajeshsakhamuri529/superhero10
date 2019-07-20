package com.blobcity.activity

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import com.blobcity.R
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.model.BranchesItem
import com.blobcity.model.CoursesResponseModel
import com.blobcity.model.ReviewModel
import com.blobcity.model.TopicResponseModel
import com.blobcity.utils.ConstantPath
import com.blobcity.utils.ConstantPath.*
import com.blobcity.utils.Utils.*
import com.blobcity.viewmodel.TopicStatusVM
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_quiz_level.*
import kotlinx.android.synthetic.main.activity_quiz_summary.*

class QuizSummaryActivity : BaseActivity(), View.OnClickListener {

    var reviewModelList: ArrayList<ReviewModel>? = null
    var topicLevel: String? = ""
    var topicName: String? = ""
    var topicId: String? = ""
    private var totalQuestion: Int? = null
    var topicStatusVM: TopicStatusVM? = null
    var topicStatusModelList: ArrayList<TopicStatusEntity>? = null
    var topicStatusModelList2: ArrayList<TopicStatusEntity>? = null

    var isBasicCompleted: Boolean = false
    var isIntermediateCompleted: Boolean = false
    var isAdvancedCompleted: Boolean = false
    private var mSetRightOut: AnimatorSet? = null
    private var mSetLeftIn: AnimatorSet? = null
    private var zoomOutAnimation: Animation? = null
    private var zoominAnimation: Animation? = null
    val handler = Handler()
    var complete: String? = null
    var position: Int? = null
    var dPath: String? = null
    var paths: String? = null
    var courseId: String? = null
    var courseName: String? = null
    var folderName: String? = null
    var level_status: Boolean? = null
    var gradeTitle: String? = null
    private var branchesItemList: List<BranchesItem>? = null
    var isLastTopicAvailable = false


    override var layoutID: Int = R.layout.activity_quiz_summary

    override fun initView() {
        reviewModelList = intent.getSerializableExtra(REVIEW_MODEL) as ArrayList<ReviewModel>?
        topicLevel = intent.getStringExtra(TOPIC_LEVEL)
        topicName = intent.getStringExtra(TOPIC_NAME)
        topicId = intent.getStringExtra(TOPIC_ID)
        totalQuestion = intent.getIntExtra(QUIZ_COUNT, 0)
        complete = intent.getStringExtra(LEVEL_COMPLETED)
        position = intent.getIntExtra(TOPIC_POSITION, -1)
        dPath = intent.getStringExtra(DYNAMIC_PATH)
        paths = intent.getStringExtra(FOLDER_PATH)
        courseId = intent.getStringExtra(COURSE_ID)
        courseName = intent.getStringExtra(COURSE_NAME)
        folderName = intent.getStringExtra(FOLDER_NAME)
        gradeTitle = intent.getStringExtra(TITLE_TOPIC)
        level_status = intent.getBooleanExtra(IS_LEVEL_COMPLETE, false)
        tv_chapter_title.text = topicName
        changeCameraDistance()
        loadAnimations()
        if (topicLevel!!.contains("basic")) {
            tv_quiz_level.text = "Quiz I"
        }
        if (topicLevel!!.contains("intermediate")) {
            tv_quiz_level.text = "Quiz II"
        }
        if (topicLevel!!.contains("advanced")) {
            iv_card_back.visibility = View.VISIBLE
            iv_card_front.visibility = View.VISIBLE
            tv_quiz_level.text = "Astra Quiz"
        }
        val size = reviewModelList!!.size
        val answer_status = "$size / $totalQuestion"
        tv_answer_status.text = answer_status
        if (level_status!!) {
            iv_animation_summary.setAnimation("winAnimation.json")
            tv_completion_status.text = "Level Completed"
            tv_completion_status.setTextColor(resources.getColor(R.color.green_right_answer))
        } else {
            iv_animation_summary.setAnimation("loseAnimation.json")
            tv_completion_status.text = "Level Failed"
            tv_completion_status.setTextColor(resources.getColor(R.color.orange_level_failed))
        }

        btn_review.setOnClickListener(this)
        iv_cancel_quiz_summary.setOnClickListener(this)
        btn_play_again.setOnClickListener(this)
        btn_next_quiz.setOnClickListener(this)
        btn_next_level.setOnClickListener(this)

        topicStatusVM = ViewModelProviders.of(this).get(TopicStatusVM::class.java)
        readFileLocally()

        loadDataFromDb()
        Log.d("summary1",position.toString()+"!"+isAdvancedCompleted.toString()+"!"+isLastTopicAvailable.toString())
        if (position == 8 && isAdvancedCompleted) {
            Log.d("summary",position.toString()+"!"+isAdvancedCompleted.toString()+"!"+isLastTopicAvailable.toString())
            if (!isLastTopicAvailable) {
                btn_next_level.setBackgroundResource(R.drawable.quiz_button_inactive)
                btn_next_level.isEnabled = false
            }
        }
        //btn_quiz3.setBackgroundResource(R.drawable.button_bg)

    }

    override fun onClick(v: View?) {
        val intent: Intent
        when (v!!.id) {

            R.id.btn_next_level -> {
                if(position == 8 && !isLastTopicAvailable){
                    lastTopicDialog()
                }else{
                    intent = Intent(this, QuizLevelActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    intent.putExtra(TOPIC_POSITION, (position!! + 1))
                    intent.putExtra(COURSE_ID, courseId)
                    intent.putExtra(COURSE_NAME, courseName)
                    intent.putExtra(FOLDER_PATH, paths)
                    intent.putExtra(TITLE_TOPIC, gradeTitle!!)
                    finish()
                    startActivity(intent)
                }

            }

            R.id.iv_cancel_quiz_summary -> {
                finish()
            }

            R.id.btn_review -> {
                intent = Intent(this, ReviewActivity::class.java)
                intent.putExtra(ConstantPath.REVIEW_MODEL, reviewModelList)
                startActivity(intent)
            }

            R.id.btn_next_quiz -> {
                topicLevel = btn_next_quiz.text.toString()
                val folderPath = paths + folderName

                if (level_status!!) {
                    complete = ""
                }

                if (topicLevel!!.equals("Quiz I")) {
                    topicLevel = "basic"
                    dPath = loadJSONFromAsset("$folderPath/basic.json")
                }
                if (topicLevel!!.equals("Quiz II")) {
                    topicLevel = "intermediate"
                    dPath = loadJSONFromAsset("$folderPath/intermediate.json")
                }
                if (topicLevel!!.equals("Astra Quiz")) {
                    topicLevel = "advanced"
                    dPath = loadJSONFromAsset("$folderPath/advanced.json")
                }
                navigateToStartQuiz()
            }

            R.id.btn_play_again -> {
                navigateToStartQuiz()
            }
        }
    }

    private fun navigateToStartQuiz() {
        val intent = Intent(this, StartQuizActivity::class.java)
        intent.putExtra(DYNAMIC_PATH, dPath)
        intent.putExtra(COURSE_ID, courseId)
        intent.putExtra(TOPIC_ID, topicId)
        intent.putExtra(COURSE_NAME, courseName)
        intent.putExtra(TOPIC_NAME, topicName)
        intent.putExtra(TOPIC_LEVEL, topicLevel)
        intent.putExtra(LEVEL_COMPLETED, complete)
        intent.putExtra(TOPIC_POSITION, position!!)
        intent.putExtra(FOLDER_PATH, paths)
        intent.putExtra(FOLDER_NAME, folderName)
        intent.putExtra(TITLE_TOPIC, gradeTitle!!)
        startActivity(intent)
        finish()
    }

    private fun readFileLocally() {
        val courseJsonString = loadJSONFromAsset(localBlobcityPath + "Courses.json")
        //val courseJsonString = readFromFile("$localBlobcityPath/Courses.json")
        Log.d("courseJsonString", courseJsonString + "!");
        /*val jsonString = (activity!! as DashBoardActivity).loadJSONFromAsset( assetTestCoursePath + "topic.json")*/
        val gsonFile = Gson()
        val courseType = object : TypeToken<List<CoursesResponseModel>>() {}.type
        val courseResponseModel: ArrayList<CoursesResponseModel> = gsonFile
            .fromJson(courseJsonString, courseType)
        courseId = courseResponseModel[0].id
        courseName = courseResponseModel[0].syllabus.title
        localPath = "$localBlobcityPath$courseName/"
        // val jsonString = readFromFile(localPath +"topic.json")
        val jsonString = loadJSONFromAsset(localPath + "topic.json")
        Log.d("jsonString", jsonString)
        val topicType = object : TypeToken<TopicResponseModel>() {}.type
        val topicResponseModel: TopicResponseModel = gsonFile.fromJson(jsonString, topicType)

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

    private fun loadDataFromDb() {
        topicStatusVM!!.getSingleTopicStatus(topicId!!, gradeTitle!!).observe(this,
            object : Observer<List<TopicStatusEntity>> {
                override fun onChanged(t: List<TopicStatusEntity>?) {
                    topicStatusModelList = ArrayList()
                    topicStatusModelList!!.addAll(t!!)
                    if (topicStatusModelList != null) {
                        if (topicStatusModelList!!.size > 0) {
                            val branchId = topicId
                            for (topicStatusModels in topicStatusModelList!!) {
                                val id = topicStatusModels.topicId
                                val level = topicStatusModels.topicLevel

                                if (id!!.contains(branchId!!)) {
                                    if (level!!.contains("basic")) {
                                        isBasicCompleted = true
                                    }
                                    if (level.contains("intermediate")) {
                                        isIntermediateCompleted = true
                                    }
                                    if (level.contains("advance")) {
                                        isAdvancedCompleted = true
                                        val pathStringList: ArrayList<String> = ArrayList()
                                        for (imagePath in listAssetFiles(loaclAstraCardPath, applicationContext)) {
                                            if (imagePath.contains("png")) {
                                                pathStringList.add(imagePath)
                                            }
                                        }
                                        /*Collections.sort(pathStringList)*/
                                        var imagepath = ""
                                        for (path in pathStringList) {
                                            if (position!! == 0) {
                                                if (path.equals("1.png")) {
                                                    imagepath = loaclAstraCardPath + path
                                                }
                                            }
                                            if (position!! == 1) {
                                                if (path.equals("2.png")) {
                                                    imagepath = loaclAstraCardPath + path
                                                }
                                            }
                                            if (position!! == 2) {
                                                if (path.equals("3.png")) {
                                                    imagepath = loaclAstraCardPath + path
                                                }
                                            }
                                            if (position!! == 3) {
                                                if (path.equals("4.png")) {
                                                    imagepath = loaclAstraCardPath + path
                                                }
                                            }
                                            if (position!! == 4) {
                                                if (path.equals("5.png")) {
                                                    imagepath = loaclAstraCardPath + path
                                                }
                                            }
                                            if (position!! == 5) {
                                                if (path.equals("6.png")) {
                                                    imagepath = loaclAstraCardPath + path
                                                }
                                            }
                                            if (position!! == 6) {
                                                if (path.equals("7.png")) {
                                                    imagepath = loaclAstraCardPath + path
                                                }
                                            }
                                            if (position!! == 7) {
                                                if (path.equals("8.png")) {
                                                    imagepath = loaclAstraCardPath + path
                                                }
                                            }
                                            if (position!! == 8) {
                                                if (path.equals("9.png")) {
                                                    imagepath = loaclAstraCardPath + path
                                                }
                                            }
                                            if (position!! == 9) {
                                                if (path.equals("10.png")) {
                                                    imagepath = loaclAstraCardPath + path
                                                }
                                            }
                                        }
                                        Glide.with(this@QuizSummaryActivity)
                                            .load(Uri.parse(ConstantPath.WEBVIEW_PATH + imagepath))
                                            .into(iv_card_back)
                                        Log.e("position: ", position.toString())
                                        if (topicLevel!!.contains("advanced")) {
                                            handler.postDelayed(object : Runnable {
                                                override fun run() {
                                                    iv_card_front.startAnimation(zoomOutAnimation)
                                                    mSetRightOut!!.setTarget(iv_card_front)
                                                    mSetLeftIn!!.setTarget(iv_card_back)
                                                    iv_card_back.startAnimation(zoominAnimation)
                                                    mSetRightOut!!.start()
                                                    mSetLeftIn!!.start()
                                                }
                                            }, 1500)
                                        }
                                    }
                                }
                            }

                            if (!isBasicCompleted && isIntermediateCompleted) {
                                btn_next_quiz.text = "Quiz I"
                                return
                            }

                            if (isBasicCompleted && !isIntermediateCompleted) {
                                btn_next_quiz.text = "Quiz II"
                                return
                            }

                            if (isBasicCompleted && isIntermediateCompleted && !isAdvancedCompleted) {
                                btn_next_quiz.text = "Astra Quiz"
                                return
                            }

                            if (isBasicCompleted && isIntermediateCompleted && isAdvancedCompleted) {
                                btn_next_quiz.visibility = View.GONE
                                btn_next_level.visibility = View.VISIBLE
                                loadDatafromDb2()
                            }
                        }
                        if (!isBasicCompleted && !isIntermediateCompleted) {
                            if (topicLevel!!.contains("basic")) {
                                btn_next_quiz.text = "Quiz II"
                                return
                            }
                            if (topicLevel!!.contains("intermediate")) {
                                btn_next_quiz.text = "Quiz I"
                                return
                            }
                        }
                    }
                }
            })


    }

    private fun loadDatafromDb2() {
        Log.d("loadDatafromDb2",position.toString()+"!"+isAdvancedCompleted.toString()+"!"+isLastTopicAvailable.toString())
        if (position == 8 && isAdvancedCompleted) {
            topicStatusVM!!.getAllTopicStatus(gradeTitle!!).observe(this,
                object : Observer<List<TopicStatusEntity>> {
                    override fun onChanged(t: List<TopicStatusEntity>?) {
                        topicStatusModelList2 = ArrayList()
                        topicStatusModelList2!!.addAll(t!!)
                        for (item in topicStatusModelList2!!) {
                            if (topicStatusModelList2 != null) {
                                if (topicStatusModelList2!!.size > 0) {
                                    for (branchItem in branchesItemList!!) {
                                        val branchId = branchItem.id
                                        branchItem.basic = 0
                                        branchItem.intermediate = 0
                                        branchItem.advance = 0
                                        for (topicStatusModels in topicStatusModelList2!!) {
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
                    }

                })

            branchesItemList!!.forEachIndexed { index, branchesItem ->
                if (branchesItem.advance == 1) {
                    isLastTopicAvailable = true
                } else {
                    isLastTopicAvailable = false
                    btn_next_level.setBackgroundResource(R.drawable.quiz_button_inactive)
                    //btn_next_level.isEnabled = false
                    return
                }
            }
        }
    }

    private fun changeCameraDistance() {
        val distance = 7000
        val scale = resources.displayMetrics.density * distance
        iv_card_front!!.setCameraDistance(scale)
        iv_card_back!!.setCameraDistance(scale)
    }

    @SuppressLint("ResourceType")
    private fun loadAnimations() {
        zoominAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        zoomOutAnimation = AnimationUtils.loadAnimation(this, R.anim.zoom_out)
        mSetRightOut = AnimatorInflater.loadAnimator(this, R.anim.out_animation) as AnimatorSet
        mSetLeftIn = AnimatorInflater.loadAnimator(this, R.anim.in_animation) as AnimatorSet
    }

    private fun lastTopicDialog() {
        val dialogBuilder = AlertDialog.Builder(this, R.style.mytheme)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.last_topic_dialog_layout, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        val tv_ok = dialogView.findViewById(R.id._last_topic_btn_ok) as Button
        val alertDialog = dialogBuilder.create()
        //val tv_return = dialogView.findViewById(R.id.tv_return) as TextView
        tv_ok.setOnClickListener {

            alertDialog.dismiss()
        }

        //alertDialog.getWindow().setBackgroundDrawable(draw);
        alertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        alertDialog.show()
    }
}
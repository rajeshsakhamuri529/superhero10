package com.blobcity.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.blobcity.R
import com.blobcity.model.Topic
import com.blobcity.utils.ConstantPath
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    var topicName: String?=""
    override var layoutID: Int = R.layout.activity_sign_in

    override fun initView() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        val topic: Topic = intent.getSerializableExtra(ConstantPath.TOPIC) as Topic
        topicName = topic.title

        tv_topic_name.text = topicName

    }
}

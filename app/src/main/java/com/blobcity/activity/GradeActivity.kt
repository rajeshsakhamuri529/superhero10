package com.blobcity.activity

import android.content.Intent
import com.blobcity.R
import com.blobcity.adapter.GradeAdapter
import com.blobcity.interfaces.GradeClickListener
import kotlinx.android.synthetic.main.activity_grade.*

class GradeActivity : BaseActivity(), GradeClickListener {


    override fun setLayout(): Int {
        return R.layout.activity_grade
    }

    override fun initView() {
        rcv_grade.adapter = GradeAdapter(this, this)

    }

    override fun click() {
        val intent = Intent(this,
            DashBoardActivity::class.java)
        startActivity(intent)
    }
}
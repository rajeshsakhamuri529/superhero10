package com.blobcity.activity

import android.transition.TransitionInflater
import android.view.MenuItem
import com.blobcity.R

class CardReviewActivity : BaseActivity() {
    override fun setLayout(): Int {
        return R.layout.activity_card_review
    }

    override fun initView() {
        window.setSharedElementEnterTransition(TransitionInflater.from(this)
            .inflateTransition(R.transition.shared_element_transition))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            //to reverse the scene transition animation
            supportFinishAfterTransition()
        }
        return super.onOptionsItemSelected(item)
    }
}
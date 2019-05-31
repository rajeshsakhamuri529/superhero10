package com.blobcity.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.R
import com.blobcity.model.ImageModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.card_review_single_layout.view.*

class MyPagerAdapter(var context: Context,var listOfImages: ArrayList<ImageModel>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.card_review_single_layout, null)
        val iv_card_review = view.iv_card_review
        val linMain = view.linMain
        linMain.setTag(position)
        if (TextUtils.isEmpty(listOfImages[position].imagePath)) {
            Glide.with(context)
                .load(listOfImages[position].imageDrawable)
                .into(iv_card_review)
        }else{
            Glide.with(context)
                .load(listOfImages[position].imagePath)
                .into(iv_card_review)
        }
        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, p1: Any): Boolean {
        return view == p1
    }

    override fun getCount(): Int {
        return listOfImages.size
    }
}
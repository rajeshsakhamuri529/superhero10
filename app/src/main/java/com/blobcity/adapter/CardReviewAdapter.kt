package com.blobcity.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.blobcity.R
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.model.BranchesItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.card_review_single_layout.view.*

class CardReviewAdapter(var topicStatusEntityList: List<TopicStatusEntity>,
                        var branchesItemList:List<BranchesItem>,
                        var context: Activity
) : RecyclerView.Adapter<CardReviewAdapter
.AstraCardHorizontalViewHolder>(){
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AstraCardHorizontalViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.card_review_single_layout, null, false)
        return AstraCardHorizontalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return branchesItemList.size
    }

    override fun onBindViewHolder(holder: AstraCardHorizontalViewHolder, position: Int) {
        loadImage(R.drawable.purple_card, holder.iv_astra_card)
        for (topicStatusEntity in topicStatusEntityList) {
            if (position == topicStatusEntity.topicPosition) {
                loadImage(R.drawable.golden_card, holder.iv_astra_card)
            }
            if (position == (branchesItemList.size - 1) && position == topicStatusEntity.topicPosition) {
                loadImage(R.drawable.golden_card, holder.iv_astra_card)
            }
            if (position == (branchesItemList.size - 1)) {
                loadImage(R.drawable.pink_card, holder.iv_astra_card)
            }
        }
    }

    fun setBranchList(branchesItemList: List<BranchesItem>){
        this.branchesItemList = branchesItemList
        notifyDataSetChanged()
    }

    class AstraCardHorizontalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iv_astra_card = itemView.iv_card_review
    }

    fun loadImage(imageDrawable: Int, imageView: ImageView){
        Glide.with(context)
            .load(imageDrawable)
            .into(imageView)
    }
}
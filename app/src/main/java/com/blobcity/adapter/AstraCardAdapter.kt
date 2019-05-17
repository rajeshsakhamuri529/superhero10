package com.blobcity.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.blobcity.R
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.model.BranchesItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.astra_card_single_layout.view.*

class AstraCardAdapter(var topicStatusEntityList: List<TopicStatusEntity>,
                       var branchesItemList:List<BranchesItem>,
                       var context: Context)
    : RecyclerView.Adapter<AstraCardAdapter.AstraCardViewHolder>(){

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): AstraCardViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.astra_card_single_layout, null, false)
        return AstraCardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return branchesItemList.size
    }

    override fun onBindViewHolder(holder: AstraCardViewHolder, position: Int) {
        /*val topicStatusEntity = topicStatusEntityList.get(position)*/
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

    class AstraCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iv_astra_card = itemView.iv_astra_card
    }

    fun loadImage(imageDrawable: Int, imageView: ImageView){
        Glide.with(context)
            .load(imageDrawable)
            .into(imageView)
    }
}
package com.blobcity.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.R
import com.blobcity.interfaces.TopicClickListener
import com.blobcity.model.BranchesItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.topics_single_layout.view.*

class ChaptersAdapter(val context: Context,
                      val branchesItemList: List<BranchesItem>,
                      val topicClickListener: TopicClickListener) :
    RecyclerView.Adapter<ChaptersAdapter.ChaptersViewHolder>()
{

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ChaptersViewHolder {
        return ChaptersViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.topics_single_layout, p0, false)
        )
    }

    override fun getItemCount(): Int {
        return branchesItemList.size
    }

    override fun onBindViewHolder(holder: ChaptersViewHolder, position: Int) {
        holder.tv_topic_number.text = branchesItemList[position].topic.index.toString()
        holder.tv_topic_name.text = branchesItemList[position].topic.title
        if (branchesItemList.size-1 == position){
            holder.singleTopic.setBackgroundResource(R.drawable.dashboard_bottom_corner)
        }
        if (position == 0){
            holder.singleTopic.setBackgroundResource(R.drawable.dashboard_top_corner)
        }
        holder.singleTopic.setOnClickListener {
            topicClickListener.onClick(branchesItemList[position].topic.folderName,
                branchesItemList[position].id, branchesItemList[position].topic.title)
        }

        if (branchesItemList[position].basic == 1){
            Glide.with(context)
                .load(R.drawable.progress_icon)
                .into(holder.iv_progress1)
        }
        else{
            Glide.with(context)
                .load(R.drawable.progress_icon_grey)
                .into(holder.iv_progress1)
        }

        if (branchesItemList[position].intermediate == 1){
            Glide.with(context)
                .load(R.drawable.progress_icon)
                .into(holder.iv_progress2)
        }
        else{
            Glide.with(context)
                .load(R.drawable.progress_icon_grey)
                .into(holder.iv_progress2)
        }

        if (branchesItemList[position].advance == 1){
            Glide.with(context)
                .load(R.drawable.progress_icon)
                .into(holder.iv_progress3)
        }
        else{
            Glide.with(context)
                .load(R.drawable.progress_icon_grey)
                .into(holder.iv_progress3)
        }
    }

    class ChaptersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val singleTopic = itemView.rl_single_topics
        val tv_topic_number = itemView.tv_topic_number
        val tv_topic_name = itemView.tv_topic_name
        val iv_progress1 = itemView.iv_progress_icon1
        val iv_progress2 = itemView.iv_progress_icon2
        val iv_progress3 = itemView.iv_progress_icon3
    }
}
package com.blobcity

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.topics_single_layout.view.*

class ChaptersAdapter(val context: Context) :
    RecyclerView.Adapter<ChaptersAdapter.ChaptersViewHolder>()
{

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ChaptersViewHolder {
        return ChaptersViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.topics_single_layout, p0, false))
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ChaptersViewHolder, position: Int) {
        if (position == 9){
            holder.singleTopic.setBackgroundResource(R.drawable.dashboard_bottom_corner)
        }
    }

    class ChaptersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val singleTopic = itemView.rl_single_topics
    }
}
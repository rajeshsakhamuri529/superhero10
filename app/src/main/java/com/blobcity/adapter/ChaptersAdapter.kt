package com.blobcity.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.blobcity.R
import com.blobcity.interfaces.TopicClickListener
import com.blobcity.model.BranchesItem
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.topics_single_layout.view.*

class ChaptersAdapter(val context: Context,
                      val branchesItemList: List<BranchesItem>,
                      val topicClickListener: TopicClickListener) :
    RecyclerView.Adapter<ChaptersAdapter.ChaptersViewHolder>() {

    var isLastTopicAvailable = false

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ChaptersViewHolder {
        return ChaptersViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.topics_single_layout, p0, false)
        )
    }

    override fun getItemCount(): Int {
        return branchesItemList.size - 1
    }

    override fun onBindViewHolder(holder: ChaptersViewHolder, position: Int) {


        if(position == 1){
            holder.lockLayout.visibility = View.VISIBLE
        }else{
            if(position > 1){
                holder.topLayout.alpha = 0.4f
                //holder.tv_topic_name.textColors.withAlpha(100)
               // holder.tv_topic_number.alpha = 0.0f
               // holder.tv_topic_name.visibility = View.GONE
                holder.iv_progress1.visibility = View.INVISIBLE
                holder.iv_progress2.visibility = View.INVISIBLE
                holder.iv_progress3.visibility = View.INVISIBLE
            }else{
                holder.tv_topic_number.alpha = 1.0f
                holder.iv_progress1.visibility = View.VISIBLE
                holder.iv_progress2.visibility = View.VISIBLE
                holder.iv_progress3.visibility = View.VISIBLE
            }
            holder.lockLayout.visibility = View.GONE
        }

        var index = branchesItemList[position].topic.index.toString()
        Log.d("chapter adapter",index);
       /* if(position != itemCount){*/
            if(index.length == 1)
            {
                index = "0"+index
            }
        /*}else{

        }*/

        Log.d("chapter adapter",index);
        holder.tv_topic_number.text = index
        holder.tv_topic_name.text = branchesItemList[position].topic.title
        if (position == 0){
            holder.singleTopic.setBackgroundResource(R.drawable.dashboard_top_corner)
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
        holder.singleTopic.setOnClickListener {
            if (position == branchesItemList.size-1) {
                if (isLastTopicAvailable) {
                    topicClickListener.onClick(
                        branchesItemList[position].topic,
                        branchesItemList[position].id, position
                    )
                }else{
                    lastTopicDialog()
                }
            }else{
                topicClickListener.onClick(
                    branchesItemList[position].topic,
                    branchesItemList[position].id, position
                )
            }
        }

        if (position < branchesItemList.size - 1) {
            branchesItemList.forEachIndexed { index, branchesItem ->

                if(index != 9) {
                    if (branchesItem.advance == 1) {
                        isLastTopicAvailable = true
                    } else {
                        isLastTopicAvailable = true
                        return
                    }
                }else {
                    return
                }
            }
        }
    }

    fun lastItem(holder: ChaptersViewHolder){
        holder.singleTopic.setBackgroundResource(R.drawable.dashboard_bottom_corner)
       /* if (isLastTopicAvailable) {
            Log.d("lastItem",isLastTopicAvailable.toString()+"!")
            holder.tv_topic_number.alpha = 1.0f
            holder.iv_progress1.visibility = View.VISIBLE
            holder.iv_progress2.visibility = View.VISIBLE
            holder.iv_progress3.visibility = View.VISIBLE

        }else{
            Log.d("lastItem",isLastTopicAvailable.toString()+"!")
            //holder.singleTopic.alpha = 0.8f
            //holder.tv_topic_name.textColors.withAlpha(100)
            holder.tv_topic_number.alpha = 0.0f
            holder.tv_topic_name.visibility = View.GONE
            holder.iv_progress1.visibility = View.INVISIBLE
            holder.iv_progress2.visibility = View.INVISIBLE
            holder.iv_progress3.visibility = View.INVISIBLE
        }*/
    }

    class ChaptersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val singleTopic = itemView.rl_single_topics
        val tv_topic_number = itemView.tv_topic_number
        val tv_topic_name = itemView.tv_topic_name
        val iv_progress1 = itemView.iv_progress_icon1
        val iv_progress2 = itemView.iv_progress_icon2
        val iv_progress3 = itemView.iv_progress_icon3

        val topLayout = itemView.top_layout
        val lockLayout = itemView.lock_layout

    }

    private fun lastTopicDialog() {
        val dialogBuilder = AlertDialog.Builder(context, R.style.mytheme)
        val inflater = LayoutInflater.from(context)
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
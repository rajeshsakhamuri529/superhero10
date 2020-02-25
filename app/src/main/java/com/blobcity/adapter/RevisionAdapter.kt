package com.blobcity.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.R
import com.blobcity.interfaces.RevisionItemClickListener
import com.blobcity.model.RevisionModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.revision_list_item.view.*

class RevisionAdapter(
    val context: Context,
    val revisionItemList: ArrayList<RevisionModel>,
    val pdfItemClickListener: RevisionItemClickListener
) : RecyclerView.Adapter<RevisionAdapter.RevisionViewHolder>() {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RevisionViewHolder {
       // Log.e("revision adapter","onCreateViewHolder.....revisionItemList...."+revisionItemList.size)
        return RevisionViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.revision_list_item, p0, false)
        )
    }

    override fun getItemCount(): Int {
       // Log.e("revision adapter","getItemCount.....revisionItemList...."+revisionItemList)
       // return 2
       return revisionItemList.size
    }

    override fun onBindViewHolder(holder: RevisionViewHolder, position: Int) {
        //Log.e("revision adapter","onBindViewHolder...single..revisionItemList...."+revisionItemList[position])

        //Log.e("revision adapter","onBindViewHolder....revisionItemList...."+revisionItemList[position].imageLink)

        holder.rid.text = revisionItemList[position].rId
        holder.title.text = revisionItemList[position].tilte
        holder.timetotaken.text = revisionItemList[position].timeToRead
        holder.shortdiscription.text = revisionItemList[position].shortDescription

        var str: String? = revisionItemList[position].tags?.replace("[","")
        var str1: String? = str?.replace("]","")

        Log.e("revision fragment",".....str1..."+str1);


        val strarray = str1?.split(",")?.toTypedArray()
        Log.e("revision fragment",".....strarray..."+strarray?.size);
        Log.e("revision fragment",".....strarray..."+ (strarray?.get(0)));
        val builder = StringBuilder()
        for (name in strarray!!) {
            println(name)

            builder.append("#")
                .append(name.trim())
                .append(" ")
        }
        Log.e("revision fragment",".....builder..."+builder);
        holder.tags.text = builder

        holder.topLayout.setOnClickListener {
            revisionItemList[position].filename?.let { it1 -> pdfItemClickListener.onClick(it1) }
        }

        Glide.with(context)
            .load(revisionItemList[position].imageLink)
            .into(holder.thumbnail)


    }


    class RevisionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail = itemView.thumbnail
        var rid = itemView.rid
        val title = itemView.title
        val timetotaken = itemView.timetotaken
        val shortdiscription = itemView.shortdescription
        val tags = itemView.tags
        val topLayout = itemView.rootLayout
    /*val singleTopic = itemView.rl_single_topics
    val tv_topic_number = itemView.tv_topic_number
    val tv_topic_name = itemView.tv_topic_name
    val iv_progress1 = itemView.iv_progress_icon1
    val iv_progress2 = itemView.iv_progress_icon2
    val iv_progress3 = itemView.iv_progress_icon3

    val topLayout = itemView.top_layout
    val lockLayout = itemView.lock_layout

    val lock = itemView.lock
    val lock_txt = itemView.lock_txt*/
    }
}
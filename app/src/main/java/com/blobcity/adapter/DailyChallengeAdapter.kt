package com.blobcity.adapter

import android.content.Context
import android.content.res.ColorStateList

import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blobcity.R
import com.blobcity.database.DatabaseHandler
import com.blobcity.entity.DailyChallenge
import com.blobcity.fragment.DailyChallengeFragment
import com.blobcity.model.ChallengeModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.challenge_single_layout.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat


class DailyChallengeAdapter(
    val context: Context,
    val dailyChallengeList: List<ChallengeModel>,
    val dailyChallengeFragment: DailyChallengeFragment) : RecyclerView.Adapter<DailyChallengeAdapter.ChallengeViewHolder>() {

    var databaseHandler: DatabaseHandler?= null
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DailyChallengeAdapter.ChallengeViewHolder {
        databaseHandler = DatabaseHandler(context);
        return DailyChallengeAdapter.ChallengeViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.challenge_single_layout, p0, false)
        )
    }

    override fun getItemCount(): Int {
        return dailyChallengeList.size

    }

    override fun onBindViewHolder(holder: DailyChallengeAdapter.ChallengeViewHolder, position: Int) {
        //holder.tv_topic_name.text = dailyChallengeList[position].challengetitle.toString()

        var inputFormat:DateFormat = SimpleDateFormat("ddMMyyyy")
        var outputFormat: DateFormat = SimpleDateFormat("MMM dd, yyyy")
        var inputDateStr = dailyChallengeList[position].challengedate.toString()
        var date = inputFormat.parse(inputDateStr)
        var outputDateStr = outputFormat.format(date)
        Log.e("daily challenge","date ......outputDateStr....."+outputDateStr);

        if(dailyChallengeList[position]?.serialno!!.toInt() < 10){
            holder.tv_challenge_date.setText("Challenge #0"+dailyChallengeList[position]?.serialno)
        }else{
            holder.tv_challenge_date.setText("Challenge #"+dailyChallengeList[position]?.serialno)
        }


        var dailyChallenge: DailyChallenge? = databaseHandler?.getDailyChallenge(dailyChallengeList[position].documentid)

        Log.e("daily challenge adapter","attempt value....."+dailyChallenge?.attempt)
        if(dailyChallenge?.attempt == "0"){
            Glide.with(context)
                .load(R.drawable.progress_icon_grey)
                .into(holder.iv_progress1)
        }else if(dailyChallenge?.attempt == "1"){
            Glide.with(context)
                .load(R.drawable.progress_icon)
                .into(holder.iv_progress1)
        }else if(dailyChallenge?.attempt == "2"){
            Log.e("daily challenge adapter","attempt value...else.."+dailyChallenge?.attempt)
            //ImageViewCompat.setImageTintList(holder.iv_progress1, ColorStateList.valueOf(context.getColor(R.color.level_failed)));
            Glide.with(context)
                .load(R.drawable.red_circle)
                .into(holder.iv_progress1)
           // holder.iv_progress1.setColorFilter(ContextCompat.getColor(context, R.color.level_failed));
        }



        holder.topLayout.setOnClickListener {
            dailyChallengeList[position]?.let { it1 -> dailyChallengeFragment.onClick(it1) }
        }
    }
    class ChallengeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_topic_name = itemView.tv_topic_name
        val iv_progress1 = itemView.iv_progress_icon2
        val tv_challenge_date = itemView.tv_challenge_date
        val topLayout = itemView.rootLayout

    }
}
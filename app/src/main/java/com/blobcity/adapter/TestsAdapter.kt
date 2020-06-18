package com.blobcity.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blobcity.R
import com.blobcity.interfaces.TestQuizReviewClickListener
import com.blobcity.model.TestQuizFinal
import com.blobcity.model.TestsModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.tests_list_item.view.*
import java.text.SimpleDateFormat

import java.util.*





class TestsAdapter(val context: Context,
                   val branchesItemList: List<TestQuizFinal>,
                    val quiztopicreviewListener: TestQuizReviewClickListener
) :
    RecyclerView.Adapter<TestsAdapter.TestsViewHolder>()  {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): TestsAdapter.TestsViewHolder {
        return TestsAdapter.TestsViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.tests_list_item, p0, false)
        )
    }

    override fun getItemCount(): Int {
        return branchesItemList.size
    }

    override fun onBindViewHolder(holder: TestsAdapter.TestsViewHolder, position: Int) {
        var count:Int = 0
        var questionanswers:String = branchesItemList[position].questionAnswers
        var queans:List<String> = questionanswers.split(",")
        for(i in 0 until branchesItemList[position].totalQuestions!!){
            var ans1:List<String> = queans.get(i).split("~")
            if(ans1[1].equals("opt1")){
                count++
            }
        }
        if(count == branchesItemList[position].totalQuestions){
            Glide.with(context)
                .load(R.drawable.ic_tick_green)
                .into(holder.iv_progress_icon)
        }else{
            Glide.with(context)
                .load(R.drawable.red_circle)
                .into(holder.iv_progress_icon)
        }
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        var oldDate = dateFormat.parse(branchesItemList[position].pdate)
        var current = Date().time
        //val days = Days.daysBetween(date1, date2).getDays()
        val diff = current - oldDate.time
        //Log.e("tests adapter","diff date...."+diff);
        val diff1 = diff.toInt() / (1000 * 60 * 60 * 24)
        Log.e("tests adapter","diff date...."+diff1);
        if(diff1 == 1){
            holder.tv_challenge_date.text = ""+diff1 + "  day ago"
        }else{
            holder.tv_challenge_date.text = ""+diff1 + "  days ago"
        }

        if(branchesItemList[position].timetaken.equals("0")){
            holder.tv_challenge_time.text =  "1"
            holder.tv_challenge_time_mins.text = "min"
        }else if(branchesItemList[position].timetaken.equals("1")){
            holder.tv_challenge_time.text = branchesItemList[position].timetaken
            holder.tv_challenge_time_mins.text = "min"
        }else{
            holder.tv_challenge_time.text = branchesItemList[position].timetaken
            holder.tv_challenge_time_mins.text = "mins"
        }

        holder.tv_challenge_score.text = ""+count+" / "+branchesItemList[position].totalQuestions

        holder.rootLayout.setOnClickListener(View.OnClickListener {
            quiztopicreviewListener.onClick(branchesItemList[position])
        })

    }

    class TestsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iv_progress_icon = itemView.iv_progress_icon
        val tv_challenge_date = itemView.tv_challenge_date
        val tv_challenge_time = itemView.tv_challenge_time
        val tv_challenge_time_mins = itemView.tv_challenge_time_mins

        val tv_challenge_score = itemView.tv_challenge_score
        val rootLayout = itemView.rootLayout;

    }

}
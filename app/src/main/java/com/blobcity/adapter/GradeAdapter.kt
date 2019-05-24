package com.blobcity.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.blobcity.R
import com.blobcity.interfaces.GradeClickListener
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.grade_single_layout.view.*

class GradeAdapter(var context: Activity, var clickListener: GradeClickListener) : RecyclerView.Adapter<GradeAdapter.GradeViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): GradeViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.grade_single_layout, null, false)
        return GradeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: GradeViewHolder, position: Int) {
        if (position == 0){
            loadImage( R.drawable.grade_5, holder.iv_grade)
            holder.rl_grade.setPadding(0, 60, 0, 0)
        }
        if (position == 1){
            loadImage( R.drawable.grade_6, holder.iv_grade)
            holder.rl_grade.setPadding(0, 60, 0, 0)
        }
        if (position == 2){
            loadImage( R.drawable.grade_7, holder.iv_grade)
            holder.rl_grade.setPadding(0, 60, 0, 0)
        }
        if (position == 3){
            loadImage( R.drawable.coming_soon, holder.iv_grade)
            holder.rl_grade.setPadding(0, 60, 0, 200)
        }
        holder.iv_grade.setOnClickListener {
            clickListener.click()
        }
    }

    class GradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iv_grade = itemView.iv_grade
        val rl_grade = itemView.rl_grade
    }

    fun loadImage(imageDrawable: Int, imageView: ImageView){
        Glide.with(context)
            .load(imageDrawable)
            .into(imageView)
    }
}
package com.yomplex.simple.interfaces

import android.view.View
import com.yomplex.simple.model.Books

interface BooksClickListener {

    fun onStarClick(title: String,category:String,status:Int)

    fun onReadClick(view: View, books:Books,status:Int)
}
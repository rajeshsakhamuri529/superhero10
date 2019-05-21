package com.blobcity.interfaces

import android.widget.ImageView

interface AstraCardClickListener {
    fun onClick(imageView: ImageView, image: Int, position: Int)
}
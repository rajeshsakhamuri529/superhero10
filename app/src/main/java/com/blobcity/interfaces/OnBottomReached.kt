package com.blobcity.interfaces

import com.blobcity.adapter.ChaptersAdapter

interface OnBottomReached {
    fun onBottom(chaptersViewHolder: ChaptersAdapter.ChaptersViewHolder)
}
package com.blobcity.interfaces

import com.blobcity.model.RevisionModel

interface RevisionItemClickListener {
    fun onClick(pdfUrl: RevisionModel)

    fun onMoreButttonClicked(open:String,position:Int)
}
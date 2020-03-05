package com.blobcity.interfaces

import com.blobcity.model.RevisionModel

interface RevisionItemClickListener {
    fun onClick(pdfUrl: RevisionModel)
}
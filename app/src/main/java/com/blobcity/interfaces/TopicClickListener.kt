package com.blobcity.interfaces

import com.blobcity.model.Topic

interface TopicClickListener {
    fun onClick(topic: Topic, topicId: String)
}
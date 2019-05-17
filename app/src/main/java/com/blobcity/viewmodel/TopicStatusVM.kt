package com.blobcity.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.repository.TopicStatusRepository

class TopicStatusVM(application: Application) : AndroidViewModel(Application()) {
    private var topicStatusRepository: TopicStatusRepository? = null
    private var topicStatusList: LiveData<List<TopicStatusEntity>>? = null

    init {
        topicStatusRepository = TopicStatusRepository(application)
        topicStatusList = topicStatusRepository!!.getAllTopicStatus()
    }

    fun getAllTopicStatus() : LiveData<List<TopicStatusEntity>>{
        return topicStatusList!!
    }

    fun getSingleTopicStatus(topicId: String) : LiveData<List<TopicStatusEntity>>{
        return topicStatusRepository!!.getSingleTopicStatus(topicId)
    }

    fun getTopicsByLevel(topicLevel: String) : LiveData<List<TopicStatusEntity>>{
        return topicStatusRepository!!.getTopicsByLevel(topicLevel)
    }

    fun insert(topicStatusEntity: TopicStatusEntity){
        topicStatusRepository!!.insert(topicStatusEntity)
    }
}
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

    fun insert(courseId: String, uId: String, topicId: String, topicLevel: String, dbPosition: Int){
        val topicStatusEntity = TopicStatusEntity()
        topicStatusEntity.courseId = courseId
        topicStatusEntity.topicId = topicId
        topicStatusEntity.uid = uId
        topicStatusEntity.topicLevel = topicLevel
        topicStatusEntity.isLevelComplete = 1
        topicStatusEntity.topicPosition = dbPosition
        topicStatusRepository!!.insert(topicStatusEntity)
    }
}
package com.blobcity.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import com.blobcity.entity.TopicStatusEntity
import com.blobcity.repository.TopicStatusRepository

class TopicStatusVM(application: Application) : AndroidViewModel(Application()) {
    private var topicStatusRepository: TopicStatusRepository? = null

    init {
        topicStatusRepository = TopicStatusRepository(application)
    }

    fun getAllTopicStatus(gradeTitle: String) : LiveData<List<TopicStatusEntity>>{
        return topicStatusRepository!!.getAllTopicStatus(gradeTitle)
    }

    fun getSingleTopicStatus(topicId: String, gradeTitle: String) : LiveData<List<TopicStatusEntity>>{
        return topicStatusRepository!!.getSingleTopicStatus(topicId, gradeTitle)
    }

    fun getTopicsByLevel(topicLevel: String, gradeTitle: String) : LiveData<List<TopicStatusEntity>>{
        return topicStatusRepository!!.getTopicsByLevel(topicLevel, gradeTitle)
    }


    fun insert(courseId: String, uId: String, topicId: String,
               topicLevel: String, dbPosition: Int, gradeTitle: String){
        val topicStatusEntity = TopicStatusEntity()
        topicStatusEntity.courseId = courseId
        topicStatusEntity.topicId = topicId
        topicStatusEntity.uid = uId
        topicStatusEntity.topicLevel = topicLevel
        topicStatusEntity.isLevelComplete = 1
        topicStatusEntity.topicPosition = dbPosition
        topicStatusEntity.gradeTitle = gradeTitle
        topicStatusRepository!!.insert(topicStatusEntity)
    }
}
package com.blobcity.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.os.AsyncTask
import com.blobcity.dao.TopicStatusDao
import com.blobcity.database.QuizDatabase
import com.blobcity.entity.TopicStatusEntity

class TopicStatusRepository(application: Application) {
    private var topicStatusDao: TopicStatusDao ?= null
    private var mAllTopicStatusEntity: LiveData<List<TopicStatusEntity>> ?= null

    init {
        val db : QuizDatabase = QuizDatabase.getDatabase(application)
        topicStatusDao = db.topicStatusDao
        mAllTopicStatusEntity = topicStatusDao!!.getAllTopicStatus()
    }

    fun getAllTopicStatus() : LiveData<List<TopicStatusEntity>>{
        return mAllTopicStatusEntity!!
    }

    fun getSingleTopicStatus(topicID: String) : LiveData<List<TopicStatusEntity>>{
        return topicStatusDao!!.getSingleTopicStatus(topicID)
    }

    fun getTopicsByLevel(topicLevel: String) : LiveData<List<TopicStatusEntity>>{
        return topicStatusDao!!.getTopicByLevel(topicLevel)
    }

    fun insert(topicStatusEntity: TopicStatusEntity){
        insertAsyncTask(topicStatusDao!!).execute(topicStatusEntity)
    }

    private class insertAsyncTask(topicStatusDao: TopicStatusDao)
        : AsyncTask<TopicStatusEntity, Void, Void>(){
        var topicStatusDao: TopicStatusDao ?= null
        init {
            this.topicStatusDao = topicStatusDao
        }
        override fun doInBackground(vararg params: TopicStatusEntity?): Void? {
            topicStatusDao!!.insert(params[0]!!)
            return null
        }

    }
}
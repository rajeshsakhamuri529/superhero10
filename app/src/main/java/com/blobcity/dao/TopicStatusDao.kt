package com.blobcity.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.blobcity.entity.TopicStatusEntity

@Dao
interface TopicStatusDao {

    @Insert
    fun insert(topicStatusEntity: TopicStatusEntity)

    @Query("SELECT * FROM topic_status")
    fun getAllTopicStatus() : LiveData<List<TopicStatusEntity>>

    @Query("SELECT * FROM topic_status WHERE Topic_Id = :topicId")
    fun getSingleTopicStatus(topicId: String) : LiveData<TopicStatusEntity>
}
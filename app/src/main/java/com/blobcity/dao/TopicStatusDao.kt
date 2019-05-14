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
    fun getAllTopicStatus() : LiveData<ArrayList<TopicStatusEntity>>

    @Query("SELECT * FROM topic_status WHERE U_Id = :uid")
    fun getSingleTopicStatus(uid: String) : LiveData<TopicStatusEntity>
}
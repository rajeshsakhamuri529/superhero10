package com.blobcity.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import com.blobcity.entity.TopicStatusEntity;

import java.util.List;

@Dao
public interface TopicStatusDao {
    @Insert
    void insert(TopicStatusEntity topicStatusEntity);

    @Query("SELECT * FROM topic_status")
    LiveData<List<TopicStatusEntity>> getAllTopicStatus();

    @Query("SELECT * FROM topic_status WHERE Topic_Id = :topicId")
    LiveData<List<TopicStatusEntity>> getSingleTopicStatus( String topicId);

    @Query("SELECT * FROM topic_status WHERE Topic_Level = :topic_level")
    LiveData<List<TopicStatusEntity>> getTopicByLevel( String topic_level);
}

package com.blobcity.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "topic_status")
class TopicStatusEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @ColumnInfo(name = "U_Id")
    var uid: String?=""

    @ColumnInfo(name = "Course_Id")
    var courseId: String?=""

    @ColumnInfo(name = "Topic_Id")
    var topicId: String?=""

    @ColumnInfo(name = "Topic_Level")
    var topicLevel: String?=""

    @ColumnInfo(name = "is_level_completed")
    var isLevelComplete: Int = 0
}
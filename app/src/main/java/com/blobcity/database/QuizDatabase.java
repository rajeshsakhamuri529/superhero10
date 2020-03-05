package com.blobcity.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.blobcity.dao.TopicStatusDao;
import com.blobcity.entity.TopicStatusEntity;

@Database(entities = {TopicStatusEntity.class}, version = 2, exportSchema = false)
public abstract class QuizDatabase extends RoomDatabase {

    private static QuizDatabase database;

    public abstract TopicStatusDao getTopicStatusDao();
    //public abstract RevisionVersionStatusDao getRevisionVersionStatusDao();

    public static QuizDatabase getDatabase(final Context context){
        if (database == null){
            synchronized (QuizDatabase.class){
                if (database == null){
                    database = Room
                            .databaseBuilder(context,
                            QuizDatabase.class,
                            "quiz_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return database;
    }
}
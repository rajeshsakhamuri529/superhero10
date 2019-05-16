package com.blobcity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import com.blobcity.entity.TopicStatusEntity;
import com.blobcity.repository.TopicStatusRepository;

import java.util.List;

public class TopicStatusViewModel extends AndroidViewModel {

    private TopicStatusRepository topicStatusRepository;
    private LiveData<List<TopicStatusEntity>> topicStatusList;
    private String topicId;

    public TopicStatusViewModel(@NonNull Application application) {
        super(application);
        topicStatusRepository = new TopicStatusRepository(application);
        topicStatusList = topicStatusRepository.getAllTopicStatus();
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public LiveData<List<TopicStatusEntity>> getAllTopicStatus() { return topicStatusList; }

    public void insert(TopicStatusEntity word) { topicStatusRepository.insert(word); }
}

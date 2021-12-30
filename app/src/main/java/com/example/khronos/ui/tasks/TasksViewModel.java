package com.example.khronos.ui.tasks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TasksViewModel extends ViewModel {

    private static final String TAG = "TasksViewModel";

    private MutableLiveData<String> mTextLive;

    public TasksViewModel() {
        mTextLive = new MutableLiveData<>();
        mTextLive.setValue("This is tasks fragment");

    }

    public LiveData<String> getText() {
        return mTextLive;
    }

}
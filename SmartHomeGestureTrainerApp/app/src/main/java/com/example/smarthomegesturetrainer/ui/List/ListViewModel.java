package com.example.smarthomegesturetrainer.ui.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Select a gesture from the Menu");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
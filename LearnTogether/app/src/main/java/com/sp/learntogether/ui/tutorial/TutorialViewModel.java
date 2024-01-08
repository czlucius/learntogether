package com.sp.learntogether.ui.tutorial;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TutorialViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public TutorialViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
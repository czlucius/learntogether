package com.sp.learntogether.ui.auth;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {
    private MutableLiveData<Uri> imageUri = new MutableLiveData<>(null);

    public LiveData<Uri> getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri.setValue(imageUri);
    }

}

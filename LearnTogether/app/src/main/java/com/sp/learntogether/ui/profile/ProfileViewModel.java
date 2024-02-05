package com.sp.learntogether.ui.profile;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.sp.learntogether.astraDBHelper;
import com.sp.learntogether.io.DatabaseInteractor;
import com.sp.learntogether.models.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileViewModel extends AndroidViewModel {
    private static final String TAG = "ProfileViewModel";

    public ProfileViewModel(@NonNull Application application) {
        super(application);
    }



    private final MutableLiveData<Profile> profile = new MutableLiveData<>(null);

    public LiveData<Profile> getProfile() {
        return profile;
    }
}
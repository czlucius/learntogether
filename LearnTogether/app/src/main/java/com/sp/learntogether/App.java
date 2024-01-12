package com.sp.learntogether;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class App extends Application {
    private static final String TAG = "App";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        FirebaseApp.initializeApp(this);
    }
}

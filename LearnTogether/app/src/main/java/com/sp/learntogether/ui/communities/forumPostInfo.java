package com.sp.learntogether.ui.communities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class forumPostInfo {
    private String name, id, forumQuestion, currentDateTime;
    private Bitmap profileImage;

    public forumPostInfo(String name, String id, String profileImageLink, String forumQuestion, String currentDateTime) {
        this.name = name;
        this.id = id;
        this.profileImage = getBitmapFromURL(profileImageLink);
        this.forumQuestion = forumQuestion;
        this.currentDateTime = currentDateTime;
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String uid) {
        this.id = id;
    }

    public String getForumQuestion() {
        return forumQuestion;
    }

    public void setForumQuestion(String forumQuestion) {
        this.forumQuestion = forumQuestion;
    }

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }
}

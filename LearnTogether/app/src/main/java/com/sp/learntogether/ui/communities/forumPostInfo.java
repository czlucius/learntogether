package com.sp.learntogether.ui.communities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

public class forumPostInfo {
    private String name, id, forumQuestion, currentDateTime;
    private Bitmap profileImage;
    private RequestQueue queue;
    private String personuid;

    public forumPostInfo(String name, String id, String profileImageLink, String forumQuestion, String currentDateTime) {
        this.name = name;
        this.id = id;
        this.forumQuestion = forumQuestion;
        this.currentDateTime = currentDateTime;
    }

    public void setPersonuid(String personuid) {
        this.personuid = personuid;
    }

    public String getPersonuid() {
        return personuid;
    }

    public void setBitmapFromURL(Context ctx, String src, Consumer<Bitmap> consumer) {
        if (queue == null) {
            queue = Volley.newRequestQueue(ctx);
        }
        ImageRequest req = new ImageRequest(src, bitmap -> {
            this.profileImage = bitmap;
            consumer.accept(bitmap);
        }, 400, 400, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888, Throwable::printStackTrace);
        queue.add(req);
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

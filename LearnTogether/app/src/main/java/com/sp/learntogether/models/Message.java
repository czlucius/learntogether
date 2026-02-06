package com.sp.learntogether.models;

import android.content.Context;
import android.net.Uri;

import com.sp.learntogether.App;
import com.sp.learntogether.R;

public class Message {
    private final boolean system;
    private String description;
    private final Uri imageLocation;

    public Message(boolean system, String description, Uri imageLocation) {
        this.system = system;
        this.description = description;
        this.imageLocation = imageLocation;
    }

    public boolean isSystem() {
        return system;
    }

    public String getDescription() {
        return description;
    }

    public Uri getImageLocation() {
        if (system)  {
//            R.drawable.ai
            return Uri.parse("android.resource://" + App.PACKAGE_NAME + "/drawable/ai");
        }
        return imageLocation;
    }

    public String getSenderName(Context context) {
        if (system) {
            return context.getString(R.string.learnbot);
        } else {
            return context.getString(R.string.you);
        }
    }
}

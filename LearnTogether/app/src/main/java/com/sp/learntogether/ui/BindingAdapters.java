package com.sp.learntogether.ui;

import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.sp.learntogether.R;

// from
public class BindingAdapters {
    private static final String TAG = "BindingAdapters";

    private static RequestQueue queue;
    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView imageView, String imageUrl) {
        if (queue == null) {
            queue = Volley.newRequestQueue(imageView.getContext());
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            ImageRequest imageRequest = new ImageRequest(imageUrl,
                    imageView::setImageBitmap, 0, 0, ImageView.ScaleType.CENTER_CROP, null,
                    error -> Log.e(TAG, "Error loading image: " + error.getMessage()));
            queue.add(imageRequest);
        }
    }
}
package com.sp.learntogether.data;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.sp.learntogether.R;
import com.sp.learntogether.astraDBHelper;
import com.sp.learntogether.io.DatabaseInteractor;
import com.sp.learntogether.models.Profile;
import com.sp.learntogether.objects.RegistrationException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

public class RegistrationHelper {
    private static final String TAG = "RegistrationHelper";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public void signup(Context context, Profile profile, Uri profilePic, Function<Void, Void> onFinish, String fcmToken, String idToken) throws RegistrationException {
        RequestQueue queue = Volley.newRequestQueue(context.getApplicationContext());
        DatabaseInteractor interactor = DatabaseInteractor.getInstance(context);
        JSONObject obj;
        try {
            obj = profile.toJsonObject();
            obj.put("fcmtoken", fcmToken);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RegistrationException(context.getString(R.string.json_parsing_failure));
        }


        String storageUrl = "https://learntogether.czlucius.dev/storage/upload";

        JsonObjectRequest request = new JsonObjectRequest(storageUrl, resp -> {
            try {

                String imageUrl = resp.getString("url");
                obj.put("profilepicurl", imageUrl);
                interactor.post(obj, astraDBHelper.userDetailsUrl, response -> {
                    onFinish.apply(null);
                }, err -> {
                    err.printStackTrace();
                    Log.e(TAG, "signup error: " + Arrays.toString(err.networkResponse.data));
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, err -> {
            err.printStackTrace();
        })
        {
            @Override
            public int getMethod() {
                return Method.PUT;
            }

            @Override
            public String getBodyContentType() {
                return "image/png";
            }
            @Override
            public byte[] getBody() {
                if (profilePic == null) return new byte[] {};
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    InputStream inputStream = context.getContentResolver().openInputStream(profilePic);
                    byte[] buf = new byte[1024];
                    int len;
                    while (true) {
                        assert inputStream != null;
                        if (!((len = inputStream.read(buf)) > 0)) break;
                        baos.write(buf, 0, len);
                    }
                    return baos.toByteArray();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Authorization", );
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                return headers;
//            }
        };

        queue.add(request);

    }
}

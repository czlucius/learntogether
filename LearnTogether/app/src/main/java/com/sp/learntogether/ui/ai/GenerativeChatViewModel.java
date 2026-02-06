package com.sp.learntogether.ui.ai;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.sp.learntogether.io.NetworkResources;
import com.sp.learntogether.models.Message;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GenerativeChatViewModel extends AndroidViewModel {

    private static final String TAG = "GenerativeChatViewModel";

    private MutableLiveData<ArrayList<Message>> messages = new MutableLiveData<>(new ArrayList<>());
    // List of maps
    /**
     * History of GPT responses.
     * JSON format follows (TS Interface notation)
     * <p></p>
     * <p>
     * {@code
     * [
     * {
     * "role": string,
     * "content": string
     * }
     * ]
     * }
     */
    private JSONArray history = null;

    public GenerativeChatViewModel(@NonNull Application application) {
        super(application);
    }

//    static class InternalMessage {
//        private String role;
//        private String content;
//
//        public InternalMessage(String role, String content) {
//            this.role = role;
//            this.content = content;
//        }
//
//        public String getRole() {
//            return role;
//        }
//
//        public String getContent() {
//            return content;
//        }
//    }

    public LiveData<ArrayList<Message>> getMessages() {
        return messages;
    }
    private RequestQueue queue = Volley.newRequestQueue(getApplication());

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    public void ask(String mdMessage) throws JSONException {
        Moshi moshi = new Moshi.Builder()
                .build();
        Type type = Types.newParameterizedType(List.class, Message.class);
//        JsonAdapter<List<Message>> adapter = moshi.adapter(type);
        JSONArray history;
        if (this.history != null) {
            history = this.history;
        } else {
            history = new JSONArray();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("history", history);
        jsonObject.put("request", mdMessage);
        Message mine = new Message(false, mdMessage, null);
        if (messages.getValue() != null) {
            messages.getValue().add(mine);
            messages.setValue(messages.getValue());
        }
        Log.i(TAG, "ask: Sending request: " + jsonObject.toString() + " to " + NetworkResources.URL + "gpt");

        Objects.requireNonNull(auth.getCurrentUser()).getIdToken(false).addOnSuccessListener(getTokenResult -> {
            String token = getTokenResult.getToken();
            Map<String, String> params = new HashMap<String, String>();

            params.put("Authorization", token);

            JsonObjectRequest request = new JsonObjectRequest
                    (Request.Method.POST, NetworkResources.URL + "gpt", jsonObject, response -> {
                    /*
                    {
                        response: message,
                        history: update
                    }
                     */

                        Log.i(TAG, "ask: GPT Response JSON " + response);

                        try {
                            String gptResponse = response.getString("response");
                            Log.i(TAG, "ask: GPT Response: " + gptResponse);
                            this.history = response.getJSONArray("history");
                            Message system = new Message(true, gptResponse, null);
                            if (messages.getValue() != null) {
                                messages.getValue().add(system);
                                messages.setValue(messages.getValue());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }, Throwable::printStackTrace) {

                //This is for Headers If You Needed
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    params.put("Content-Type", "application/json; charset=UTF-8");
                    return params;
                }


            };


            request.setRetryPolicy(new DefaultRetryPolicy(
                    60_000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            queue.add(request);
        });

    }
}

package com.sp.learntogether.ui.communities;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.sp.learntogether.R;
import com.sp.learntogether.astraDBHelper;
import com.sp.learntogether.databinding.FragmentPostScreenBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class postScreen extends Fragment {
    private FragmentPostScreenBinding binding;
    private RequestQueue queue;
    commentListAdapter adapter = null;
    private FirebaseAuth auth;
    ImageView authorPic;
    TextView authorName, authorDateTime, postDesc;
    GridView commentViews;
    private int volleyResponseStatus;
    FloatingActionButton submit;
    ArrayList<commentInfo> commentList;
    String authorUsername, postTime, authorPostDesc, postId;
    EditText writeComment;
    String username;
    String commentId;
    Bitmap profileImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        authorUsername = getArguments().getString("username");
        postTime = getArguments().getString("dateTime");
        authorPostDesc = getArguments().getString("forumBody");

        postId = getArguments().getString("postId");

        profileImage = getArguments().getParcelable("profileImage");

        binding = FragmentPostScreenBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        authorPic = binding.authorPic;
        authorPic.setImageBitmap(profileImage);

        authorName = binding.authorName;
        authorName.setText(authorUsername);

        authorDateTime = binding.authorDateTime;
        authorDateTime.setText(postTime);

        postDesc = binding.postViewDesc;
        postDesc.setText(authorPostDesc);

        submit = binding.submitFAB;

        commentViews = binding.commentList;
        commentList = new ArrayList<commentInfo>();
        adapter = new commentListAdapter(getContext(), commentList);
        commentViews.setAdapter(adapter);

        getAllComments();

        writeComment = binding.commentWrite;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entryComment = writeComment.getText().toString().trim();
                if(entryComment.isEmpty()){
                    Toast.makeText(getContext(), "Please enter in comment before posting it!", Toast.LENGTH_LONG).show();
                }
                else{
                    String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
                    auth = FirebaseAuth.getInstance();
                    String useremail = auth.getCurrentUser().getEmail();
                    Toast.makeText(getContext(), useremail, Toast.LENGTH_LONG).show();
                    commentId = UUID.randomUUID().toString();
                    getUsername(useremail, u -> {
                        insertComment(commentId, entryComment, currentDateTime, postId, u);
                    });
                }
            }
        });

        return root;
    }

    private void getAllComments(){
        queue = Volley.newRequestQueue(getContext());
        String url = astraDBHelper.commentsUrl + "rows";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (volleyResponseStatus == 200) {
                    try {
                        int count = response.getInt("count");
                        adapter.clear();
                        if (count > 0) {
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i <= count; i++) {
                                String postidentifier = data.getJSONObject(i).getString("postid");
                                if(postidentifier.equals(postId)){
                                    commentInfo comment = new commentInfo(data.getJSONObject(i).getString("username"),
                                                                            data.getJSONObject(i).getString("postid"),
                                                                            data.getJSONObject(i).getString("datetime"),
                                                                            data.getJSONObject(i).getString("commentdesc"));
                                    adapter.add(comment);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("OnErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders(){
                return astraDBHelper.getHeader();
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response){
                volleyResponseStatus = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void getUsername(String useremail, Consumer<String> callback){
        queue = Volley.newRequestQueue(getContext());
        String url = astraDBHelper.userDetailsUrl + "rows";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (volleyResponseStatus == 200) {
                    try {
                        int count = response.getInt("count");
                        if (count > 0) {
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i <= count; i++) {
                                String checkEmail = data.getJSONObject(i).getString("email");
                                if(useremail.equals(checkEmail)){
                                    username = data.getJSONObject(i).getString("username");
                                    callback.accept(username);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("OnErrorResponse", error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders(){
                return astraDBHelper.getHeader();
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response){
                volleyResponseStatus = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };
        queue.add(jsonObjectRequest);
    }

    private void insertComment(String commentIdentifier, String commentDesc, String datetime, String postidentifier, String username){
        Map<String, String> params = new HashMap<String, String>();
        params.put("commentid", commentIdentifier);
        params.put("commentdesc", commentDesc);
        params.put("datetime", datetime);
        params.put("postid", postidentifier);
        params.put("username", username);
        JSONObject postdata = new JSONObject(params); // Data as JSON object to be insert into the database
        RequestQueue queue = Volley.newRequestQueue(getContext());
        // Rest api link
        String url = astraDBHelper.commentsUrl;
        // Use POST REST api call
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postdata,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        adapter.add(new commentInfo(username, postidentifier, datetime, commentDesc));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("OnErrorResponse", Arrays.toString(error.networkResponse.data));
                        Toast.makeText(getContext(), "Error inserting post into Database", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                return astraDBHelper.getHeader();
            }
        };
        // add JsonObjectRequest to the RequestQueue
        queue.add(jsonObjectRequest);
    }
}
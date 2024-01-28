package com.sp.learntogether;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.sp.learntogether.databinding.FragmentAddForumPostBinding;
import com.sp.learntogether.ui.communities.Communities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class addPostFragment extends Fragment {
    private FragmentAddForumPostBinding binding;
    EditText postDesc;
    Spinner grpSpinner;
    Button submit;
    ArrayAdapter<CharSequence> adapter = null;
    private int volleyResponseStatus;
    private RequestQueue queue;
    private FirebaseAuth auth;
    private String username;
    private int postId;
    private String subject;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddForumPostBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        postDesc = binding.addForumDesc;
        grpSpinner = binding.addSelectSubj;
        submit = binding.savePostButton;

        adapter = ArrayAdapter.createFromResource(getContext(), R.array.communities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        grpSpinner.setAdapter(adapter);
        submit.setOnClickListener(onSave);
        return view;
    }

    private View.OnClickListener onSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String addPostDesc = postDesc.getText().toString().trim();
            if(addPostDesc.isEmpty()){
                Toast.makeText(getContext(), "Please enter text for your post!", Toast.LENGTH_LONG).show();
            } else{
                subject = grpSpinner.getSelectedItem().toString();
                auth = FirebaseAuth.getInstance();
                String useremail = auth.getCurrentUser().getEmail();
                String currentDateTime = DateFormat.getDateTimeInstance().format(new Date());
                getUserName(useremail);
                String insertId = String.valueOf(postId);
                getInsertID();
                Toast.makeText(getContext(), username, Toast.LENGTH_LONG).show();
                // TODO implement code to get profile pic here
                insertVolley(insertId, currentDateTime, addPostDesc, "testLink", subject, username);
            }
        }
    };

    private void getInsertID(){
        queue = Volley.newRequestQueue(getContext());
        String url = astraDBHelper.forumPostUrl + "rows";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (volleyResponseStatus == 200) {
                    try {
                        int count = response.getInt("count");
                        postId = count + 1;
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

    private void getUserName(String useremail){
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

    private void insertVolley(String Id, String dateTime, String forumDesc, String profileImg, String subject, String username) {
        // Create a JSON object from the parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", Id);
        params.put("datetime", dateTime);
        params.put("forumquestion", forumDesc);
        params.put("profileimg", profileImg);
        params.put("subject", subject);
        params.put("username", username);
        JSONObject postdata = new JSONObject(params); // Data as JSON object to be insert into the database
        RequestQueue queue = Volley.newRequestQueue(getContext());
        // Rest api link
        String url = astraDBHelper.forumPostUrl;
        // Use POST REST api call
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postdata,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Toast.makeText(getContext(), "Successfully created post in " + subject +" forum" , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("OnErrorResponse", error.toString());
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
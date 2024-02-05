package com.sp.learntogether.ui.communities;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sp.learntogether.R;
import com.sp.learntogether.astraDBHelper;
import com.sp.learntogether.databinding.FragmentForumPostsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class forumPosts extends Fragment implements recycler_interface{
    RecyclerView forumListings;
    private RequestQueue queue;
    List<forumPostInfo> forumList = null;
    forumListAdapter adapter = null;
    private int volleyResponseStatus;
    private FragmentForumPostsBinding binding;
    private String comType;
    private ImageView commImg;
    private TextView communityType;
    private NavController navCon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        comType = getArguments().getString("comType");
        // Inflate the layout for this fragment
        binding = FragmentForumPostsBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        forumListings = binding.forumRecycler;
        forumList = new ArrayList<forumPostInfo>();
        getAllSubjPosts();
        forumListings.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new forumListAdapter(getContext(), forumList, this::OnItemClick);
        forumListings.setAdapter(adapter);

        navCon = NavHostFragment.findNavController(this);

        commImg = binding.comImg;
        communityType = binding.comTypeGC;
        if(comType.equals("Mathematics")){
            commImg.setBackgroundResource(R.drawable.math);
            communityType.setText("Mathematics Forum");
        } else if(comType.equals("English")){
            commImg.setBackgroundResource(R.drawable.english);
            communityType.setText("English Forum");
        } else if(comType.equals("Science")){
            commImg.setBackgroundResource(R.drawable.science);
            communityType.setText("Science Forum");
        } else if(comType.equals("Social Studies")){
            commImg.setBackgroundResource(R.drawable.socialstudies);
            communityType.setText("Social Studies Forum");
        } else if(comType.equals("History")){
            commImg.setBackgroundResource(R.drawable.history);
            communityType.setText("History Forum");
        } else if(comType.equals("Geography")){
            commImg.setBackgroundResource(R.drawable.geography);
            communityType.setText("Geography Forum");
        } else if(comType.equals("Piano")){
            commImg.setBackgroundResource(R.drawable.piano);
            communityType.setText("Piano Forum");
        } else if(comType.equals("Violin")){
            commImg.setBackgroundResource(R.drawable.violin);
            communityType.setText("Violin Forum");
        } else if(comType.equals("Planting")){
            commImg.setBackgroundResource(R.drawable.planting);
            communityType.setText("Planting Forum");
        }
        else{
            commImg.setBackgroundResource(R.drawable.notfound);
            communityType.setText(comType + " Forum");
        }
        return view;
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        forumList.clear();
        binding = null;
    }
    private void getAllSubjPosts(){
        queue = Volley.newRequestQueue(getContext());
        String url = astraDBHelper.forumPostUrl + "rows";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (volleyResponseStatus == 200) {
                    try {
                        int count = response.getInt("count");
                        forumList.clear();
                        if (count > 0) {
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i <= count; i++) {
                                String type = data.getJSONObject(i).getString("subject");
                                if(type.equals(comType)) {
                                    forumPostInfo info = new forumPostInfo(data.getJSONObject(i).getString("username"), data.getJSONObject(i).getString("id"), data.getJSONObject(i).getString("profileimg"), data.getJSONObject(i).getString("forumquestion"), data.getJSONObject(i).getString("datetime"));
                                    forumList.add(info);
                                    adapter.notifyItemInserted(forumList.size() - 1);
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

    @Override
    public void OnItemClick(int position){
        Bundle bundle = new Bundle();
        String postId = forumList.get(position).getId();
        String username = forumList.get(position).getName();
        Bitmap profileImage = forumList.get(position).getProfileImage();
        String forumBody = forumList.get(position).getForumQuestion();
        String dateTime = forumList.get(position).getCurrentDateTime();
        bundle.putString("postId", postId);
        bundle.putString("username", username);
        bundle.putParcelable("profileImage", profileImage);
        bundle.putString("forumBody", forumBody);
        bundle.putString("dateTime", dateTime);
        navCon.navigate(R.id.postScreen, bundle);
    }
}
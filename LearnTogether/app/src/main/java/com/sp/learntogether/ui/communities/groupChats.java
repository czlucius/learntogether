package com.sp.learntogether.ui.communities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sp.learntogether.R;
import com.sp.learntogether.astraDBHelper;
import com.sp.learntogether.databinding.FragmentGroupChatsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class groupChats extends Fragment {
    RecyclerView grpListings;
    private RequestQueue queue;
    List<groupchatInfo> gcInfoList = null;
    gcListAdapter adapter = null;
    private int volleyResponseStatus;
    private FragmentGroupChatsBinding binding;
    private String comType;
    private ImageView commImg;
    private TextView communityType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        comType = getArguments().getString("comType");
        // Inflate the layout for this fragment
        binding = FragmentGroupChatsBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        grpListings = binding.grpList;
        gcInfoList = new ArrayList<groupchatInfo>();
        getAllSubjGC();
        grpListings.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new gcListAdapter(getContext(),gcInfoList);
        grpListings.setAdapter(adapter);

        commImg = binding.comImg;
        communityType = binding.comTypeGC;
        if(comType.equals("Mathematics")){
            commImg.setBackgroundResource(R.drawable.math);
            communityType.setText("Mathematics Communities");
        } else if(comType.equals("English")){
            commImg.setBackgroundResource(R.drawable.english);
            communityType.setText("English Communities");
        } else if(comType.equals("Science")){
            commImg.setBackgroundResource(R.drawable.science);
            communityType.setText("Science Communities");
        } else if(comType.equals("Social Studies")){
            commImg.setBackgroundResource(R.drawable.socialstudies);
            communityType.setText("Social Studies Communities");
        } else if(comType.equals("History")){
            commImg.setBackgroundResource(R.drawable.history);
            communityType.setText("History Communities");
        } else if(comType.equals("Geography")){
            commImg.setBackgroundResource(R.drawable.geography);
            communityType.setText("Geography Communities");
        } else if(comType.equals("Piano")){
            commImg.setBackgroundResource(R.drawable.piano);
            communityType.setText("Piano Communities");
        } else if(comType.equals("Violin")){
            commImg.setBackgroundResource(R.drawable.violin);
            communityType.setText("Violin Communities");
        } else if(comType.equals("Planting")){
            commImg.setBackgroundResource(R.drawable.planting);
            communityType.setText("Planting Communities");
        }
        else{
            commImg.setBackgroundResource(R.drawable.notfound);
            communityType.setText(comType + " Communities");
        }
        return view;
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        gcInfoList.clear();
        binding = null;
    }
    private void getAllSubjGC(){
        queue = Volley.newRequestQueue(getContext());
        String url = astraDBHelper.groupChatsUrl + "rows";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (volleyResponseStatus == 200) {
                    try {
                        int count = response.getInt("count");
                        gcInfoList.clear();
                        if (count > 0) {
                            JSONArray data = response.getJSONArray("data");
                            for (int i = 0; i <= count; i++) {
                                String type = data.getJSONObject(i).getString("subject");
                                if(type.equals(comType)) {
                                    groupchatInfo info = new groupchatInfo(data.getJSONObject(i).getString("groupname"), data.getJSONObject(i).getString("description"), String.valueOf(data.getJSONObject(i).getInt("capacity")));
                                    gcInfoList.add(info);
                                    adapter.notifyItemInserted(gcInfoList.size() - 1);
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
}
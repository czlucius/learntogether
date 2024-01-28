package com.sp.learntogether.ui.communities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.sp.learntogether.astraDBHelper;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.sp.learntogether.R;
import com.sp.learntogether.databinding.FragmentCommunitiesBinding;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import com.sp.learntogether.ui.LoginFragment;

public class CommunitiesFragment extends Fragment {
    GridView communityGV;
    communitiesGVadapter adapter = null;
    private FirebaseAuth auth;
    private FragmentCommunitiesBinding binding;
    private RequestQueue queue;
    private int volleyResponseStatus;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CommunitiesViewModel communitiesViewModel =
                new ViewModelProvider(this).get(CommunitiesViewModel.class);

        setHasOptionsMenu(true);

        binding = FragmentCommunitiesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        communityGV = binding.Communities;
        ArrayList<Communities> communitiesModelArrayList = new ArrayList<Communities>();
        adapter = new communitiesGVadapter(getContext(), communitiesModelArrayList);
        communityGV.setAdapter(adapter);
        communityGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String comType = adapter.getItem(position).getCommunity_type();
                //Toast.makeText(getContext(), "You clicked: "+type, Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                bundle.putString("comType", comType);
                Navigation.findNavController(view).navigate(R.id.forumPosts, bundle);
            }
        });
        getAllVolley();

        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.communities_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.createGC){
            NavHostFragment.findNavController(this).navigate(R.id.action_communities_fragment_to_addGCFragment);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    private void getAllVolley(){
        queue = Volley.newRequestQueue(getContext());
        String url = astraDBHelper.communitiesUrl + "rows";
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
                                Communities community = new Communities(data.getJSONObject(i).getString("community"));
                                adapter.add(community);
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
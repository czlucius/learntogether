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
import com.sp.learntogether.databinding.FragmentAddGCBinding;
import com.sp.learntogether.ui.communities.groupchatInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addGCFragment extends Fragment {
    private FragmentAddGCBinding binding;
    EditText grpName;
    EditText grpDesc;
    Spinner grpSpinner;
    Button submit;
    ArrayAdapter<CharSequence> adapter = null;
    private int volleyResponseStatus;
    private RequestQueue queue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddGCBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        grpName = binding.addGroupName;
        grpDesc = binding.addGroupDesc;
        grpSpinner = binding.addSelectSubj;
        submit = binding.saveGcButton;

        adapter = ArrayAdapter.createFromResource(getContext(), R.array.communities_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        grpSpinner.setAdapter(adapter);
        submit.setOnClickListener(onSave);
        return view;
    }

    private View.OnClickListener onSave = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String gcName = grpName.getText().toString().trim();
            String gcDesc = grpDesc.getText().toString().trim();
            if(gcName.isEmpty() || gcDesc.isEmpty()){
                Toast.makeText(getContext(), "Please enter text for name and description!", Toast.LENGTH_LONG).show();
            } else{
                String subject = grpSpinner.getSelectedItem().toString();
                insertVolley(gcName, gcDesc, subject, "1");
            }
        }
    };

    private void insertVolley(String gcName, String gcDesc, String subject, String capacity) {
        // Create a JSON object from the parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("groupname", gcName);
        params.put("description", gcDesc);
        params.put("subject", subject);
        params.put("capacity", capacity);
        JSONObject postdata = new JSONObject(params); // Data as JSON object to be insert into the database
        RequestQueue queue = Volley.newRequestQueue(getContext());
        // Rest api link
        String url = astraDBHelper.groupChatsUrl;
        // Use POST REST api call
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, postdata,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(), "Successfully inserted into Database", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.e("OnErrorResponse", error.toString());
                        Toast.makeText(getContext(), "Error inserting into Database", Toast.LENGTH_LONG).show();
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
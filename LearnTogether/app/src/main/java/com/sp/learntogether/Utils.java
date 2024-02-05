package com.sp.learntogether;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String FIREBASE_RTDB_URL = "https://learntogether-sg-default-rtdb.asia-southeast1.firebasedatabase.app/";
    public static String[] strArrFromJsonArray(JSONArray jsa) throws JSONException {
        if (jsa == null) return null;
        String[] arr = new String[jsa.length()];
        for (int i=0; i<jsa.length(); i++) {
            arr[i] = jsa.getString(i);
        }
        return arr;
    }



//    public <T> List<String> jsonArrayToList(JSONArray array) throws JSONException {
//
//    }
}

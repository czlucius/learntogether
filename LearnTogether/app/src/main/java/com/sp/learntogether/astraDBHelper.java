package com.sp.learntogether;
import java.util.HashMap;

public class astraDBHelper {
    static String community_name;
    static String imageUri;
    static String region = "asia-south1";
    public static String communitiesUrl = region + "/v2/keyspaces/learntogether/communities";
    public static int lastID = 0;
    static String cassandra_token = "AstraCS:sSNCsjSFLnzoFoguSDZUdKNy:77be8150aa2ec570810a0435c29d20720133e3c1bd39e04a8621392fa7c71ee9";
    public static HashMap getHeader(){
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Cassandra-Token", cassandra_token);
        headers.put("Accept", "application/json");
        return headers;
    }

}

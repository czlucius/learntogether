package com.sp.learntogether;
import java.util.HashMap;

public class astraDBHelper {
    static String community_name;
    static String imageUri;
    static String region = "https://6f3d98cf-c4b5-4fc2-b02c-65a85427b886-asia-south1.apps.astra.datastax.com/api/rest";
    public static String communitiesUrl = region + "/v2/keyspaces/learntogether/communities/";
    //public static String groupChatsUrl = region + "/v2/keyspaces/learntogether/groupchats/";
    public static String forumPostUrl = region + "/v2/keyspaces/learntogether/forumpost/";
    public static String userDetailsUrl = region + "/v2/keyspaces/learntogether/userdetails/";
    public static String commentsUrl = region + "/v2/keyspaces/learntogether/comments/";
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
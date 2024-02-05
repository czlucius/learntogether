package com.sp.learntogether;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sp.learntogether.data.AppDatabase;
import com.sp.learntogether.data.TrackingDao;
import com.sp.learntogether.io.DatabaseInteractor;
import com.sp.learntogether.models.Meetup;
import com.sp.learntogether.models.Track;
import com.sp.learntogether.objects.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class MeetupsViewModel extends AndroidViewModel {


    private FirebaseAuth auth;
    private DatabaseInteractor dbIO;
    private TrackingDao trackingDao;
    private FirebaseDatabase database = FirebaseDatabase.getInstance(Utils.FIREBASE_RTDB_URL);
    private DatabaseReference ref = database.getReference("meetups");

    public MeetupsViewModel(@NonNull Application application) {
        super(application);
        dbIO = DatabaseInteractor.getInstance(application);
        auth = FirebaseAuth.getInstance();
        trackingDao = AppDatabase.getInstance(application).trackingDao();
        loadData();
    }
    private MutableLiveData<List<String>> meetups = new MutableLiveData<>();

    public LiveData<List<String >> getMeetups() {
        return meetups;
    }




    private static final String TAG = "MeetupsViewModel";
    public void getMeetupInitiatorLocation(String meetupId, Consumer<Location> locationConsumer) {
        Log.i(TAG, "getMeetupInitiatorLocation: " + meetupId);
        new Thread(() -> {
            database.getReference().child("meetups").child(meetupId).child("initiatorLocation")
                    .get()
                    .addOnSuccessListener(dataSnapshot -> {
                        Location location = dataSnapshot.getValue(Location.class);
                        locationConsumer.accept(location);
                    }).addOnFailureListener(e -> {
                        e.printStackTrace();
                    })
                    .addOnCompleteListener(task -> {
                        Log.i(TAG, "getMeetupInitiatorLocation: " + task.getResult() + " " + task.getException());
                    });
        }).start();
    }
    public void loadData() {
        dbIO.get(response -> {
            int count;
            try {
                count = response.getInt("count");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            if (count > 0) {
                JSONArray data;
                ArrayList<String> retrieved = new ArrayList<>();
                try {
                    Log.i(TAG, "loadData: " + response.toString());
                    data = response.getJSONArray("data");
                    for (int i=0;i<data.length();i++) {
                        JSONObject obj = data.getJSONObject(i);

                        String mId = obj.getString("id");
                        retrieved.add(mId);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                meetups.setValue(retrieved);
            }

        }, astraDBHelper.meetupsUrl + "rows?fields=id");
    }

    private SharedPreferences prefs = getApplication().getSharedPreferences("meetups", Context.MODE_PRIVATE);


    public void addMeetupToTrackingPrefs(Meetup meetup) {
        new Thread(() -> {
            Log.i(TAG, "addMeetupToTrackingPrefs " );
            trackingDao.insertOne(new Track(
                    meetup.getId(), meetup.getMeetTime(), meetup.getLatitude(), meetup.getLongitide()
            ));
        }).start();



    }


    public void getTrackableMeetups(Consumer<List<Track>> consumer) {

        new Thread(() -> {
            consumer.accept(trackingDao.getFuture(new Date().getTime()));

        }).start();
    }
}

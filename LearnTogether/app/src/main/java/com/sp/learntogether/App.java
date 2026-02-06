package com.sp.learntogether;

import android.Manifest;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.sp.learntogether.data.AppDatabase;
import com.sp.learntogether.data.TrackingDao;
import com.sp.learntogether.models.Track;

import java.util.Date;
import java.util.List;

public class App extends Application {
    private static final String TAG = "App";
    public static String PACKAGE_NAME;
    private static TrackingDao trackingDao;

    public static Context appCtx;
    private static GeofencingClient geofencingClient;


    public static String[] PERMISSIONS;
    /**
     * Permission to check to see if there is background location permissions.
     * Reason: most Android devices have this characteristic (via system settings) that if ACCESS_BACKGROUND_LOCATION is granted, ACCESS_FINE_LOCATION will have been granted in the first place.
     * Unless the user disables fine location, but even then we can get an approximate coordinate.
     * If below Android Q we just have to check for fine location permissions.
     * So the determination of location in a background environment is determinant on this.
     */
    public static String BACKGND_LOC_PM;


    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            PERMISSIONS = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION};
            BACKGND_LOC_PM = Manifest.permission.ACCESS_BACKGROUND_LOCATION;
        } else {
            PERMISSIONS = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
            BACKGND_LOC_PM = Manifest.permission.ACCESS_FINE_LOCATION;
        }
    }



    @Override
    public void onCreate() {
        super.onCreate();
        appCtx = getApplicationContext();
        Log.i(TAG, "onCreate");
        FirebaseApp.initializeApp(this);
        PACKAGE_NAME = getPackageName();

        geofencingClient = LocationServices.getGeofencingClient(this);

        createChannel(
                R.string.meetups,
                R.string.meetups_desc,
                NotificationCompat.PRIORITY_HIGH,
                "meetup"
        );

        createChannel(
                R.string.location,
                R.string.update_your_location_for_meetups,
                NotificationCompat.PRIORITY_HIGH,
                "location"
        );
        trackingDao = AppDatabase.getInstance(this).trackingDao();




    }



    private static PendingIntent geofencePendingIntent;
    private static PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(appCtx, GeofencingReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        geofencePendingIntent = PendingIntent.getBroadcast(appCtx, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return geofencePendingIntent;

    }


    private static void initGeoFence(String id, LatLng latLng, long meetupTime) throws SecurityException {

        Geofence geofence = new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(latLng.latitude, latLng.longitude, 100.0F)
                .setExpirationDuration(meetupTime - new Date().getTime()) // Meetup time should be in the future.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        GeofencingRequest req = new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .build();
        geofencingClient.addGeofences(req, getGeofencePendingIntent())
                .addOnSuccessListener(_s -> {

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(appCtx, "Failed to add geofence", Toast.LENGTH_SHORT).show();
                });

    }



    public static void startTracking() {
        new Thread(() -> {
            List<Track> future = trackingDao.getFuture(new Date().getTime());
            if (future.size() > 0) {
                Intent intent = new Intent(appCtx, GLocService.class);
                appCtx.startService(intent);

                for (Track track : future) {
                    initGeoFence(
                            track.getMeetupUid(),
                            new LatLng(track.getLat(), track.getLng()),
                            track.getTimestmp()
                    );
                }

            }

        }).start();
    }


    private void createChannel(@StringRes int nameRes, @StringRes int descRes, int importance, String channelId ) {
        // Create the NotificationChannel.
        String name = getString(nameRes);
        String  descriptionText = getString(descRes);

        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(descriptionText);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE) ;
        notificationManager.createNotificationChannel(channel);
        // no-op if not >= O
    }
}

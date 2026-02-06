package com.sp.learntogether;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class GeofencingReceiver extends BroadcastReceiver {
    private FirebaseDatabase database = FirebaseDatabase.getInstance(Utils.FIREBASE_RTDB_URL);
    private DatabaseReference ref = database.getReference("meetups");
    private static final String TAG = "GeofencingReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent ev = GeofencingEvent.fromIntent(intent);
        if (ev == null) {
            throw new RuntimeException("GeofencingEvent is null!");
        }
        if (ev.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(ev.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }
        int geofenceTransition = ev.getGeofenceTransition();
        List<Geofence> geofences = Objects.requireNonNull(ev.getTriggeringGeofences());
        boolean inFence;
        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                inFence = true;
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                inFence = false;
                break;
            default:
                throw new RuntimeException("Unknown geofencing event!");
        }
        for (Geofence geofence : geofences) {
            ref.child(geofence.getRequestId()).child("nearby")
                            .setValue(inFence);
        }
    }
}

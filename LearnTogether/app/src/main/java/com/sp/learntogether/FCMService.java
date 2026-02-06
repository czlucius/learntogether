package com.sp.learntogether;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sp.learntogether.objects.NotificationAction;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.i(TAG, "onMessageReceived: message received");
        Map<String, String> payload = message.getData();
        String action = payload.get("action");
        for (NotificationAction a: NotificationAction.values()) {
            if (a.identifier.equals(action)) {
                a.performAction(getApplicationContext(), payload);
            }
        }
    }
}

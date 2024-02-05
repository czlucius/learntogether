package com.sp.learntogether.objects;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.sp.learntogether.R;

import java.util.Map;
import java.util.Random;

public enum NotificationAction {
    /**
     * Meetup request notification.
     * Payload for such notifications shall be:
     * <pre>
     * {
     *    "action": "MEETUP_REQUEST",
     *    "meetup_id": [id here],
     *    "initiator_name": [name here],
     *    "latitude": [lat here],
     *    "longitude": [lng here]
     * }
     * </pre>
     * All notifications must be sent with priority HIGH.
     */
    MEETUP_REQUEST("MEETUP_REQUEST") {
        @Override
        public void performAction(Context context, Map<String, String> payload) {
            // Push a notification asking for meetup
            new Thread(() -> {
                String initiator = payload.get("initiator_name");
                double lat = Double.parseDouble(payload.get("latitude") != null ? payload.get("latitude") : "0.0");
                double lng = Double.parseDouble(payload.get("longitude") != null ? payload.get("longitude") : "0.0");
                String meetupId = payload.get("meetup_id");
                String displayText = "You have a meetup request from " + initiator + " at (" + lat + ", " + lng + ")";

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "meetup")
                        .setSmallIcon(R.drawable.baseline_people_24)
                        .setContentTitle(context.getString(R.string.meetup_request))
                        .setContentText(displayText)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                int notificationId = new Random().nextInt();

                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                Intent intent = new Intent(context, ActionsReceiver.class);
                intent.putExtra("notificationId", notificationId);

                Intent acceptIntent = new Intent(intent);
                acceptIntent.putExtra("action", identifier);
                acceptIntent.putExtra("meetupId", meetupId);

                int broadcastIdAccept = new Random().nextInt();
                PendingIntent acceptPendingIntent =
                        PendingIntent.getBroadcast(
                                context,
                                broadcastIdAccept,
                                acceptIntent,
                                PendingIntent.FLAG_MUTABLE
                        );


                Intent denyIntent = new Intent(intent);
                int broadcastIdDeny = new Random().nextInt();

                denyIntent.putExtra("action", "dismiss");
                PendingIntent denyPendingIntent =
                        PendingIntent.getBroadcast(context, broadcastIdDeny, denyIntent, PendingIntent.FLAG_MUTABLE);


                NotificationCompat.Action acceptAction = new NotificationCompat.Action(null, context.getString(R.string.accept), acceptPendingIntent);
                NotificationCompat.Action denyAction = new NotificationCompat.Action(null, context.getString(R.string.deny), denyPendingIntent);

                builder.addAction(acceptAction);
                builder.addAction(denyAction);

                NotificationManagerCompat.from(context).notify(notificationId, builder.build());
                return;


            }).start();
        }
    }

    ;
    public final String identifier;
    NotificationAction(String identifier) {
        this.identifier = identifier;
    }

    public abstract void performAction(Context context, Map<String, String> payload);



}

package com.kalabhedia.gimme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by What's That Lambda on 11/6/17.
 */

public class MessageRecieverService extends FirebaseMessagingService {
    private static final int REQUEST_CODE = 1;
    private static final int NOTIFICATION_ID = 6578;

    public MessageRecieverService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.w("onMessageReceived: ", remoteMessage.getData().get("title"));
        final String title = remoteMessage.getData().get("title");
        final String message = remoteMessage.getData().get("body");

        showNotifications(title, message);
    }

    private void showNotifications(String title, String msg) {
        Intent i = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE,
                i, PendingIntent.FLAG_UPDATE_CURRENT);

        String CHANNEL_ID = "channel_money_request";// The id of the channel.
        CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
            Notification notification1 = new Notification.Builder(this, CHANNEL_ID)
                    .setContentText(msg)
                    .setContentTitle(title)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .build();
            mNotificationManager.notify(0, notification1);
        } else {
            Notification notification = new Notification.Builder(this)
                    .setContentText(msg)
                    .setContentTitle(title)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setAutoCancel(true)
                    .build();
            mNotificationManager.notify(0, notification);
        }
    }
}


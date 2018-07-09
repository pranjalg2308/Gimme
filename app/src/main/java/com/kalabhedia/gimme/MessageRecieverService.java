package com.kalabhedia.gimme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;


public class MessageRecieverService extends FirebaseMessagingService {
    private static final int REQUEST_CODE = 1;
    private static final int NOTIFICATION_ID = 6578;
    DataBaseHelper db;

    public MessageRecieverService() {
        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.w("onMessageReceived: ", remoteMessage.getData().get("title"));
        final String title = remoteMessage.getData().get("title");
        String messageReceived = remoteMessage.getData().get("body");
        int location = messageReceived.indexOf("for");
        String reason = "";
        if (location != -1) {
            reason = messageReceived.substring(location);
        }
        messageReceived =messageReceived.substring(0, location);
        String phoneNumber = "";
        String message = "";
        String[] checkingPhoneNumber = messageReceived.split(" ");
        int i;
        for (i = checkingPhoneNumber.length - 1; i > 0; i--) {
            if (checkingPhoneNumber[i].charAt(0) == '+') {
                phoneNumber = checkingPhoneNumber[i] + phoneNumber;
                break;
            } else {
                phoneNumber = checkingPhoneNumber[i] + phoneNumber;
            }
        }


        int j;
        for (j = 0; j < i; j++) {
            message += checkingPhoneNumber[j] + " ";
        }

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Gimme", Context.MODE_PRIVATE);
        String name = sharedPreferences.getString(phoneNumber, null);
        if (name == null) {
            message += " " + phoneNumber;
        } else {
            message += " " + name;
        }
        message=message+" "+reason;
        showNotifications(title, message, phoneNumber);
    }

    private void showNotifications(String title, String msg, String phoneNumber) {
        Intent i = new Intent(this, MainActivity.class);
        int uniqueId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        String moneyString = msg.split(" ")[0];
        db = new DataBaseHelper(this);
        db.getWritableDatabase();
        Boolean result = db.insertData(phoneNumber, "", moneyString);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE,
                i, PendingIntent.FLAG_UPDATE_CURRENT);

        String CHANNEL_ID = "channel_money_request";// The id of the channel.
        CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.accept, "Previous", pendingIntent).build();
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
            Notification notification1 = new Notification.Builder(this, CHANNEL_ID)
                    .setContentText(msg)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentTitle(title)
                    .setContentIntent(pendingIntent)
                    .addAction(R.drawable.accept, "Decline", pendingIntent)
                    .addAction(R.drawable.decline, "Accept", pendingIntent)
                    .setSmallIcon(R.drawable.notif_icon)
                    .setAutoCancel(true)
                    .build();
            mNotificationManager.notify(uniqueId, notification1);
        } else {
            Notification notification = new Notification.Builder(this)
                    .setContentText(msg)
                    .setContentTitle(title)
                    .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.notif_icon)
                    .setAutoCancel(true)
                    .addAction(R.drawable.accept, "Accept", pendingIntent)
                    .addAction(R.drawable.decline, "Decline", pendingIntent)
                    .build();
            mNotificationManager.notify(uniqueId, notification);
        }
    }
}


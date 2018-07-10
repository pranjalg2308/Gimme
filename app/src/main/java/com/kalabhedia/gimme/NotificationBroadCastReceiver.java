package com.kalabhedia.gimme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

public class NotificationBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "BroadCastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String s = intent.getStringExtra("Button clicked");
        int notificationId = intent.getIntExtra("notificationID", 0);
        Toast.makeText(context, "Notification " + s + " Button clicked", Toast.LENGTH_SHORT).show();
        NotificationManagerCompat.from(context).cancel(notificationId);
    }
}

package com.kalabhedia.gimme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

public class NotificationBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "BroadCastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String s = intent.getStringExtra("Button clicked");
        DataBaseHelper db = new DataBaseHelper(context);
        String timeStamp = intent.getStringExtra("TimeStamp");
        if (s.equals("accept")) {
            db.updateData(timeStamp, "1", "1");
            if (!MessageRecieverService.isAppSentToBackground(context)) {
                Intent gcm_rec = new Intent("your_action");
                LocalBroadcastManager.getInstance(context).sendBroadcast(gcm_rec);
            }
        }
        if (s.equals("declined")) {
            db.updateData(timeStamp, "1", "2");
            if (!MessageRecieverService.isAppSentToBackground(context)) {
                Intent gcm_rec = new Intent("your_action");
                LocalBroadcastManager.getInstance(context).sendBroadcast(gcm_rec);
            }
        }
        int notificationId = intent.getIntExtra("notificationID", 0);
        NotificationManagerCompat.from(context).cancel(notificationId);

    }
}

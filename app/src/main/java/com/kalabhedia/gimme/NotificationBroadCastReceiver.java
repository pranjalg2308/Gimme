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
        String receiverKey = intent.getStringExtra("receiverKey");
        String senderKey = intent.getStringExtra("senderkey");
        String code = intent.getStringExtra("code");
        int notificationId = intent.getIntExtra("notificationID", 0);
        NotificationManagerCompat.from(context).cancel(notificationId);
        if (code.equals("11") || code.equals("21")) {
            if (s.equals("accept")) {
                db.updateData(timeStamp, "1", "1");
                AddingNewContactFragment.sendNotificationToUser(timeStamp, senderKey, receiverKey, "0", "0", " ", "11");
                if (!MessageRecieverService.isAppSentToBackground(context)) {
                    Intent gcm_rec = new Intent("your_action");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(gcm_rec);
                }
            }
            if (s.equals("declined")) {
                db.updateData(timeStamp, "2", "1");
                AddingNewContactFragment.sendNotificationToUser(timeStamp, senderKey, receiverKey, "0", "0", " ", "12");
                if (!MessageRecieverService.isAppSentToBackground(context)) {
                    Intent gcm_rec = new Intent("your_action");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(gcm_rec);
                }
            }
        } else {
            if (s.equals("accept")) {
                db.updateData(timeStamp, "3", "3");
                AddingNewContactFragment.sendNotificationToUser(timeStamp, senderKey, receiverKey, "0", "0", " ", "33");
                if (!MessageRecieverService.isAppSentToBackground(context)) {
                    Intent gcm_rec = new Intent("your_action");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(gcm_rec);
                }
            }
            if (s.equals("declined")) {
                db.updateData(timeStamp, "3", "2");
                AddingNewContactFragment.sendNotificationToUser(timeStamp, senderKey, receiverKey, "0", "0", " ", "23");
                if (!MessageRecieverService.isAppSentToBackground(context)) {
                    Intent gcm_rec = new Intent("your_action");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(gcm_rec);
                }
            }
        }

    }

}

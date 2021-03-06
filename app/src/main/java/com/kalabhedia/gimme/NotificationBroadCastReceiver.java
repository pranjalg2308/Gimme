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
        String phoneNumber = intent.getStringExtra("phonenumber");
        String code = intent.getStringExtra("code");
        String myNumber = intent.getStringExtra("my_number");
        int notificationId = intent.getIntExtra("notificationID", 0);
        NotificationManagerCompat.from(context).cancel(notificationId);
        if (code.equals("11") || code.equals("21") || code.equals("01")) {
            if (s.equals("accept")) {
                db.updateData(timeStamp, "1", "1");
                sendInBackground(timeStamp, senderKey, receiverKey, "11", "0");
                if (!MessageRecieverService.isAppSentToBackground(context)) {
                    Intent gcm_rec = new Intent("your_action");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(gcm_rec);
                }
            }
            if (s.equals("declined")) {
                db.updateData(timeStamp, "2", "1");
                sendInBackground(timeStamp, senderKey, receiverKey, "12", "0");
                if (!MessageRecieverService.isAppSentToBackground(context)) {
                    Intent gcm_rec = new Intent("your_action");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(gcm_rec);
                }
            }
        } else {
            if (s.equals("accept")) {
                db.updateData(timeStamp, "1", "3");
                sendInBackground(timeStamp, senderKey, receiverKey, "31", myNumber);
                HistoryDataBaseHelper dbHistory;
                dbHistory = new HistoryDataBaseHelper(context);
                dbHistory.getWritableDatabase();
                dbHistory.insertData(timeStamp, phoneNumber, (db.getVerifiedSum(phoneNumber)) + "");
                db.deleteUserData(phoneNumber);
                if (!MessageRecieverService.isAppSentToBackground(context)) {
                    Intent gcm_rec = new Intent("your_action");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(gcm_rec);
                }
            }
            if (s.equals("declined")) {
                db.updateData(timeStamp, "2", "3");
                sendInBackground(timeStamp, senderKey, receiverKey, "32", myNumber);
                if (!MessageRecieverService.isAppSentToBackground(context)) {
                    Intent gcm_rec = new Intent("your_action");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(gcm_rec);
                }
            }
        }

    }

    public void sendInBackground(String timeStamp, String senderKey, String receiverKey, String code, String myNumber) {
        new Thread(new Runnable() {
            public void run() {
                AddingNewContactFragment.sendNotificationToUser(timeStamp, senderKey, receiverKey, myNumber, "0", " ", code);
            }
        }).start();
    }

}

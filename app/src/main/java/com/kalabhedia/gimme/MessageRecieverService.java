package com.kalabhedia.gimme;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;


public class MessageRecieverService extends FirebaseMessagingService {
    private static final int REQUEST_CODE = 1;
    private DataBaseHelper db;
    private HistoryDataBaseHelper dbHistory;
    private static String id = "";

    public static String getId() {
        return id;
    }

    public MessageRecieverService() {
        super();
    }


    public static boolean isAppSentToBackground(final Context context) {

        try {
            ActivityManager am = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            // The first in the list of RunningTasks is always the foreground
            // task.
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            String foregroundTaskPackageName = foregroundTaskInfo.topActivity
                    .getPackageName();// get the top fore ground activity
            PackageManager pm = context.getPackageManager();
            PackageInfo foregroundAppPackageInfo = pm.getPackageInfo(
                    foregroundTaskPackageName, 0);

            String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo
                    .loadLabel(pm).toString();

            if (!foregroundTaskAppName.equals("Gimme")) {
                return true;
            }
        } catch (Exception e) {
            Log.e("isAppSentToBackground", "" + e);
        }
        return false;
    }


    void DeletionFromRealtimeDatabase(String receiverUserID, String senderUserID, String notificationid) {
        FirebaseDatabase.getInstance().goOnline();
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Notifications").child(receiverUserID);
        ref.keepSynced(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    if (appleSnapshot.getKey().equals(notificationid))
                        appleSnapshot.getRef().removeValue();
                    Log.w("Notification: ", "Data deleted");
                }
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(receiverUserID);
                reference.keepSynced(true);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            if (appleSnapshot.getKey().equals(notificationid))
                                appleSnapshot.getRef().removeValue();
                            Log.w("Notification: ", "Data deleted");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(receiverUserID);
                databaseReference.keepSynced(true);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            if (appleSnapshot.getKey().equals(notificationid))
                                appleSnapshot.getRef().removeValue();
                            Log.w("Notification: ", "Data deleted");
                        }


                        DatabaseReference dataReference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(receiverUserID);
                        dataReference.keepSynced(true);
                        dataReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                    if (appleSnapshot.getKey().equals(notificationid))
                                        appleSnapshot.getRef().removeValue();
                                    Log.w("Notification: ", "Data deleted");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Notification", "onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * @param title
     * @param msg
     * @param phoneNumber
     * @param timeStamp
     * @param reason
     * @param receiverKey
     * @param senderKey
     * @param code
     */
    private void showNotifications(String title, String msg, String phoneNumber, String timeStamp,
                                   String reason, String receiverKey, String senderKey, String code) {
        int uniqueId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        id += uniqueId + " ";
        OnlineUserDataBase onlineUserDataBase = new OnlineUserDataBase(getApplicationContext());
        onlineUserDataBase.insertData(phoneNumber, receiverKey, 0);
        if (code.equals("03")) {
            String message;
            SharedPreferences Pref = getApplicationContext().getSharedPreferences("Data", Context.MODE_PRIVATE);
            String myNumber = Pref.getString("phonenumber", null);
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Gimme", Context.MODE_PRIVATE);
            String name = sharedPreferences.getString(phoneNumber, null);
            db = new DataBaseHelper(this);
            db.getWritableDatabase();

            String moneyString = msg.split(" ")[0];
            Boolean result = db.insertData(timeStamp, phoneNumber, reason, moneyString, code.charAt(0) + "", code.charAt(1) + "");
            if (name == null) {
                message = phoneNumber + " Claims for Settle up";
            } else {
                message = name + " Claims for Settle up";
            }
            Intent i = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE,
                    i, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent intent = new Intent(this, NotificationBroadCastReceiver.class);
            intent.putExtra("Button clicked", "accept");
            intent.putExtra("notificationID", uniqueId);
            intent.putExtra("TimeStamp", timeStamp);
            intent.putExtra("receiverKey", receiverKey);
            intent.putExtra("senderkey", senderKey);
            intent.putExtra("my_number", myNumber);
            intent.putExtra("code", code);
            intent.putExtra("phonenumber", phoneNumber);
            PendingIntent accept = PendingIntent.getBroadcast(this, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            intent.putExtra("Button clicked", "declined");
            PendingIntent decline = PendingIntent.getBroadcast(this, uniqueId + 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            String CHANNEL_ID = "channel_money_request";// The id of the channel.
            CharSequence channelName = "Settle up";
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (!MainActivity.appIsInForeground) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.accept, "Previous", pendingIntent).build();
                    NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, channelName, importance);
                    mNotificationManager.createNotificationChannel(mChannel);
                    Notification notification1 = new Notification.Builder(this, CHANNEL_ID)
                            .setContentText(message)
                            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                            .setContentTitle(title)
                            .setContentIntent(pendingIntent)
                            .addAction(R.drawable.decline, "Accept", accept)
                            .addAction(R.drawable.accept, "Decline", decline)
                            .setSmallIcon(R.drawable.notif_icon)
                            .setGroup("Gimme")
                            .setAutoCancel(true)
                            .build();
                    mNotificationManager.notify(uniqueId, notification1);
                } else {
                    Notification notification = new Notification.Builder(this)
                            .setContentText(message)
                            .setContentTitle(title)
                            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                            .setContentIntent(pendingIntent)
                            .setSmallIcon(R.drawable.notif_icon)
                            .setAutoCancel(true)
                            .addAction(R.drawable.accept, "Accept", accept)
                            .addAction(R.drawable.decline, "Decline", decline)
                            .setGroup("Gimme")
                            .build();
                    mNotificationManager.notify(uniqueId, notification);


                }
                if (MainActivity.appIsInForeground) {
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                }
            }

        } else {
            Intent i = new Intent(this, MainActivity.class);
            String moneyString = msg.split(" ")[0];
            db = new DataBaseHelper(this);
            db.getWritableDatabase();
            Boolean result = db.insertData(timeStamp, phoneNumber, reason, moneyString, code.charAt(0) + "", code.charAt(1) + "");
            if (moneyString.startsWith("-"))
                msg = "₹" + msg.substring(1);
            else
                msg = "₹" + msg;

            SharedPreferences Pref = getApplicationContext().getSharedPreferences("Data", Context.MODE_PRIVATE);
            String myNumber = Pref.getString("phonenumber", null);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, REQUEST_CODE,
                    i, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent intent = new Intent(this, NotificationBroadCastReceiver.class);
            intent.putExtra("Button clicked", "accept");
            intent.putExtra("notificationID", uniqueId);
            intent.putExtra("TimeStamp", timeStamp);
            intent.putExtra("receiverKey", receiverKey);
            intent.putExtra("senderkey", senderKey);
            intent.putExtra("code", code);
            intent.putExtra("my_number", myNumber);
            PendingIntent accept = PendingIntent.getBroadcast(this, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            intent.putExtra("Button clicked", "declined");
            PendingIntent decline = PendingIntent.getBroadcast(this, uniqueId + 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            String CHANNEL_ID = "channel_money_request";// The id of the channel.
            CharSequence name = getString(R.string.channel_name);// The user-visible name of the channel.

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (!MainActivity.appIsInForeground) {
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
                            .addAction(R.drawable.decline, "Accept", accept)
                            .addAction(R.drawable.accept, "Decline", decline)
                            .setSmallIcon(R.drawable.notif_icon)
                            .setGroup("Gimme")
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
                            .addAction(R.drawable.accept, "Accept", accept)
                            .addAction(R.drawable.decline, "Decline", decline)
                            .setGroup("Gimme")
                            .build();
                    mNotificationManager.notify(uniqueId, notification);

                }
                if (MainActivity.appIsInForeground) {
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                }
            }
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.w("onMessageReceived: ", remoteMessage.getData().get("title"));
        if (!isAppSentToBackground(getApplicationContext())) {
            Intent gcm_rec = new Intent("your_action");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(gcm_rec);
        }
        final String title = remoteMessage.getData().get("title");
        String messageReceived = remoteMessage.getData().get("body");
        String[] messageSplit = messageReceived.split(" ");
        String notificationid = messageSplit[0];
        String code = messageSplit[3];
        String senderKey = messageSplit[1];
        String receiverKey = messageSplit[2];
        messageReceived = "";
        for (int i = 4; i < messageSplit.length - 1; i++) {
            messageReceived += messageSplit[i] + " ";
        }
        messageReceived += messageSplit[messageSplit.length - 1];

        int location = messageReceived.indexOf("for");
        String reason = "";
        if (location != -1) {
            String temp = "";
            String[] tempArray = messageReceived.substring(location).split(" ");
            for (int i = 1; i < tempArray.length - 1; i++) {
                reason += tempArray[i] + " ";
            }
            temp = tempArray[tempArray.length - 1];
            messageReceived = messageReceived.substring(0, location) + temp;
        }
        String phoneNumber = "";
        String message = "";
        String[] checkingPhoneNumber = messageReceived.split(" ");
        String timeStamp = checkingPhoneNumber[checkingPhoneNumber.length - 1];
        int i;
        for (i = checkingPhoneNumber.length - 2; i > 0; i--) {
            if (checkingPhoneNumber[i].charAt(0) == '+') {
                phoneNumber = checkingPhoneNumber[i] + phoneNumber;
                break;
            } else {
                phoneNumber = checkingPhoneNumber[i] + phoneNumber;
            }
        }
        db = new DataBaseHelper(this);
        if (code.equals("01") || code.equals("03")) {


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
            message = message + " " + reason;
            showNotifications(title, message, phoneNumber, timeStamp, reason, receiverKey, senderKey, code);
        } else {

            Boolean result = db.updateData(timeStamp, code.charAt(0) + "", code.charAt(1) + "");
            if (code.equals("31") || code.equals("13")) {
                dbHistory = new HistoryDataBaseHelper(getApplication());
                dbHistory.insertData(timeStamp, phoneNumber, (db.getVerifiedSum(phoneNumber)) + "");
                db.deleteUserData(phoneNumber);
            }
        }

        DeletionFromRealtimeDatabase(senderKey, receiverKey, notificationid);

    }

//    public void onDestroy() {
//        super.onDestroy();
//        AlarmManager alarmMgr = (AlarmManager) this.getSystemService(ALARM_SERVICE);
//        Intent i = new Intent(this, MessageRecieverService.class);
//        PendingIntent pendingIntent = PendingIntent.getService(this, 0, i, 0);
//        alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10, pendingIntent);
//    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedPreferences Pref = getApplicationContext().getSharedPreferences("Data", Context.MODE_PRIVATE);
        String phonenumber = Pref.getString("phonenumber", null);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        String devicetoken = FirebaseInstanceId.getInstance().getToken();
        if (mAuth.getCurrentUser() != null) {
            String online_user_id = mAuth.getCurrentUser().getUid();
            databaseReference.child(online_user_id).child("device_number").setValue(phonenumber);
            databaseReference.child(online_user_id).child("device_token").setValue(devicetoken);
        }
    }
}


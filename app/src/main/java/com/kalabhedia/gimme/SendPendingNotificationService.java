package com.kalabhedia.gimme;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class SendPendingNotificationService extends JobService {
    private static final String TAG = "SendPendingNotification";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Job Started ");
        lookingForPendingNotification(jobParameters);
        return true;
    }

    private void lookingForPendingNotification(JobParameters jobParameters) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Notifications");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Data", Context.MODE_PRIVATE);
        String onlineUserId = sharedPreferences.getString("Current_user_id", null);
        database.keepSynced(true);
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<HashMap<String, String>> pendingNotification = new ArrayList<>();
                if (dataSnapshot.getChildren() != null) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        for (DataSnapshot data1 : data.getChildren()) {
                            HashMap<String, String> item = new HashMap<>();
                            if (data1.child("From").getValue().toString().equals(onlineUserId)) {
                                item.put("receiver_key", data.getKey().toString());
                                item.put("timestamp", data1.child("TimeStamp").getValue().toString());
                                item.put("Code", data1.child("Code").getValue().toString());
                                item.put("phone_number", data1.child("phone_number").getValue().toString());
                                item.put("Amount", data1.child("Amount").getValue().toString());
                                item.put("Reason", data1.child("Reason").getValue().toString());
                                pendingNotification.add(item);
                                DeletionFromRealtimeDatabase(data.getKey().toString(), onlineUserId);
                            }
                        }
                    }
                    if (pendingNotification.size() > 0) {
                        for (HashMap<String, String> item : pendingNotification) {
                            AddingNewContactFragment.sendNotificationToUser(item.get("timestamp"),
                                    onlineUserId, item.get("receiver_key"),
                                    item.get("phone_number"), item.get("Amount"),
                                    item.get("Reason"), item.get("Code"));
                        }
                    }
                    pendingNotification.clear();
                }
                jobFinished(jobParameters, false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return true;
    }

    void DeletionFromRealtimeDatabase(String receiverUserID, String senderUserID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Notifications");
//        Query applesQuery = ref.child(receiverUserID).orderByChild("From").equalTo(senderUserID);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    for (DataSnapshot appleSnapshot : data.getChildren()) {
                        if (appleSnapshot.child("From").getValue(String.class).equals(senderUserID))
                            appleSnapshot.getRef().removeValue();
                    }
                    Log.w("Notification: ", "Data deleted");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Notification", "onCancelled", databaseError.toException());
            }
        });
    }

}

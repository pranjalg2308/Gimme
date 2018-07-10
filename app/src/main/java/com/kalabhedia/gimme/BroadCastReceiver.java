package com.kalabhedia.gimme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "BroadCastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Notification Button clicked", Toast.LENGTH_SHORT).show();
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

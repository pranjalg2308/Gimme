package com.kalabhedia.gimme;

import com.google.firebase.database.FirebaseDatabase;

public class OfflineModeApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

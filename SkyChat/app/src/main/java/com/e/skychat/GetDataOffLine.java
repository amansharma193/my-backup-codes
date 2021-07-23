package com.e.skychat;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class GetDataOffLine extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

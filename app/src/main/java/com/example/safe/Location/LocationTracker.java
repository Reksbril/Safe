package com.example.safe.Location;

import android.location.Location;

import com.google.android.gms.tasks.OnSuccessListener;

public interface LocationTracker {
    boolean start(int delay, OnSuccessListener<Location> listener);
    void stop();
}

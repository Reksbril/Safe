package com.example.safe.Location;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.safe.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationTrackerImpl implements LocationTracker {
    private FusedLocationProviderClient locationClient;
    private Activity activity;
    private boolean started = false;

    public LocationTrackerImpl(Activity activity) {
        locationClient = LocationServices.getFusedLocationProviderClient(activity);
        this.activity = activity;

        checkPermissions();
    }

    private boolean checkPermissions() {
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    activity.getResources().getInteger(R.integer.REQUEST_ACCESS_FINE_LOCATION));
            return false;
        }
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    activity.getResources().getInteger(R.integer.REQUEST_ACCESS_COARSE_LOCATION));
            return false;
        }
        return true;
    }

    private void requestLocation(OnSuccessListener<Location> listener) throws SecurityException {
        locationClient.getLastLocation().addOnSuccessListener(activity, listener);
    }

    public boolean start(final int delay, final OnSuccessListener<Location> listener) {
        if(!checkPermissions())
            return false;

        if(!started) {
            started = true;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(checkPermissions())
                        requestLocation(listener);
                    if(started)
                        handler.postDelayed(this, delay);
                }
            }, delay);
        }
        return false;
    }

    public void stop() {
        started = false;
    }
}

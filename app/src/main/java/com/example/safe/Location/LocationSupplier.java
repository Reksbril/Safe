package com.example.safe.Location;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.safe.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationSupplier {
    private FusedLocationProviderClient locationClient;
    private Activity activity;

    public LocationSupplier(@NonNull Activity activity) {
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

    public void requestLocation(OnSuccessListener<Location> listener) {
        try {
            if(checkPermissions())
                locationClient.getLastLocation().addOnSuccessListener(activity, listener);
        } catch(SecurityException e) {
            //although program will never throw this exception, try{}catch block is needed
            e.printStackTrace();
        }
    }
}

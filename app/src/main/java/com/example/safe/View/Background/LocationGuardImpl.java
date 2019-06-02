package com.example.safe.View.Background;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import com.example.safe.Location.LocationSupplier;
import com.example.safe.Model.LocationGuard;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Calendar;
import java.util.Timer;

class LocationGuardImpl implements LocationGuard {
    private final Location destination;
    private Location currentLocation;
    private final LocationSupplier locationSupplier;
    private final long interval; //zmienna oznacza minimalny odstęp czasu (w milisekundach)
                                //pomiędzy dwoma próbami pobrania lokalizacji
    private long lastRequestMillis;
    private float eps = 1.f; //odległość od celu (w metrach), która jest uznawana za wystarczającą
                            //do zakończenia podróży


    LocationGuardImpl(Context context, Location destination, long interval) {
        this.destination = destination;
        locationSupplier = new LocationSupplier(context);
        locationSupplier.requestLocation(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentLocation = location;
            }
        });
        this.interval = interval;
        this.lastRequestMillis = Calendar.getInstance().getTimeInMillis();
    }

    @Override
    public boolean destinationReached() {
        if(currentLocation == null)
            return false;
        System.out.println(currentLocation.distanceTo(destination));
        if(currentLocation.distanceTo(destination) < eps)
            return true;

        if(Calendar.getInstance().getTimeInMillis() - lastRequestMillis > interval)
            locationSupplier.requestLocation(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    currentLocation = location;
                }
            });

        return false;
    }

    @Override
    public boolean outOfSafeLocation() {
        return false;
    }
}

package com.example.safe.View.Background;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.safe.Model.LocationGuard;
import com.google.android.gms.location.LocationRequest;

import java.util.HashSet;
import java.util.Set;

class LocationGuardImpl implements LocationGuard {
    private final Location destination;
    private Location currentLocation;
    private float eps = 1.f; //odległość od celu (w metrach), która jest uznawana za wystarczającą
                            //do zakończenia podróży

    private final Set<Observer> observers = new HashSet<>();


    LocationGuardImpl(Context context, Location destination, long interval) {
        this.destination = destination;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(interval);
        locationRequest.setFastestInterval(interval);

        //todo
        LocationManager locationManager =
                (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    interval,
                    eps / 2,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            currentLocation = location;
                            synchronized (observers) {
                                for(Observer o : observers)
                                    o.notifyChange(location);
                            }
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    });
        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean destinationReached() {
        if(currentLocation == null)
            return false;
        return (currentLocation.distanceTo(destination) < eps);
    }

    @Override
    public boolean outOfSafeLocation() {
        return false;
    }

    @Override
    public void addObserver(Observer o) {
        synchronized (observers) {
            observers.add(o);
        }
    }

    @Override
    public void removeObserver(Observer o) {
        synchronized (observers) {
            observers.remove(o);
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }
}

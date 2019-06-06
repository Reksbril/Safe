package com.example.safe.View.Background;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.safe.Model.LocationGuard;
import com.google.android.gms.location.LocationRequest;

class LocationGuardImpl implements LocationGuard {
    private final Location destination;
    private Location currentLocation;
    private long lastRequestMillis;
    private float eps = 1.f; //odległość od celu (w metrach), która jest uznawana za wystarczającą
                            //do zakończenia podróży


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
}

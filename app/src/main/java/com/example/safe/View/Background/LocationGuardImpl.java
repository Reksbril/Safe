package com.example.safe.View.Background;

import android.location.Location;

import com.example.safe.Model.LocationGuard;

class LocationGuardImpl implements LocationGuard {
    private final Location destination;
    private Location currentLocation;

    LocationGuardImpl(Location destination) {
        this.destination = destination;
    }

    @Override
    public boolean destinationReached() {
        return false;
    }

    @Override
    public boolean outOfSafeLocation() {
        return false;
    }
}

package com.example.safe.Model;

import android.location.Location;

public interface LocationGuard {
    boolean destinationReached();
    boolean outOfSafeLocation();
    void addObserver(Observer o);
    void removeObserver(Observer o);
    Location getCurrentLocation();


    interface Observer {
        void notifyChange(Location loc);
    }
}

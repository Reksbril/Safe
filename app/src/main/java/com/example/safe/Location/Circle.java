package com.example.safe.Location;

import android.location.Location;

public class Circle implements PredictedLocations {
    final Location middle;
    final double range;


    public Circle(Location middle, double range) {
        this.middle = middle;
        this.range = range;
    }

    public boolean isGood(Location location) {
        return location.distanceTo(middle) <= range;
    }
}

package com.google.sps.placeGuide;

import org.jetbrains.annotations.Nullable;

/** Class containing latitude and longitude of a place. */
public class PlaceCoordinate {
    private final double lat;
    private final double lng;

    public PlaceCoordinate(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLatitude() {
        return lat;
    }

    public double getLongitude() {
        return lng;
    }
}
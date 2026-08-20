package com.example.cabbooking.dto;

import com.example.cabbooking.exception.InvalidLocationException;

public record Location(double latitude, double longitude) {

    public static final double MIN_LATITUDE = -90.0;
    public static final double MAX_LATITUDE = 90.0;
    public static final double MIN_LONGITUDE = -180.0;
    public static final double MAX_LONGITUDE = 180.0;

    public Location {
        if (Double.isNaN(latitude) || Double.isNaN(longitude)
                || Double.isInfinite(latitude) || Double.isInfinite(longitude)) {
            throw new InvalidLocationException("Latitude/longitude must be finite numbers");
        }
        if (latitude < MIN_LATITUDE || latitude > MAX_LATITUDE) {
            throw new InvalidLocationException(
                    "Latitude must be between " + MIN_LATITUDE + " and " + MAX_LATITUDE + " but was " + latitude);
        }
        if (longitude < MIN_LONGITUDE || longitude > MAX_LONGITUDE) {
            throw new InvalidLocationException(
                    "Longitude must be between " + MIN_LONGITUDE + " and " + MAX_LONGITUDE + " but was " + longitude);
        }
    }
}

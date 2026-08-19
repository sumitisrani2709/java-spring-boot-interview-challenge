package com.example.cabbooking.service;

import com.example.cabbooking.dto.Location;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Great-circle distance using the Haversine formula.
 *
 * <p>Haversine models the Earth as a sphere. It is numerically stable for the small
 * distances that matter for cab dispatch, needs no external service, and runs in O(1)
 * per pair of points - so matching a pickup against {@code n} drivers is O(n).
 */
@Component
public class HaversineDistanceCalculator implements DistanceCalculator {

    /** Mean Earth radius in kilometres (IUGG mean radius). */
    static final double EARTH_RADIUS_KM = 6371.0088;

    @Override
    public double distanceInKm(Location from, Location to) {
        Objects.requireNonNull(from, "from location must not be null");
        Objects.requireNonNull(to, "to location must not be null");

        double lat1 = Math.toRadians(from.latitude());
        double lat2 = Math.toRadians(to.latitude());
        double deltaLat = lat2 - lat1;
        double deltaLon = Math.toRadians(to.longitude() - from.longitude());

        double sinHalfLat = Math.sin(deltaLat / 2);
        double sinHalfLon = Math.sin(deltaLon / 2);

        double a = sinHalfLat * sinHalfLat + Math.cos(lat1) * Math.cos(lat2) * sinHalfLon * sinHalfLon;
        double c = 2 * Math.asin(Math.min(1.0, Math.sqrt(a)));

        return EARTH_RADIUS_KM * c;
    }
}

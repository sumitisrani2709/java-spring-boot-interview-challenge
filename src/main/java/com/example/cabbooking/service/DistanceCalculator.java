package com.example.cabbooking.service;

import com.example.cabbooking.dto.Location;

/**
 * Strategy for measuring the distance between two points on Earth.
 *
 * <p>Kept as an interface so the matching logic never hard-codes a particular formula
 * and so tests can substitute a trivial implementation.
 */
public interface DistanceCalculator {

    /** Great-circle distance between two points, in kilometres. Always {@code >= 0}. */
    double distanceInKm(Location from, Location to);
}

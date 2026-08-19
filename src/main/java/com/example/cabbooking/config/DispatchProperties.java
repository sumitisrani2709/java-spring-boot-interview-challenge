package com.example.cabbooking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Tunable dispatch rules. Bound from the {@code cab.dispatch.*} keys in
 * {@code application.properties} so nothing has to be hard-coded in the services.
 */
@ConfigurationProperties(prefix = "cab.dispatch")
public class DispatchProperties {

    /**
     * Maximum great-circle distance, in kilometres, between a pickup point and a driver
     * for that driver to be considered for the ride. A driver exactly at this distance
     * is still eligible; anything beyond it is not.
     */
    private double maxPickupDistanceKm = 15.0;

    public double getMaxPickupDistanceKm() {
        return maxPickupDistanceKm;
    }

    public void setMaxPickupDistanceKm(double maxPickupDistanceKm) {
        this.maxPickupDistanceKm = maxPickupDistanceKm;
    }
}

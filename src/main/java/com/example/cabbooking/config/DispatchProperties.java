package com.example.cabbooking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cab.dispatch")
public class DispatchProperties {

    private double maxPickupDistanceKm = 15.0;

    public double getMaxPickupDistanceKm() {
        return maxPickupDistanceKm;
    }

    public void setMaxPickupDistanceKm(double maxPickupDistanceKm) {
        this.maxPickupDistanceKm = maxPickupDistanceKm;
    }
}

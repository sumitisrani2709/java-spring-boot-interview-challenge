package com.example.cabbooking.dto;

import com.example.cabbooking.domain.Driver;
import com.example.cabbooking.domain.DriverStatus;

/** Response body for driver endpoints. */
public record DriverResponse(Long id, String name, double latitude, double longitude, DriverStatus status) {

    public static DriverResponse from(Driver driver) {
        return new DriverResponse(
                driver.getId(), driver.getName(), driver.getLatitude(), driver.getLongitude(), driver.getStatus());
    }
}

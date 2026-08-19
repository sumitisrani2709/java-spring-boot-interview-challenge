package com.example.cabbooking.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/** Payload for {@code POST /api/rides}. */
public record CreateRideRequest(

        @NotNull(message = "customerId is required")
        Long customerId,

        @NotNull(message = "pickupLatitude is required")
        @DecimalMin(value = "-90.0", message = "pickupLatitude must be >= -90")
        @DecimalMax(value = "90.0", message = "pickupLatitude must be <= 90")
        Double pickupLatitude,

        @NotNull(message = "pickupLongitude is required")
        @DecimalMin(value = "-180.0", message = "pickupLongitude must be >= -180")
        @DecimalMax(value = "180.0", message = "pickupLongitude must be <= 180")
        Double pickupLongitude) {

    public Location pickupLocation() {
        return new Location(pickupLatitude, pickupLongitude);
    }
}

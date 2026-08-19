package com.example.cabbooking.dto;

import com.example.cabbooking.domain.RideRequest;
import com.example.cabbooking.domain.RideStatus;

/** Response body for ride endpoints. */
public record RideResponse(Long rideId, Long driverId, RideStatus status) {

    public static RideResponse from(RideRequest ride) {
        return new RideResponse(ride.getId(), ride.getAssignedDriverId(), ride.getStatus());
    }
}

package com.example.cabbooking.dto;

import com.example.cabbooking.entity.RideRequest;
import com.example.cabbooking.entity.enums.RideStatus;

public record RideResponse(Long rideId, Long driverId, RideStatus status) {

    public static RideResponse from(RideRequest ride) {
        return new RideResponse(ride.getId(), ride.getAssignedDriverId(), ride.getStatus());
    }
}

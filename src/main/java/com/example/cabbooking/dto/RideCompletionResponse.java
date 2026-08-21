package com.example.cabbooking.dto;

import com.example.cabbooking.entity.enums.RideStatus;

/** Response body for a completed ride. */
public record RideCompletionResponse(Long rideId, Long driverId, RideStatus status, double fare) {
}

package com.example.cabbooking.dto;

/** Payload for {@code POST /api/rides/{rideId}/complete}, sent by the driver app at drop-off. */
public record CompleteRideRequest(Double distanceKm, Integer durationMinutes) {
}

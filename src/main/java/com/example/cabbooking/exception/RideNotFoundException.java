package com.example.cabbooking.exception;

/** Thrown when a ride request id does not exist. */
public class RideNotFoundException extends RuntimeException {

    public RideNotFoundException(Long rideId) {
        super("Ride request not found: " + rideId);
    }
}

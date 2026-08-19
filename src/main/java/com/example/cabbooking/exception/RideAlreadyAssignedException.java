package com.example.cabbooking.exception;

import com.example.cabbooking.domain.RideStatus;

/** Thrown when a ride is not in a state that allows a driver to be assigned. */
public class RideAlreadyAssignedException extends RuntimeException {

    public RideAlreadyAssignedException(Long rideId, RideStatus status) {
        super("Ride " + rideId + " cannot be assigned a driver while in status " + status);
    }
}

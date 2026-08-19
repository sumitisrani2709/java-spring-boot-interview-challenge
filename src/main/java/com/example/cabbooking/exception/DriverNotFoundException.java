package com.example.cabbooking.exception;

/** Thrown when a driver id does not exist. */
public class DriverNotFoundException extends RuntimeException {

    public DriverNotFoundException(Long driverId) {
        super("Driver not found: " + driverId);
    }
}

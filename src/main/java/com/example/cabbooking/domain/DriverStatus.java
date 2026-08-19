package com.example.cabbooking.domain;

/**
 * Lifecycle state of a driver.
 *
 * <p>Only {@link #AVAILABLE} drivers may be considered for a new ride.
 */
public enum DriverStatus {

    /** Online and not currently on a ride - eligible for assignment. */
    AVAILABLE,

    /** Online but already assigned to a ride. */
    BUSY,

    /** Not working - must never be assigned. */
    OFFLINE
}

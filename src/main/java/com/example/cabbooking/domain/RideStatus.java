package com.example.cabbooking.domain;

/** Lifecycle state of a ride request. */
public enum RideStatus {

    /** Created, but no driver has been assigned yet. */
    REQUESTED,

    /** A driver has been assigned and is on the way. */
    DRIVER_ASSIGNED,

    /** The ride was cancelled before completion. */
    CANCELLED,

    /** The ride finished successfully. */
    COMPLETED
}

package com.example.cabbooking.exception;

/** Thrown when no {@code AVAILABLE} driver could be matched to a pickup location. */
public class NoDriverAvailableException extends RuntimeException {

    public NoDriverAvailableException(String message) {
        super(message);
    }
}

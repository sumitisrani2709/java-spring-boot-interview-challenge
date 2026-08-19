package com.example.cabbooking.exception;

/** Thrown when a latitude/longitude pair is not a valid point on Earth. */
public class InvalidLocationException extends RuntimeException {

    public InvalidLocationException(String message) {
        super(message);
    }
}

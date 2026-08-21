package com.example.cabbooking.dto;

import java.time.LocalDateTime;
import java.util.List;

/** Earnings summary for one driver, newest rides last. */
public record DriverEarningsResponse(Long driverId, double totalEarned, int rideCount, List<Entry> entries) {

    public record Entry(
            Long rideId,
            double amount,
            double pickupLatitude,
            double pickupLongitude,
            LocalDateTime completedAt) {
    }
}

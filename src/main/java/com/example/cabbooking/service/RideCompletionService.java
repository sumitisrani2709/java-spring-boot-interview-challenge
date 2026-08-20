package com.example.cabbooking.service;

import com.example.cabbooking.dto.CompleteRideRequest;
import com.example.cabbooking.dto.RideCompletionResponse;
import com.example.cabbooking.entity.Driver;
import com.example.cabbooking.entity.DriverEarning;
import com.example.cabbooking.entity.RideRequest;
import com.example.cabbooking.entity.enums.DriverStatus;
import com.example.cabbooking.entity.enums.RideStatus;
import com.example.cabbooking.exception.DriverNotFoundException;
import com.example.cabbooking.exception.RideNotFoundException;
import com.example.cabbooking.repository.DriverEarningRepository;
import com.example.cabbooking.repository.DriverRepository;
import com.example.cabbooking.repository.RideRequestRepository;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Closes out a ride at drop-off: marks it {@code COMPLETED}, puts the driver back into the
 * available pool, and records what the driver earned for the trip.
 */
@Service
public class RideCompletionService {

    private static final Logger log = LoggerFactory.getLogger(RideCompletionService.class);

    private static final double BASE_FARE = 50.0;
    private static final double RATE_PER_KM = 12.0;
    private static final double RATE_PER_MINUTE = 2.0;

    private final RideRequestRepository rideRequestRepository;
    private final DriverRepository driverRepository;
    private final DriverEarningRepository driverEarningRepository;

    public RideCompletionService(
            RideRequestRepository rideRequestRepository,
            DriverRepository driverRepository,
            DriverEarningRepository driverEarningRepository) {
        this.rideRequestRepository = rideRequestRepository;
        this.driverRepository = driverRepository;
        this.driverEarningRepository = driverEarningRepository;
    }

    /**
     * Completes a ride and pays the driver for it.
     *
     * @param rideId  the ride being dropped off
     * @param request distance and duration reported by the driver app
     * @return the completed ride with the fare that was recorded
     */
    public RideCompletionResponse completeRide(Long rideId, CompleteRideRequest request) {
        RideRequest ride = rideRequestRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(rideId));

        double fare = calculateFare(request.distanceKm(), request.durationMinutes());

        ride.setStatus(RideStatus.COMPLETED);
        rideRequestRepository.save(ride);

        Driver driver = driverRepository.findById(ride.getAssignedDriverId())
                .orElseThrow(() -> new DriverNotFoundException(ride.getAssignedDriverId()));
        driver.setStatus(DriverStatus.AVAILABLE);
        driverRepository.save(driver);

        try {
            driverEarningRepository.save(
                    new DriverEarning(rideId, driver.getId(), fare, LocalDateTime.now()));
        } catch (Exception ex) {
            log.warn("Could not record earning for ride {}", rideId, ex);
        }

        log.info("Ride {} completed by driver {} for {}", rideId, driver.getId(), fare);
        return new RideCompletionResponse(rideId, driver.getId(), RideStatus.COMPLETED, fare);
    }

    /** Flat pickup charge, plus distance, plus time on the road. Fares are whole rupees. */
    private double calculateFare(Double distanceKm, Integer durationMinutes) {
        double fare = BASE_FARE + (RATE_PER_KM * distanceKm) + (RATE_PER_MINUTE * durationMinutes);
        return (int) fare;
    }
}

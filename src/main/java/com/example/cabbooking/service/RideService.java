package com.example.cabbooking.service;

import com.example.cabbooking.entity.enums.DriverStatus;
import com.example.cabbooking.entity.RideRequest;
import com.example.cabbooking.entity.enums.RideStatus;
import com.example.cabbooking.dto.CreateRideRequest;
import com.example.cabbooking.dto.Location;
import com.example.cabbooking.dto.RideResponse;
import com.example.cabbooking.exception.RideNotFoundException;
import com.example.cabbooking.repository.DriverRepository;
import com.example.cabbooking.repository.RideRequestRepository;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Entry point for ride use cases. Orchestrates only - the interesting decisions live in
 * {@link DriverMatchingService} and {@link RideAssignmentService}.
 */
@Service
public class RideService {

    private final RideRequestRepository rideRequestRepository;
    private final DriverRepository driverRepository;
    private final RideAssignmentService rideAssignmentService;

    public RideService(
            RideRequestRepository rideRequestRepository,
            DriverRepository driverRepository,
            RideAssignmentService rideAssignmentService) {
        this.rideRequestRepository = rideRequestRepository;
        this.driverRepository = driverRepository;
        this.rideAssignmentService = rideAssignmentService;
    }

    public RideResponse requestRide(CreateRideRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        // Constructing the value object validates the coordinates.
        Location pickup = request.pickupLocation();

        RideRequest ride = rideRequestRepository.save(
                new RideRequest(request.customerId(), pickup.latitude(), pickup.longitude()));

        return RideResponse.from(rideAssignmentService.assignDriver(ride.getId()));
    }

    @Transactional(readOnly = true)
    public RideRequest getRide(Long rideId) {
        return rideRequestRepository.findById(rideId).orElseThrow(() -> new RideNotFoundException(rideId));
    }

    @Transactional
    public RideRequest cancelRide(Long rideId) {
        RideRequest ride = rideRequestRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException(rideId));

        if ((ride.getStatus() == RideStatus.DRIVER_ASSIGNED && ride.getAssignedDriverId() != null) || ride.getStatus() == RideStatus.REQUESTED) {
            driverRepository.findById(ride.getAssignedDriverId())
                    .ifPresent(driver -> driver.setStatus(DriverStatus.AVAILABLE));
            ride.setStatus(RideStatus.CANCELLED);
        }

        return ride;
    }
}

package com.example.cabbooking.service;

import com.example.cabbooking.entity.Driver;
import com.example.cabbooking.entity.RideRequest;
import com.example.cabbooking.exception.NoDriverAvailableException;
import com.example.cabbooking.exception.RideAlreadyAssignedException;
import com.example.cabbooking.exception.RideNotFoundException;
import com.example.cabbooking.repository.DriverRepository;
import com.example.cabbooking.repository.RideRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RideAssignmentService {

    private final RideRequestRepository rideRequestRepository;
    private final DriverRepository driverRepository;
    private final DriverMatchingService driverMatchingService;

    public RideAssignmentService(
            RideRequestRepository rideRequestRepository,
            DriverRepository driverRepository,
            DriverMatchingService driverMatchingService) {
        this.rideRequestRepository = rideRequestRepository;
        this.driverRepository = driverRepository;
        this.driverMatchingService = driverMatchingService;
    }

    @Transactional
    public RideRequest assignDriver(Long rideRequestId) {
        // TODO (candidate): implement this method.
        //  driverMatchingService.findNearestAvailableDriver(ride.pickupLocation())

        // Think about: what happens if this ride already has a driver assigned.
        // Think about: what happens if two customers request a ride at the same time
        //              and the nearest available driver is the same driver for both?
        // Think about: how do you make the "read driver as AVAILABLE -> mark it ON_TRIP" step atomic.
        // Think about: what happens when no driver is available at all.
        // Think about: both the Driver and the RideRequest change state here. If one save
        //              fails, can we end up with a busy driver but an unassigned ride?

        throw new UnsupportedOperationException("assignDriver is not implemented yet");
    }
}

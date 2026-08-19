package com.example.cabbooking.service;

import com.example.cabbooking.domain.Driver;
import com.example.cabbooking.domain.RideRequest;
import com.example.cabbooking.exception.NoDriverAvailableException;
import com.example.cabbooking.exception.RideAlreadyAssignedException;
import com.example.cabbooking.exception.RideNotFoundException;
import com.example.cabbooking.repository.DriverRepository;
import com.example.cabbooking.repository.RideRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Turns a matching decision into a durable state change: the ride gets a driver and
 * the driver stops being available.
 */
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

    /**
     * Assigns the nearest available driver to a ride that is still {@code REQUESTED}.
     *
     * <p>On success the ride moves to {@code DRIVER_ASSIGNED} with
     * {@code assignedDriverId} set, and the chosen {@link Driver} moves from
     * {@code AVAILABLE} to {@code BUSY}. Either both happen or neither does.
     *
     * @param rideRequestId id of the ride to assign
     * @return the updated ride
     * @throws RideNotFoundException        if no such ride exists
     * @throws RideAlreadyAssignedException if the ride is not in {@code REQUESTED}
     * @throws NoDriverAvailableException   if no eligible driver could be found
     */
    @Transactional
    public RideRequest assignDriver(Long rideRequestId) {
        // ====================================================================
        // TODO (candidate): implement this method.
        //
        // Available building blocks:
        //   - rideRequestRepository.findById(...) and ride.assignTo(driverId)
        //   - driverMatchingService.findNearestAvailableDriver(ride.pickupLocation())
        //   - driverRepository.findByIdForUpdate(...) for a row-level write lock
        //   - Driver has an @Version field, so JPA optimistic locking is available too
        //
        // Think about: what happens if two customers request a ride at the same
        // instant and the matching step hands both of them the same driver.
        // ====================================================================
        throw new UnsupportedOperationException("assignDriver is not implemented yet");
    }
}

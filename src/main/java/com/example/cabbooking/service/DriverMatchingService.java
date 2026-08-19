package com.example.cabbooking.service;

import com.example.cabbooking.config.DispatchProperties;
import com.example.cabbooking.domain.Driver;
import com.example.cabbooking.dto.Location;
import com.example.cabbooking.repository.DriverRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Decides <em>which</em> driver should serve a pickup point.
 *
 * <p>This class is intentionally free of side effects: it only reads. Changing driver
 * state is the job of {@link RideAssignmentService}.
 */
@Service
public class DriverMatchingService {

    private final DriverRepository driverRepository;
    private final DistanceCalculator distanceCalculator;
    private final DispatchProperties dispatchProperties;

    public DriverMatchingService(
            DriverRepository driverRepository,
            DistanceCalculator distanceCalculator,
            DispatchProperties dispatchProperties) {
        this.driverRepository = driverRepository;
        this.distanceCalculator = distanceCalculator;
        this.dispatchProperties = dispatchProperties;
    }

    /**
     * Finds the driver closest to {@code pickupLocation}.
     *
     * <p>Rules:
     * <ol>
     *   <li>Only drivers whose status is {@code AVAILABLE} may be returned.</li>
     *   <li>Distance is measured with the injected {@link DistanceCalculator}.</li>
     *   <li>A driver farther away than
     *       {@link DispatchProperties#getMaxPickupDistanceKm()} is not eligible;
     *       a driver exactly at that distance still is.</li>
     *   <li>If two eligible drivers are the same distance away, the one with the
     *       lowest id wins, so the result is deterministic.</li>
     *   <li>If nobody is eligible, return {@link Optional#empty()} - do not return
     *       {@code null} and do not throw.</li>
     * </ol>
     *
     * @param pickupLocation where the customer is waiting; never {@code null}
     * @return the nearest eligible driver, or empty if there is none
     */
    @Transactional(readOnly = true)
    public Optional<Driver> findNearestAvailableDriver(Location pickupLocation) {
        // ====================================================================
        // TODO (candidate): implement this method.
        //
        // Available building blocks:
        //   - driverRepository.findByStatus(...) / findByStatusOrderByIdAsc(...)
        //   - distanceCalculator.distanceInKm(from, to)
        //   - driver.location() and dispatchProperties.getMaxPickupDistanceKm()
        //
        // Think about: which work belongs in the database and which in Java, and
        // how this behaves once there are 100k drivers rather than 5.
        // ====================================================================
        throw new UnsupportedOperationException("findNearestAvailableDriver is not implemented yet");
    }
}

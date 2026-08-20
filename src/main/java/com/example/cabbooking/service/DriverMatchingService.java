package com.example.cabbooking.service;

import com.example.cabbooking.config.DispatchProperties;
import com.example.cabbooking.entity.Driver;
import com.example.cabbooking.dto.Location;
import com.example.cabbooking.repository.DriverRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Optional<Driver> findNearestAvailableDriver(Location pickupLocation) {
        // TODO (candidate): implement this method.
        //  driverRepository.findByStatus(DriverStatus.AVAILABLE)
        //  distanceCalculator.distanceInKm(pickupLocation, driver.location())
        //  dispatchProperties.getMaxPickupDistanceKm()

        // Think about: how this behaves once there are 100k drivers rather than 5.
        // Think about: driver is within the max pickup distance.
        // Think about: two drivers exactly the same distance away.


        throw new UnsupportedOperationException("findNearestAvailableDriver is not implemented yet");
    }
}

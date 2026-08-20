package com.example.cabbooking.service;

import com.example.cabbooking.entity.Driver;
import com.example.cabbooking.entity.enums.DriverStatus;
import com.example.cabbooking.exception.DriverNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cabbooking.repository.DriverRepository;

/** Small admin surface over drivers, used to set up scenarios during the interview. */
@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Transactional(readOnly = true)
    public List<Driver> findAll() {
        return driverRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Driver findById(Long driverId) {
        return driverRepository.findById(driverId).orElseThrow(() -> new DriverNotFoundException(driverId));
    }

    @Transactional
    public Driver updateStatus(Long driverId, DriverStatus status) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        driver.setStatus(status);
        return driver;
    }
}

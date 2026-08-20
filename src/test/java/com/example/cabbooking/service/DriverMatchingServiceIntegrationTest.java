package com.example.cabbooking.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.cabbooking.entity.Driver;
import com.example.cabbooking.entity.enums.DriverStatus;
import com.example.cabbooking.dto.Location;
import com.example.cabbooking.repository.DriverRepository;
import com.example.cabbooking.repository.RideRequestRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Behaviour of {@link DriverMatchingService#findNearestAvailableDriver(Location)}.
 *
 * <p>These tests run against a real H2 database, so any reasonable implementation
 * passes - it does not matter which repository method you choose.
 */
@SpringBootTest
class DriverMatchingServiceIntegrationTest {

    /** Connaught Place, New Delhi. */
    private static final Location PICKUP = new Location(28.6139, 77.2090);

    @Autowired
    private DriverMatchingService driverMatchingService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private RideRequestRepository rideRequestRepository;

    @BeforeEach
    void resetDatabase() {
        rideRequestRepository.deleteAll();
        driverRepository.deleteAll();
    }

    @Test
    @DisplayName("returns the closest AVAILABLE driver")
    void picksTheClosestAvailableDriver() {
        Driver far = save("Far", 28.6200, 77.2200, DriverStatus.AVAILABLE);   // ~1.3 km
        Driver near = save("Near", 28.6145, 77.2100, DriverStatus.AVAILABLE); // ~0.1 km

        Optional<Driver> match = driverMatchingService.findNearestAvailableDriver(PICKUP);

        assertThat(match).isPresent();
        assertThat(match.get().getId()).isEqualTo(near.getId());
        assertThat(match.get().getId()).isNotEqualTo(far.getId());
    }

    @Test
    @DisplayName("ignores BUSY drivers even when they are the closest")
    void ignoresBusyDrivers() {
        save("Busy but closest", 28.6139, 77.2090, DriverStatus.BUSY);
        Driver available = save("Available", 28.6200, 77.2200, DriverStatus.AVAILABLE);

        Optional<Driver> match = driverMatchingService.findNearestAvailableDriver(PICKUP);

        assertThat(match).isPresent();
        assertThat(match.get().getId()).isEqualTo(available.getId());
    }

    @Test
    @DisplayName("ignores OFFLINE drivers even when they are the closest")
    void ignoresOfflineDrivers() {
        save("Offline but closest", 28.6139, 77.2090, DriverStatus.OFFLINE);
        Driver available = save("Available", 28.6200, 77.2200, DriverStatus.AVAILABLE);

        Optional<Driver> match = driverMatchingService.findNearestAvailableDriver(PICKUP);

        assertThat(match).isPresent();
        assertThat(match.get().getId()).isEqualTo(available.getId());
    }

    @Test
    @DisplayName("returns empty when there are no drivers at all")
    void emptyWhenNoDrivers() {
        assertThat(driverMatchingService.findNearestAvailableDriver(PICKUP)).isEmpty();
    }

    @Test
    @DisplayName("returns empty when every driver is BUSY or OFFLINE")
    void emptyWhenNobodyIsAvailable() {
        save("Busy", 28.6140, 77.2091, DriverStatus.BUSY);
        save("Offline", 28.6141, 77.2092, DriverStatus.OFFLINE);

        assertThat(driverMatchingService.findNearestAvailableDriver(PICKUP)).isEmpty();
    }

    @Test
    @DisplayName("breaks a distance tie deterministically by lowest driver id")
    void breaksTiesByLowestId() {
        // Symmetric about the pickup point, so both are exactly the same distance away.
        Driver first = save("North", 28.6239, 77.2090, DriverStatus.AVAILABLE);
        Driver second = save("South", 28.6039, 77.2090, DriverStatus.AVAILABLE);
        assertThat(first.getId()).isLessThan(second.getId());

        for (int attempt = 0; attempt < 5; attempt++) {
            Optional<Driver> match = driverMatchingService.findNearestAvailableDriver(PICKUP);

            assertThat(match).isPresent();
            assertThat(match.get().getId())
                    .as("tie-break must be stable across calls (attempt %d)", attempt)
                    .isEqualTo(first.getId());
        }
    }

    @Test
    @DisplayName("ignores drivers beyond the configured pickup radius")
    void ignoresDriversOutsideTheRadius() {
        save("Too far", 28.7300, 77.0700, DriverStatus.AVAILABLE); // ~18.7 km, radius is 15 km

        assertThat(driverMatchingService.findNearestAvailableDriver(PICKUP)).isEmpty();
    }

    @Test
    @DisplayName("still matches a driver that is inside the radius")
    void matchesDriverInsideTheRadius() {
        save("Too far", 28.7300, 77.0700, DriverStatus.AVAILABLE);   // ~18.7 km
        Driver inRange = save("In range", 28.7041, 77.1025, DriverStatus.AVAILABLE); // ~14.4 km

        Optional<Driver> match = driverMatchingService.findNearestAvailableDriver(PICKUP);

        assertThat(match).isPresent();
        assertThat(match.get().getId()).isEqualTo(inRange.getId());
    }

    private Driver save(String name, double latitude, double longitude, DriverStatus status) {
        return driverRepository.saveAndFlush(new Driver(name, latitude, longitude, status));
    }
}

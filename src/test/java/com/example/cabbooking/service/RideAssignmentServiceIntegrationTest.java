package com.example.cabbooking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.cabbooking.entity.Driver;
import com.example.cabbooking.entity.enums.DriverStatus;
import com.example.cabbooking.entity.RideRequest;
import com.example.cabbooking.entity.enums.RideStatus;
import com.example.cabbooking.exception.NoDriverAvailableException;
import com.example.cabbooking.exception.RideAlreadyAssignedException;
import com.example.cabbooking.exception.RideNotFoundException;
import com.example.cabbooking.repository.DriverRepository;
import com.example.cabbooking.repository.RideRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RideAssignmentServiceIntegrationTest {

    private static final double PICKUP_LAT = 28.6139;
    private static final double PICKUP_LON = 77.2090;

    @Autowired
    private RideAssignmentService rideAssignmentService;

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
    @DisplayName("assigns the nearest driver and moves the ride to DRIVER_ASSIGNED")
    void assignsNearestDriver() {
        save("Far", 28.6200, 77.2200, DriverStatus.AVAILABLE);
        Driver near = save("Near", 28.6145, 77.2100, DriverStatus.AVAILABLE);
        RideRequest ride = newRide();

        RideRequest assigned = rideAssignmentService.assignDriver(ride.getId());

        assertThat(assigned.getStatus()).isEqualTo(RideStatus.DRIVER_ASSIGNED);
        assertThat(assigned.getAssignedDriverId()).isEqualTo(near.getId());
    }

    @Test
    @DisplayName("the assigned driver becomes BUSY")
    void assignedDriverBecomesBusy() {
        Driver driver = save("Solo", 28.6145, 77.2100, DriverStatus.AVAILABLE);
        RideRequest ride = newRide();

        rideAssignmentService.assignDriver(ride.getId());

        Driver reloaded = driverRepository.findById(driver.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(DriverStatus.BUSY);
    }

    @Test
    @DisplayName("the change is durable - reloading the ride shows the driver")
    void assignmentIsPersisted() {
        Driver driver = save("Solo", 28.6145, 77.2100, DriverStatus.AVAILABLE);
        RideRequest ride = newRide();

        rideAssignmentService.assignDriver(ride.getId());

        RideRequest reloaded = rideRequestRepository.findById(ride.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(RideStatus.DRIVER_ASSIGNED);
        assertThat(reloaded.getAssignedDriverId()).isEqualTo(driver.getId());
    }

    @Test
    @DisplayName("a second ride never gets the driver that is already BUSY")
    void doesNotReuseABusyDriver() {
        Driver only = save("Only", 28.6145, 77.2100, DriverStatus.AVAILABLE);
        Driver second = save("Second", 28.6150, 77.2110, DriverStatus.AVAILABLE);
        RideRequest first = newRide();
        RideRequest next = newRide();

        rideAssignmentService.assignDriver(first.getId());
        rideAssignmentService.assignDriver(next.getId());

        RideRequest reloadedFirst = rideRequestRepository.findById(first.getId()).orElseThrow();
        RideRequest reloadedNext = rideRequestRepository.findById(next.getId()).orElseThrow();
        assertThat(reloadedFirst.getAssignedDriverId()).isEqualTo(only.getId());
        assertThat(reloadedNext.getAssignedDriverId()).isEqualTo(second.getId());
        assertThat(reloadedFirst.getAssignedDriverId()).isNotEqualTo(reloadedNext.getAssignedDriverId());
    }

    @Test
    @DisplayName("throws NoDriverAvailableException when nobody is available")
    void failsWhenNoDriverAvailable() {
        save("Busy", 28.6140, 77.2091, DriverStatus.BUSY);
        RideRequest ride = newRide();

        assertThatThrownBy(() -> rideAssignmentService.assignDriver(ride.getId()))
                .isInstanceOf(NoDriverAvailableException.class);
    }

    @Test
    @DisplayName("a failed assignment leaves the ride in REQUESTED")
    void failedAssignmentLeavesRideRequested() {
        RideRequest ride = newRide();

        assertThatThrownBy(() -> rideAssignmentService.assignDriver(ride.getId()))
                .isInstanceOf(NoDriverAvailableException.class);

        RideRequest reloaded = rideRequestRepository.findById(ride.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(RideStatus.REQUESTED);
        assertThat(reloaded.getAssignedDriverId()).isNull();
    }

    @Test
    @DisplayName("throws RideNotFoundException for an unknown ride id")
    void failsForUnknownRide() {
        assertThatThrownBy(() -> rideAssignmentService.assignDriver(999_999L))
                .isInstanceOf(RideNotFoundException.class);
    }

    @Test
    @DisplayName("refuses to assign a driver twice to the same ride")
    void refusesToReassignAnAlreadyAssignedRide() {
        save("A", 28.6145, 77.2100, DriverStatus.AVAILABLE);
        save("B", 28.6150, 77.2110, DriverStatus.AVAILABLE);
        RideRequest ride = newRide();
        rideAssignmentService.assignDriver(ride.getId());

        assertThatThrownBy(() -> rideAssignmentService.assignDriver(ride.getId()))
                .isInstanceOf(RideAlreadyAssignedException.class);
    }

    private Driver save(String name, double latitude, double longitude, DriverStatus status) {
        return driverRepository.saveAndFlush(new Driver(name, latitude, longitude, status));
    }

    private RideRequest newRide() {
        return rideRequestRepository.saveAndFlush(new RideRequest(101L, PICKUP_LAT, PICKUP_LON));
    }
}

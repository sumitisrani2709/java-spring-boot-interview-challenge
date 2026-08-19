package com.example.cabbooking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.example.cabbooking.domain.Driver;
import com.example.cabbooking.domain.DriverStatus;
import com.example.cabbooking.domain.RideRequest;
import com.example.cabbooking.domain.RideStatus;
import com.example.cabbooking.dto.CreateRideRequest;
import com.example.cabbooking.dto.RideResponse;
import com.example.cabbooking.exception.InvalidLocationException;
import com.example.cabbooking.exception.RideNotFoundException;
import com.example.cabbooking.repository.DriverRepository;
import com.example.cabbooking.repository.RideRequestRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Orchestration only - the matching and assignment collaborators are mocked, so this
 * suite passes with or without the candidate TODOs being implemented.
 */
@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRequestRepository rideRequestRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private RideAssignmentService rideAssignmentService;

    @InjectMocks
    private RideService rideService;

    @Test
    @DisplayName("persists the ride and delegates assignment to the assignment service")
    void createsRideThenDelegates() {
        RideRequest saved = rideWithId(7L, RideStatus.REQUESTED, null);
        RideRequest assigned = rideWithId(7L, RideStatus.DRIVER_ASSIGNED, 12L);
        when(rideRequestRepository.save(any(RideRequest.class))).thenReturn(saved);
        when(rideAssignmentService.assignDriver(7L)).thenReturn(assigned);

        RideResponse response = rideService.requestRide(new CreateRideRequest(101L, 28.6139, 77.2090));

        assertThat(response).isEqualTo(new RideResponse(7L, 12L, RideStatus.DRIVER_ASSIGNED));
        ArgumentCaptor<RideRequest> captor = ArgumentCaptor.forClass(RideRequest.class);
        verify(rideRequestRepository).save(captor.capture());
        assertThat(captor.getValue().getCustomerId()).isEqualTo(101L);
        assertThat(captor.getValue().getStatus()).isEqualTo(RideStatus.REQUESTED);
    }

    @Test
    @DisplayName("rejects an out-of-range pickup before touching the database")
    void rejectsInvalidPickup() {
        CreateRideRequest request = new CreateRideRequest(101L, 95.0, 77.2090);

        assertThatThrownBy(() -> rideService.requestRide(request)).isInstanceOf(InvalidLocationException.class);

        verifyNoInteractions(rideRequestRepository);
        verifyNoInteractions(rideAssignmentService);
    }

    @Test
    @DisplayName("getRide throws when the ride does not exist")
    void getRideThrowsForUnknownId() {
        when(rideRequestRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> rideService.getRide(42L)).isInstanceOf(RideNotFoundException.class);
    }

    @Test
    @DisplayName("cancelling an assigned ride releases the driver")
    void cancelReleasesDriver() {
        RideRequest ride = rideWithId(5L, RideStatus.DRIVER_ASSIGNED, 12L);
        Driver driver = new Driver("Assigned", 28.6, 77.2, DriverStatus.BUSY);
        when(rideRequestRepository.findById(5L)).thenReturn(Optional.of(ride));
        when(driverRepository.findByIdForUpdate(12L)).thenReturn(Optional.of(driver));

        RideRequest cancelled = rideService.cancelRide(5L);

        assertThat(cancelled.getStatus()).isEqualTo(RideStatus.CANCELLED);
        assertThat(driver.getStatus()).isEqualTo(DriverStatus.AVAILABLE);
    }

    @Test
    @DisplayName("cancelling an unassigned ride does not look up any driver")
    void cancelWithoutDriverSkipsDriverLookup() {
        RideRequest ride = rideWithId(5L, RideStatus.REQUESTED, null);
        when(rideRequestRepository.findById(5L)).thenReturn(Optional.of(ride));

        assertThat(rideService.cancelRide(5L).getStatus()).isEqualTo(RideStatus.CANCELLED);

        verify(driverRepository, never()).findByIdForUpdate(anyLong());
    }

    private RideRequest rideWithId(Long id, RideStatus status, Long assignedDriverId) {
        RideRequest ride = new RideRequest(101L, 28.6139, 77.2090);
        ReflectionTestUtils.setField(ride, "id", id);
        ride.setStatus(status);
        ride.setAssignedDriverId(assignedDriverId);
        return ride;
    }
}

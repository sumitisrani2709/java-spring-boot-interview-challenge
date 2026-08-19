package com.example.cabbooking.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.cabbooking.domain.Driver;
import com.example.cabbooking.domain.DriverStatus;
import com.example.cabbooking.domain.RideRequest;
import com.example.cabbooking.domain.RideStatus;
import com.example.cabbooking.repository.DriverRepository;
import com.example.cabbooking.repository.RideRequestRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Exposes the double-assignment race.
 *
 * <p>Several customers request a ride at the same instant while exactly <em>one</em>
 * driver is available. A naive implementation reads the driver, sees {@code AVAILABLE}
 * in every thread, and then writes {@code BUSY} from every thread - so the same driver
 * ends up on several rides:
 *
 * <pre>
 *   Ride A -> finds Driver 10 AVAILABLE
 *   Ride B -> finds Driver 10 AVAILABLE
 *   Ride A -> assigns Driver 10
 *   Ride B -> also assigns Driver 10   &lt;-- wrong
 * </pre>
 *
 * <p>This test is disabled by default because it is a discussion prop rather than part
 * of the core task. Remove {@link Disabled} to see whether your implementation holds up.
 */
@Disabled("Enable this once the core task is working - see the class javadoc")
@SpringBootTest
class ConcurrentAssignmentTest {

    private static final int CONCURRENT_CUSTOMERS = 8;
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
    @DisplayName("one available driver can only ever be assigned to one ride")
    void aSingleDriverIsNeverAssignedTwice() throws Exception {
        Driver onlyDriver = driverRepository.saveAndFlush(
                new Driver("Only one on shift", 28.6145, 77.2100, DriverStatus.AVAILABLE));

        List<Long> rideIds = new ArrayList<>();
        for (int i = 0; i < CONCURRENT_CUSTOMERS; i++) {
            rideIds.add(rideRequestRepository
                    .saveAndFlush(new RideRequest(100L + i, PICKUP_LAT, PICKUP_LON))
                    .getId());
        }

        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(CONCURRENT_CUSTOMERS);
        AtomicInteger succeeded = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(CONCURRENT_CUSTOMERS);

        try {
            for (Long rideId : rideIds) {
                pool.submit(() -> {
                    try {
                        startGun.await();
                        rideAssignmentService.assignDriver(rideId);
                        succeeded.incrementAndGet();
                    } catch (Exception expectedForLosers) {
                        // Losing threads should fail cleanly (no driver / lock conflict).
                    } finally {
                        finished.countDown();
                    }
                });
            }
            startGun.countDown();
            assertThat(finished.await(30, TimeUnit.SECONDS)).isTrue();
        } finally {
            pool.shutdownNow();
        }

        List<RideRequest> assignedRides = rideRequestRepository.findByStatus(RideStatus.DRIVER_ASSIGNED);

        assertThat(succeeded.get())
                .as("exactly one customer should have won the driver")
                .isEqualTo(1);
        assertThat(assignedRides)
                .as("only one ride may hold the single available driver")
                .hasSize(1);
        assertThat(assignedRides.get(0).getAssignedDriverId()).isEqualTo(onlyDriver.getId());
        assertThat(driverRepository.findById(onlyDriver.getId()).orElseThrow().getStatus())
                .isEqualTo(DriverStatus.BUSY);
    }
}

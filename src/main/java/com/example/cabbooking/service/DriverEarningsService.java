package com.example.cabbooking.service;

import com.example.cabbooking.dto.DriverEarningsResponse;
import com.example.cabbooking.entity.DriverEarning;
import com.example.cabbooking.entity.RideRequest;
import com.example.cabbooking.repository.DriverEarningRepository;
import com.example.cabbooking.repository.RideRequestRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Read side of the earnings ledger: what a driver has been paid, and for which rides. */
@Service
public class DriverEarningsService {

    private final DriverEarningRepository driverEarningRepository;
    private final RideRequestRepository rideRequestRepository;

    public DriverEarningsService(
            DriverEarningRepository driverEarningRepository, RideRequestRepository rideRequestRepository) {
        this.driverEarningRepository = driverEarningRepository;
        this.rideRequestRepository = rideRequestRepository;
    }

    /** Every ride this driver has been paid for, plus the running total. */
    @Transactional(readOnly = true)
    public DriverEarningsResponse getEarnings(Long driverId) {
        List<DriverEarning> earnings = driverEarningRepository.findAll();

        List<DriverEarningsResponse.Entry> entries = new ArrayList<>();
        double totalEarned = 0.0;

        for (DriverEarning earning : earnings) {
            if (!earning.getDriverId().equals(driverId)) {
                continue;
            }

            RideRequest ride = rideRequestRepository.findById(earning.getRideId()).orElse(null);
            entries.add(new DriverEarningsResponse.Entry(
                    earning.getRideId(),
                    earning.getAmount(),
                    ride != null ? ride.getPickupLatitude() : 0.0,
                    ride != null ? ride.getPickupLongitude() : 0.0,
                    earning.getCreatedAt()));

            totalEarned = totalEarned + earning.getAmount();
        }

        return new DriverEarningsResponse(driverId, totalEarned, entries.size(), entries);
    }
}

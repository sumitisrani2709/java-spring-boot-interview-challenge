package com.example.cabbooking.repository;

import com.example.cabbooking.domain.RideRequest;
import com.example.cabbooking.domain.RideStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRequestRepository extends JpaRepository<RideRequest, Long> {

    List<RideRequest> findByCustomerId(Long customerId);

    List<RideRequest> findByStatus(RideStatus status);

    long countByAssignedDriverIdAndStatus(Long assignedDriverId, RideStatus status);
}

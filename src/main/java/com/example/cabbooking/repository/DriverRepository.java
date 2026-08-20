package com.example.cabbooking.repository;

import com.example.cabbooking.entity.Driver;
import com.example.cabbooking.entity.enums.DriverStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByStatus(DriverStatus status);

    List<Driver> findByStatusOrderByIdAsc(DriverStatus status);
}

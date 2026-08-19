package com.example.cabbooking.repository;

import com.example.cabbooking.domain.Driver;
import com.example.cabbooking.domain.DriverStatus;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    /** Drivers in a given status. Backed by the {@code idx_driver_status} index. */
    List<Driver> findByStatus(DriverStatus status);

    /** Same as {@link #findByStatus} but with a stable ordering, which makes ties deterministic. */
    List<Driver> findByStatusOrderByIdAsc(DriverStatus status);

    /**
     * Loads a driver row and holds a write lock on it until the surrounding transaction
     * commits. Must be called inside a transaction.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Driver d where d.id = :id")
    Optional<Driver> findByIdForUpdate(@Param("id") Long id);

    // TODO (candidate, optional): if you decide the matching query itself should run in the
    // database rather than in application memory, add the query you would use here.
}

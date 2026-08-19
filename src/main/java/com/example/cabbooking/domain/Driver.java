package com.example.cabbooking.domain;

import com.example.cabbooking.dto.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "driver", indexes = @Index(name = "idx_driver_status", columnList = "status"))
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private DriverStatus status;

    /**
     * JPA optimistic-locking version. Hibernate bumps this on every update and fails
     * with an {@code OptimisticLockingFailureException} if the row changed underneath us.
     */
    @Version
    private Long version;

    protected Driver() {
        // required by JPA
    }

    public Driver(String name, double latitude, double longitude, DriverStatus status) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    /** Convenience accessor so distance code can work with a single value object. */
    public Location location() {
        return new Location(latitude, longitude);
    }

    public boolean isAvailable() {
        return status == DriverStatus.AVAILABLE;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Driver{id=" + id + ", name='" + name + "', status=" + status + "}";
    }
}

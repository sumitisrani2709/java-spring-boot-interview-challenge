package com.example.cabbooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/** What a driver earned for one completed ride. */
@Entity
@Table(name = "driver_earning")
public class DriverEarning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long rideId;

    @Column(nullable = false)
    private Long driverId;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected DriverEarning() {
        // required by JPA
    }

    public DriverEarning(Long rideId, Long driverId, double amount, LocalDateTime createdAt) {
        this.rideId = rideId;
        this.driverId = driverId;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getRideId() {
        return rideId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "DriverEarning{id=" + id + ", rideId=" + rideId + ", driverId=" + driverId + ", amount=" + amount + "}";
    }
}

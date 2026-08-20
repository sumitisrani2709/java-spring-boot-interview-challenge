package com.example.cabbooking.entity;

import com.example.cabbooking.dto.Location;
import com.example.cabbooking.entity.enums.RideStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ride_request")
public class RideRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private double pickupLatitude;

    @Column(nullable = false)
    private double pickupLongitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private RideStatus status;

    private Long assignedDriverId;

    protected RideRequest() {
    }

    public RideRequest(Long customerId, double pickupLatitude, double pickupLongitude) {
        this.customerId = customerId;
        this.pickupLatitude = pickupLatitude;
        this.pickupLongitude = pickupLongitude;
        this.status = RideStatus.REQUESTED;
    }

    public Location pickupLocation() {
        return new Location(pickupLatitude, pickupLongitude);
    }

    public void assignTo(Long driverId) {
        this.assignedDriverId = driverId;
        this.status = RideStatus.DRIVER_ASSIGNED;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public double getPickupLatitude() {
        return pickupLatitude;
    }

    public double getPickupLongitude() {
        return pickupLongitude;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public Long getAssignedDriverId() {
        return assignedDriverId;
    }

    public void setAssignedDriverId(Long assignedDriverId) {
        this.assignedDriverId = assignedDriverId;
    }

    @Override
    public String toString() {
        return "RideRequest{id=" + id + ", status=" + status + ", assignedDriverId=" + assignedDriverId + "}";
    }
}

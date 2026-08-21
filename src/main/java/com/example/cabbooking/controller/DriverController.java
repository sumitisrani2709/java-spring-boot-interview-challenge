package com.example.cabbooking.controller;

import com.example.cabbooking.dto.DriverEarningsResponse;
import com.example.cabbooking.dto.DriverResponse;
import com.example.cabbooking.dto.UpdateDriverStatusRequest;
import com.example.cabbooking.service.DriverEarningsService;
import com.example.cabbooking.service.DriverService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;
    private final DriverEarningsService driverEarningsService;

    public DriverController(DriverService driverService, DriverEarningsService driverEarningsService) {
        this.driverService = driverService;
        this.driverEarningsService = driverEarningsService;
    }

    @GetMapping
    public List<DriverResponse> listDrivers() {
        return driverService.findAll().stream().map(DriverResponse::from).toList();
    }

    @GetMapping("/{driverId}")
    public DriverResponse getDriver(@PathVariable Long driverId) {
        return DriverResponse.from(driverService.findById(driverId));
    }

    @PatchMapping("/{driverId}/status")
    public DriverResponse updateStatus(
            @PathVariable Long driverId, @Valid @RequestBody UpdateDriverStatusRequest request) {
        return DriverResponse.from(driverService.updateStatus(driverId, request.status()));
    }

    @PostMapping("/{driverId}/earnings")
    public DriverEarningsResponse getEarnings(@PathVariable Long driverId) {
        return driverEarningsService.getEarnings(driverId);
    }
}

package com.example.cabbooking.controller;

import com.example.cabbooking.dto.CompleteRideRequest;
import com.example.cabbooking.dto.CreateRideRequest;
import com.example.cabbooking.dto.RideCompletionResponse;
import com.example.cabbooking.dto.RideResponse;
import com.example.cabbooking.service.RideCompletionService;
import com.example.cabbooking.service.RideService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;
    private final RideCompletionService rideCompletionService;

    public RideController(RideService rideService, RideCompletionService rideCompletionService) {
        this.rideService = rideService;
        this.rideCompletionService = rideCompletionService;
    }

    @PostMapping
    public ResponseEntity<RideResponse> createRide(@Valid @RequestBody CreateRideRequest request) {
        RideResponse response = rideService.requestRide(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{rideId}")
    public RideResponse getRide(@PathVariable Long rideId) {
        return RideResponse.from(rideService.getRide(rideId));
    }

    @PostMapping("/{rideId}/cancel")
    public RideResponse cancelRide(@PathVariable Long rideId) {
        return RideResponse.from(rideService.cancelRide(rideId));
    }

    @PostMapping("/{rideId}/complete")
    public RideCompletionResponse completeRide(
            @PathVariable Long rideId, @Valid @RequestBody CompleteRideRequest request) {
        return rideCompletionService.completeRide(rideId, request);
    }
}

package com.example.cabbooking.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cabbooking.domain.Driver;
import com.example.cabbooking.domain.DriverStatus;
import com.example.cabbooking.repository.DriverRepository;
import com.example.cabbooking.repository.RideRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RideControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
    @DisplayName("POST /api/rides returns 201 with the assigned driver")
    void createsRideAndAssignsDriver() throws Exception {
        Driver near = save("Near", 28.6145, 77.2100, DriverStatus.AVAILABLE);
        save("Far", 28.6200, 77.2200, DriverStatus.AVAILABLE);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(101L, 28.6139, 77.2090)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rideId").isNumber())
                .andExpect(jsonPath("$.driverId").value(near.getId()))
                .andExpect(jsonPath("$.status").value("DRIVER_ASSIGNED"));
    }

    @Test
    @DisplayName("POST /api/rides returns 503 when no driver is available")
    void returnsServiceUnavailableWhenNoDriver() throws Exception {
        save("Busy", 28.6145, 77.2100, DriverStatus.BUSY);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(101L, 28.6139, 77.2090)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").isNotEmpty());

        // The request itself is still recorded, so it could be retried later.
        assertThat(rideRequestRepository.findByCustomerId(101L)).hasSize(1);
    }

    @Test
    @DisplayName("POST /api/rides returns 400 for an out-of-range latitude")
    void rejectsOutOfRangeLatitude() throws Exception {
        save("Near", 28.6145, 77.2100, DriverStatus.AVAILABLE);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body(101L, 120.0, 77.2090)))
                .andExpect(status().isBadRequest());

        assertThat(rideRequestRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST /api/rides returns 400 when coordinates are missing")
    void rejectsMissingCoordinates() throws Exception {
        save("Near", 28.6145, 77.2100, DriverStatus.AVAILABLE);

        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":101}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/rides returns 400 when customerId is missing")
    void rejectsMissingCustomerId() throws Exception {
        mockMvc.perform(post("/api/rides")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pickupLatitude\":28.6139,\"pickupLongitude\":77.2090}"))
                .andExpect(status().isBadRequest());
    }

    private Driver save(String name, double latitude, double longitude, DriverStatus status) {
        return driverRepository.saveAndFlush(new Driver(name, latitude, longitude, status));
    }

    private String body(Long customerId, double latitude, double longitude) {
        return """
                {"customerId": %d, "pickupLatitude": %s, "pickupLongitude": %s}"""
                .formatted(customerId, latitude, longitude);
    }
}

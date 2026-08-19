package com.example.cabbooking.dto;

import com.example.cabbooking.domain.DriverStatus;
import jakarta.validation.constraints.NotNull;

/** Payload for {@code PATCH /api/drivers/{id}/status}. */
public record UpdateDriverStatusRequest(@NotNull(message = "status is required") DriverStatus status) {
}

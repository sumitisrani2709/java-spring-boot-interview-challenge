package com.example.cabbooking.dto;

import com.example.cabbooking.entity.enums.DriverStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateDriverStatusRequest(@NotNull(message = "status is required") DriverStatus status) {
}

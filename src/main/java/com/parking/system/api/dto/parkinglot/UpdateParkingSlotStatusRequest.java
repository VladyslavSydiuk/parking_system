package com.parking.system.api.dto.parkinglot;

import com.parking.system.domain.model.SlotStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UpdateParkingSlotStatusRequest(
        @Schema(description = "Manual slot status, only AVAILABLE or UNAVAILABLE are allowed", example = "UNAVAILABLE")
        @NotNull(message = "must not be null")
        SlotStatus status
) {
}

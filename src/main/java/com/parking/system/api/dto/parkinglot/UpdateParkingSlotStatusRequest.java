package com.parking.system.api.dto.parkinglot;

import com.parking.system.domain.model.SlotStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateParkingSlotStatusRequest(
        @NotNull(message = "must not be null")
        SlotStatus status
) {
}

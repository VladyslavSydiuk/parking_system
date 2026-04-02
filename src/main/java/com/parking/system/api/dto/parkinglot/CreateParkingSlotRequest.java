package com.parking.system.api.dto.parkinglot;

import com.parking.system.domain.model.SlotType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateParkingSlotRequest(
        @NotBlank(message = "must not be blank")
        String slotCode,
        @NotNull(message = "must not be null")
        SlotType slotType
) {
}

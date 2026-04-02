package com.parking.system.api.dto.parkinglot;

import com.parking.system.domain.model.SlotType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateParkingSlotRequest(
        @Schema(description = "Unique slot code inside the level", example = "C-01")
        @NotBlank(message = "must not be blank")
        String slotCode,
        @Schema(description = "Parking slot type", example = "COMPACT")
        @NotNull(message = "must not be null")
        SlotType slotType
) {
}

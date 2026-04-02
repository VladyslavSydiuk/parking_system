package com.parking.system.api.dto.parkinglot;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateParkingLevelRequest(
        @Schema(description = "Numeric level identifier inside the parking lot", example = "1")
        @NotNull(message = "must not be null")
        Integer levelNumber
) {
}

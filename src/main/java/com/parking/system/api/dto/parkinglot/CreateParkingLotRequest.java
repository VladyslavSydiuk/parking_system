package com.parking.system.api.dto.parkinglot;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CreateParkingLotRequest(
        @Schema(description = "Human-readable parking lot name", example = "Central Plaza")
        @NotBlank(message = "must not be blank")
        String name
) {
}

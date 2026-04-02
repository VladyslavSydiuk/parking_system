package com.parking.system.api.dto.parkinglot;

import jakarta.validation.constraints.NotNull;

public record CreateParkingLevelRequest(
        @NotNull(message = "must not be null")
        Integer levelNumber
) {
}

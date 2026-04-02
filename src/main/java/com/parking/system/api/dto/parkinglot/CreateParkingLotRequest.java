package com.parking.system.api.dto.parkinglot;

import jakarta.validation.constraints.NotBlank;

public record CreateParkingLotRequest(
        @NotBlank(message = "must not be blank")
        String name
) {
}

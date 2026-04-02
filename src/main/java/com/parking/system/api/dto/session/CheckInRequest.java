package com.parking.system.api.dto.session;

import com.parking.system.domain.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckInRequest(
        @NotNull(message = "must not be null")
        UUID parkingLotId,
        @NotBlank(message = "must not be blank")
        String licensePlate,
        @NotNull(message = "must not be null")
        VehicleType vehicleType
) {
}

package com.parking.system.api.dto.session;

import com.parking.system.domain.model.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckInRequest(
        @Schema(description = "Parking lot identifier", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        @NotNull(message = "must not be null")
        UUID parkingLotId,
        @Schema(description = "Vehicle license plate", example = "AA-1234-BB")
        @NotBlank(message = "must not be blank")
        String licensePlate,
        @Schema(description = "Vehicle type", example = "CAR")
        @NotNull(message = "must not be null")
        VehicleType vehicleType
) {
}

package com.parking.system.api.dto.parkinglot;

import java.util.List;
import java.util.UUID;

public record ParkingLotResponse(
        UUID id,
        String name,
        List<ParkingLevelResponse> levels
) {
}

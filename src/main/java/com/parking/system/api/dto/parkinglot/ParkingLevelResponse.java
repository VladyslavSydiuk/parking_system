package com.parking.system.api.dto.parkinglot;

import java.util.List;
import java.util.UUID;

public record ParkingLevelResponse(
        UUID id,
        Integer levelNumber,
        List<ParkingSlotResponse> slots
) {
}

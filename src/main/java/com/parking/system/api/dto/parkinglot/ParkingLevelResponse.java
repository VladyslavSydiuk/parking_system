package com.parking.system.api.dto.parkinglot;

import java.util.List;

public record ParkingLevelResponse(
        Long id,
        Integer levelNumber,
        List<ParkingSlotResponse> slots
) {
}

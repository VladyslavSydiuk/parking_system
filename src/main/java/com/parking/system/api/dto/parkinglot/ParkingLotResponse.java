package com.parking.system.api.dto.parkinglot;

import java.util.List;

public record ParkingLotResponse(
        Long id,
        String name,
        List<ParkingLevelResponse> levels
) {
}

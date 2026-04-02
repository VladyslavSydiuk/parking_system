package com.parking.system.api.dto.parkinglot;

import com.parking.system.domain.model.SlotStatus;
import com.parking.system.domain.model.SlotType;

public record ParkingSlotResponse(
        Long id,
        String slotCode,
        SlotType slotType,
        SlotStatus status
) {
}

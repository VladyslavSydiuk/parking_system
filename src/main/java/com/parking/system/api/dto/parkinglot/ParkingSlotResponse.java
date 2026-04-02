package com.parking.system.api.dto.parkinglot;

import com.parking.system.domain.model.SlotStatus;
import com.parking.system.domain.model.SlotType;

import java.util.UUID;

public record ParkingSlotResponse(
        UUID id,
        String slotCode,
        SlotType slotType,
        SlotStatus status
) {
}

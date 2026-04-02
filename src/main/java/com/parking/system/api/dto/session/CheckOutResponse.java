package com.parking.system.api.dto.session;

import com.parking.system.domain.model.SlotType;
import com.parking.system.domain.model.VehicleType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CheckOutResponse(
        UUID sessionId,
        String licensePlate,
        VehicleType vehicleType,
        UUID parkingLotId,
        String parkingLotName,
        UUID levelId,
        Integer levelNumber,
        UUID slotId,
        String slotCode,
        SlotType slotType,
        Instant entryTime,
        Instant exitTime,
        long durationMinutes,
        BigDecimal totalFee
) {
}

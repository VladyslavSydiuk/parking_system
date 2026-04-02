package com.parking.system.api.dto.session;

import com.parking.system.domain.model.SlotType;
import com.parking.system.domain.model.VehicleType;

import java.math.BigDecimal;
import java.time.Instant;

public record CheckOutResponse(
        Long sessionId,
        String licensePlate,
        VehicleType vehicleType,
        Long parkingLotId,
        String parkingLotName,
        Long levelId,
        Integer levelNumber,
        Long slotId,
        String slotCode,
        SlotType slotType,
        Instant entryTime,
        Instant exitTime,
        long durationMinutes,
        BigDecimal totalFee
) {
}

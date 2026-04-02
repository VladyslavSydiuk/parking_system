package com.parking.system.api.mapper;

import com.parking.system.api.dto.parkinglot.ParkingLevelResponse;
import com.parking.system.api.dto.parkinglot.ParkingLotResponse;
import com.parking.system.api.dto.parkinglot.ParkingSlotResponse;
import com.parking.system.domain.model.ParkingLevel;
import com.parking.system.domain.model.ParkingLot;
import com.parking.system.domain.model.ParkingSlot;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ParkingLotResponseMapper {

    public ParkingLotResponse toResponse(ParkingLot parkingLot) {
        List<ParkingLevelResponse> levels = parkingLot.getLevels()
                .stream()
                .sorted(Comparator.comparing(ParkingLevel::getLevelNumber))
                .map(this::toResponse)
                .toList();

        return new ParkingLotResponse(parkingLot.getId(), parkingLot.getName(), levels);
    }

    public ParkingLevelResponse toResponse(ParkingLevel level) {
        List<ParkingSlotResponse> slots = level.getSlots()
                .stream()
                .sorted(Comparator.comparing(ParkingSlot::getSlotCode))
                .map(this::toResponse)
                .toList();

        return new ParkingLevelResponse(level.getId(), level.getLevelNumber(), slots);
    }

    public ParkingSlotResponse toResponse(ParkingSlot slot) {
        return new ParkingSlotResponse(
                slot.getId(),
                slot.getSlotCode(),
                slot.getSlotType(),
                slot.getStatus()
        );
    }
}

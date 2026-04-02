package com.parking.system.api.mapper;

import com.parking.system.api.dto.session.ActiveParkingSessionResponse;
import com.parking.system.api.dto.session.CheckInResponse;
import com.parking.system.api.dto.session.CheckOutResponse;
import com.parking.system.domain.model.ParkingSession;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ParkingSessionResponseMapper {

    public CheckInResponse toCheckInResponse(ParkingSession session) {
        return new CheckInResponse(
                session.getId(),
                session.getLicensePlate(),
                session.getVehicleType(),
                session.getParkingLotId(),
                session.getParkingLotName(),
                session.getLevelId(),
                session.getLevelNumber(),
                session.getSlotId(),
                session.getSlotCode(),
                session.getSlotType(),
                session.getEntryTime()
        );
    }

    public ActiveParkingSessionResponse toActiveSessionResponse(ParkingSession session) {
        return new ActiveParkingSessionResponse(
                session.getId(),
                session.getLicensePlate(),
                session.getVehicleType(),
                session.getParkingLotId(),
                session.getParkingLotName(),
                session.getLevelId(),
                session.getLevelNumber(),
                session.getSlotId(),
                session.getSlotCode(),
                session.getSlotType(),
                session.getEntryTime()
        );
    }

    public CheckOutResponse toCheckOutResponse(ParkingSession session) {
        long durationMinutes = Duration.between(session.getEntryTime(), session.getExitTime()).toMinutes();

        return new CheckOutResponse(
                session.getId(),
                session.getLicensePlate(),
                session.getVehicleType(),
                session.getParkingLotId(),
                session.getParkingLotName(),
                session.getLevelId(),
                session.getLevelNumber(),
                session.getSlotId(),
                session.getSlotCode(),
                session.getSlotType(),
                session.getEntryTime(),
                session.getExitTime(),
                durationMinutes,
                session.getTotalFee()
        );
    }
}

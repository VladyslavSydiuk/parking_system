package com.parking.system.application.service;

import com.parking.system.api.dto.session.ActiveParkingSessionResponse;
import com.parking.system.api.dto.session.CheckInRequest;
import com.parking.system.api.dto.session.CheckInResponse;
import com.parking.system.api.dto.session.CheckOutResponse;
import com.parking.system.api.mapper.ParkingSessionResponseMapper;
import com.parking.system.application.exception.ConflictException;
import com.parking.system.application.exception.ResourceNotFoundException;
import com.parking.system.domain.model.ParkingLot;
import com.parking.system.domain.model.ParkingSession;
import com.parking.system.domain.model.ParkingSlot;
import com.parking.system.domain.model.SessionStatus;
import com.parking.system.domain.policy.VehicleSlotCompatibilityPolicy;
import com.parking.system.domain.pricing.FeeCalculationStrategyResolver;
import com.parking.system.infrastructure.persistence.ParkingLotRepository;
import com.parking.system.infrastructure.persistence.ParkingSessionRepository;
import com.parking.system.infrastructure.persistence.ParkingSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class ParkingSessionService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final VehicleSlotCompatibilityPolicy compatibilityPolicy;
    private final FeeCalculationStrategyResolver feeCalculationStrategyResolver;
    private final ParkingSessionResponseMapper parkingSessionResponseMapper;
    private final Clock clock;

    public ParkingSessionService(
            ParkingLotRepository parkingLotRepository,
            ParkingSlotRepository parkingSlotRepository,
            ParkingSessionRepository parkingSessionRepository,
            VehicleSlotCompatibilityPolicy compatibilityPolicy,
            FeeCalculationStrategyResolver feeCalculationStrategyResolver,
            ParkingSessionResponseMapper parkingSessionResponseMapper,
            Clock clock
    ) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingSessionRepository = parkingSessionRepository;
        this.compatibilityPolicy = compatibilityPolicy;
        this.feeCalculationStrategyResolver = feeCalculationStrategyResolver;
        this.parkingSessionResponseMapper = parkingSessionResponseMapper;
        this.clock = clock;
    }

    public CheckInResponse checkIn(CheckInRequest request) {
        ParkingLot parkingLot = parkingLotRepository.findById(request.parkingLotId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot '%s' was not found".formatted(request.parkingLotId())));

        String licensePlate = normalizeLicensePlate(request.licensePlate());
        if (parkingSessionRepository.existsByLicensePlateIgnoreCaseAndStatus(licensePlate, SessionStatus.ACTIVE)) {
            throw new ConflictException("Vehicle '%s' already has an active parking session".formatted(licensePlate));
        }

        ParkingSlot slot = parkingSlotRepository.findAssignableSlotsByParkingLotId(parkingLot.getId())
                .stream()
                .filter(ParkingSlot::isAvailableForAssignment)
                .filter(candidate -> compatibilityPolicy.isCompatible(request.vehicleType(), candidate.getSlotType()))
                .findFirst()
                .orElseThrow(() -> new ConflictException(
                        "No compatible parking slot is available for vehicle type %s".formatted(request.vehicleType())
                ));

        slot.markOccupied();

        ParkingSession session = parkingSessionRepository.save(new ParkingSession(
                licensePlate,
                request.vehicleType(),
                parkingLot.getId(),
                parkingLot.getName(),
                slot.getLevel().getId(),
                slot.getLevel().getLevelNumber(),
                slot.getId(),
                slot.getSlotCode(),
                slot.getSlotType(),
                Instant.now(clock)
        ));

        return parkingSessionResponseMapper.toCheckInResponse(session);
    }

    public CheckOutResponse checkOut(Long sessionId) {
        ParkingSession session = parkingSessionRepository.findByIdAndStatus(sessionId, SessionStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active parking session '%s' was not found".formatted(sessionId)));

        ParkingSlot slot = parkingSlotRepository.findById(session.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot '%s' was not found".formatted(session.getSlotId())));

        Instant exitTime = Instant.now(clock);
        session.complete(
                exitTime,
                feeCalculationStrategyResolver.resolve(session.getVehicleType()).calculate(session.getEntryTime(), exitTime)
        );
        slot.markAvailable();

        return parkingSessionResponseMapper.toCheckOutResponse(session);
    }

    @Transactional(readOnly = true)
    public List<ActiveParkingSessionResponse> getActiveSessions() {
        return parkingSessionRepository.findAllByStatusOrderByEntryTimeAsc(SessionStatus.ACTIVE)
                .stream()
                .map(parkingSessionResponseMapper::toActiveSessionResponse)
                .toList();
    }

    private String normalizeLicensePlate(String licensePlate) {
        String normalized = licensePlate == null ? null : licensePlate.trim().toUpperCase();
        if (normalized == null || normalized.isBlank()) {
            throw new IllegalArgumentException("license plate must not be blank");
        }
        return normalized;
    }
}

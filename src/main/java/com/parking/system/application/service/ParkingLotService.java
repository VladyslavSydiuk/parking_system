package com.parking.system.application.service;

import com.parking.system.api.dto.parkinglot.CreateParkingLevelRequest;
import com.parking.system.api.dto.parkinglot.CreateParkingLotRequest;
import com.parking.system.api.dto.parkinglot.CreateParkingSlotRequest;
import com.parking.system.api.dto.parkinglot.ParkingLevelResponse;
import com.parking.system.api.dto.parkinglot.ParkingLotResponse;
import com.parking.system.api.dto.parkinglot.ParkingSlotResponse;
import com.parking.system.api.dto.parkinglot.UpdateParkingSlotStatusRequest;
import com.parking.system.api.mapper.ParkingLotResponseMapper;
import com.parking.system.application.exception.ConflictException;
import com.parking.system.application.exception.ResourceNotFoundException;
import com.parking.system.domain.model.ParkingLevel;
import com.parking.system.domain.model.ParkingLot;
import com.parking.system.domain.model.ParkingSlot;
import com.parking.system.domain.model.SessionStatus;
import com.parking.system.domain.model.SlotStatus;
import com.parking.system.infrastructure.persistence.ParkingLevelRepository;
import com.parking.system.infrastructure.persistence.ParkingLotRepository;
import com.parking.system.infrastructure.persistence.ParkingSessionRepository;
import com.parking.system.infrastructure.persistence.ParkingSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingLevelRepository parkingLevelRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final ParkingLotResponseMapper parkingLotResponseMapper;

    public ParkingLotService(
            ParkingLotRepository parkingLotRepository,
            ParkingLevelRepository parkingLevelRepository,
            ParkingSlotRepository parkingSlotRepository,
            ParkingSessionRepository parkingSessionRepository,
            ParkingLotResponseMapper parkingLotResponseMapper
    ) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingLevelRepository = parkingLevelRepository;
        this.parkingSlotRepository = parkingSlotRepository;
        this.parkingSessionRepository = parkingSessionRepository;
        this.parkingLotResponseMapper = parkingLotResponseMapper;
    }

    @Transactional(readOnly = true)
    public List<ParkingLotResponse> getParkingLots() {
        return parkingLotRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(ParkingLot::getName))
                .map(parkingLotResponseMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ParkingLotResponse getParkingLot(UUID parkingLotId) {
        return parkingLotResponseMapper.toResponse(findParkingLot(parkingLotId));
    }

    public ParkingLotResponse createParkingLot(CreateParkingLotRequest request) {
        String parkingLotName = normalizeName(request.name(), "parking lot name");
        if (parkingLotRepository.existsByNameIgnoreCase(parkingLotName)) {
            throw new ConflictException("Parking lot with name '%s' already exists".formatted(parkingLotName));
        }

        ParkingLot parkingLot = parkingLotRepository.save(new ParkingLot(parkingLotName));
        return parkingLotResponseMapper.toResponse(parkingLot);
    }

    public void deleteParkingLot(UUID parkingLotId) {
        ParkingLot parkingLot = findParkingLot(parkingLotId);
        if (parkingSessionRepository.existsByParkingLotIdAndStatus(parkingLotId, SessionStatus.ACTIVE)) {
            throw new ConflictException("Parking lot '%s' has active parking sessions".formatted(parkingLot.getName()));
        }

        parkingLotRepository.delete(parkingLot);
    }

    public ParkingLevelResponse addLevel(UUID parkingLotId, CreateParkingLevelRequest request) {
        ParkingLot parkingLot = findParkingLot(parkingLotId);
        if (parkingLevelRepository.existsByParkingLot_IdAndLevelNumber(parkingLotId, request.levelNumber())) {
            throw new ConflictException(
                    "Level %s already exists in parking lot '%s'".formatted(request.levelNumber(), parkingLot.getName())
            );
        }

        ParkingLevel level = new ParkingLevel(request.levelNumber());
        parkingLot.addLevel(level);
        parkingLotRepository.save(parkingLot);
        return parkingLotResponseMapper.toResponse(level);
    }

    public void deleteLevel(UUID parkingLotId, UUID levelId) {
        ParkingLevel level = findLevel(parkingLotId, levelId);
        if (parkingSessionRepository.existsByLevelIdAndStatus(levelId, SessionStatus.ACTIVE)) {
            throw new ConflictException("Level %s has active parking sessions".formatted(level.getLevelNumber()));
        }

        parkingLevelRepository.delete(level);
    }

    public ParkingSlotResponse addSlot(UUID parkingLotId, UUID levelId, CreateParkingSlotRequest request) {
        ParkingLevel level = findLevel(parkingLotId, levelId);
        String slotCode = normalizeCode(request.slotCode(), "slot code");
        if (parkingSlotRepository.existsByLevel_IdAndSlotCodeIgnoreCase(levelId, slotCode)) {
            throw new ConflictException(
                    "Slot '%s' already exists on level %s".formatted(slotCode, level.getLevelNumber())
            );
        }

        ParkingSlot slot = new ParkingSlot(slotCode, request.slotType());
        level.addSlot(slot);
        parkingLevelRepository.save(level);
        return parkingLotResponseMapper.toResponse(slot);
    }

    public void deleteSlot(UUID parkingLotId, UUID levelId, UUID slotId) {
        ParkingSlot slot = findSlot(parkingLotId, levelId, slotId);
        if (slot.getStatus() == SlotStatus.OCCUPIED || parkingSessionRepository.existsBySlotIdAndStatus(slotId, SessionStatus.ACTIVE)) {
            throw new ConflictException("Slot '%s' is currently occupied".formatted(slot.getSlotCode()));
        }

        parkingSlotRepository.delete(slot);
    }

    public ParkingSlotResponse updateSlotStatus(
            UUID parkingLotId,
            UUID levelId,
            UUID slotId,
            UpdateParkingSlotStatusRequest request
    ) {
        ParkingSlot slot = findSlot(parkingLotId, levelId, slotId);
        SlotStatus targetStatus = request.status();

        if (targetStatus == SlotStatus.OCCUPIED) {
            throw new IllegalArgumentException("Slot status cannot be set to OCCUPIED manually");
        }
        if (slot.getStatus() == SlotStatus.OCCUPIED) {
            throw new ConflictException("Occupied slot '%s' cannot be updated manually".formatted(slot.getSlotCode()));
        }

        if (targetStatus == SlotStatus.AVAILABLE) {
            slot.markAvailable();
        } else {
            slot.markUnavailable();
        }

        return parkingLotResponseMapper.toResponse(slot);
    }

    private ParkingLot findParkingLot(UUID parkingLotId) {
        return parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot '%s' was not found".formatted(parkingLotId)));
    }

    private ParkingLevel findLevel(UUID parkingLotId, UUID levelId) {
        findParkingLot(parkingLotId);
        return parkingLevelRepository.findByIdAndParkingLot_Id(levelId, parkingLotId)
                .orElseThrow(() -> new ResourceNotFoundException("Level '%s' was not found".formatted(levelId)));
    }

    private ParkingSlot findSlot(UUID parkingLotId, UUID levelId, UUID slotId) {
        findLevel(parkingLotId, levelId);
        return parkingSlotRepository.findByIdAndLevel_Id(slotId, levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Slot '%s' was not found".formatted(slotId)));
    }

    private String normalizeName(String value, String fieldName) {
        String normalized = value == null ? null : value.trim();
        if (normalized == null || normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    private String normalizeCode(String value, String fieldName) {
        return normalizeName(value, fieldName).toUpperCase();
    }
}

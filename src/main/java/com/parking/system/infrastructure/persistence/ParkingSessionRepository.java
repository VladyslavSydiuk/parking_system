package com.parking.system.infrastructure.persistence;

import com.parking.system.domain.model.ParkingSession;
import com.parking.system.domain.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, UUID> {

    boolean existsByLicensePlateIgnoreCaseAndStatus(String licensePlate, SessionStatus status);

    boolean existsByParkingLotIdAndStatus(UUID parkingLotId, SessionStatus status);

    boolean existsByLevelIdAndStatus(UUID levelId, SessionStatus status);

    boolean existsBySlotIdAndStatus(UUID slotId, SessionStatus status);

    List<ParkingSession> findAllByStatusOrderByEntryTimeAsc(SessionStatus status);

    Optional<ParkingSession> findByIdAndStatus(UUID id, SessionStatus status);
}

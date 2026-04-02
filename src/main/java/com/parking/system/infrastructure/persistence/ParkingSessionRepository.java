package com.parking.system.infrastructure.persistence;

import com.parking.system.domain.model.ParkingSession;
import com.parking.system.domain.model.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {

    boolean existsByLicensePlateIgnoreCaseAndStatus(String licensePlate, SessionStatus status);

    boolean existsByParkingLotIdAndStatus(Long parkingLotId, SessionStatus status);

    boolean existsByLevelIdAndStatus(Long levelId, SessionStatus status);

    boolean existsBySlotIdAndStatus(Long slotId, SessionStatus status);

    List<ParkingSession> findAllByStatusOrderByEntryTimeAsc(SessionStatus status);

    Optional<ParkingSession> findByIdAndStatus(Long id, SessionStatus status);
}

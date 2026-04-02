package com.parking.system.infrastructure.persistence;

import com.parking.system.domain.model.ParkingLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParkingLevelRepository extends JpaRepository<ParkingLevel, UUID> {

    boolean existsByParkingLot_IdAndLevelNumber(UUID parkingLotId, Integer levelNumber);

    Optional<ParkingLevel> findByIdAndParkingLot_Id(UUID id, UUID parkingLotId);
}

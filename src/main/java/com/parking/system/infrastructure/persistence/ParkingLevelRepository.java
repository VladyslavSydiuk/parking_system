package com.parking.system.infrastructure.persistence;

import com.parking.system.domain.model.ParkingLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingLevelRepository extends JpaRepository<ParkingLevel, Long> {

    boolean existsByParkingLot_IdAndLevelNumber(Long parkingLotId, Integer levelNumber);

    Optional<ParkingLevel> findByIdAndParkingLot_Id(Long id, Long parkingLotId);
}

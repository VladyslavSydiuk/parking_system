package com.parking.system.infrastructure.persistence;

import com.parking.system.domain.model.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {

    boolean existsByNameIgnoreCase(String name);

    Optional<ParkingLot> findByNameIgnoreCase(String name);
}

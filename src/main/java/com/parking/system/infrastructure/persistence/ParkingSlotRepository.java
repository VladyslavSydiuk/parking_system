package com.parking.system.infrastructure.persistence;

import com.parking.system.domain.model.ParkingSlot;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    boolean existsByLevel_IdAndSlotCodeIgnoreCase(Long levelId, String slotCode);

    Optional<ParkingSlot> findByIdAndLevel_Id(Long id, Long levelId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select slot
            from ParkingSlot slot
            join slot.level level
            join level.parkingLot lot
            where lot.id = :parkingLotId
            order by level.levelNumber asc, slot.slotCode asc
            """)
    List<ParkingSlot> findAssignableSlotsByParkingLotId(@Param("parkingLotId") Long parkingLotId);
}

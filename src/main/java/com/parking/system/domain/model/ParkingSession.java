package com.parking.system.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "parking_sessions",
        indexes = {
                @Index(name = "idx_parking_session_license_plate", columnList = "license_plate"),
                @Index(name = "idx_parking_session_status", columnList = "status")
        }
)
public class ParkingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", nullable = false, length = 20)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 30)
    private VehicleType vehicleType;

    @Column(name = "parking_lot_id", nullable = false)
    private Long parkingLotId;

    @Column(name = "parking_lot_name", nullable = false, length = 100)
    private String parkingLotName;

    @Column(name = "level_id", nullable = false)
    private Long levelId;

    @Column(name = "level_number", nullable = false)
    private Integer levelNumber;

    @Column(name = "slot_id", nullable = false)
    private Long slotId;

    @Column(name = "slot_code", nullable = false, length = 30)
    private String slotCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_type", nullable = false, length = 30)
    private SlotType slotType;

    @Column(name = "entry_time", nullable = false)
    private Instant entryTime;

    @Column(name = "exit_time")
    private Instant exitTime;

    @Column(name = "total_fee", precision = 10, scale = 2)
    private BigDecimal totalFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SessionStatus status;

    protected ParkingSession() {
    }

    public ParkingSession(
            String licensePlate,
            VehicleType vehicleType,
            Long parkingLotId,
            String parkingLotName,
            Long levelId,
            Integer levelNumber,
            Long slotId,
            String slotCode,
            SlotType slotType,
            Instant entryTime
    ) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.parkingLotId = parkingLotId;
        this.parkingLotName = parkingLotName;
        this.levelId = levelId;
        this.levelNumber = levelNumber;
        this.slotId = slotId;
        this.slotCode = slotCode;
        this.slotType = slotType;
        this.entryTime = entryTime;
        this.status = SessionStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public Long getParkingLotId() {
        return parkingLotId;
    }

    public String getParkingLotName() {
        return parkingLotName;
    }

    public Long getLevelId() {
        return levelId;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public Long getSlotId() {
        return slotId;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public Instant getEntryTime() {
        return entryTime;
    }

    public Instant getExitTime() {
        return exitTime;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public boolean isActive() {
        return status == SessionStatus.ACTIVE;
    }

    public void complete(Instant exitTime, BigDecimal totalFee) {
        this.exitTime = exitTime;
        this.totalFee = totalFee;
        this.status = SessionStatus.COMPLETED;
    }
}

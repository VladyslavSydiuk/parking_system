package com.parking.system.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.UUID;

@Entity
@Table(
        name = "parking_slots",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_parking_slot_code",
                columnNames = {"level_id", "slot_code"}
        )
)
public class ParkingSlot {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "slot_code", nullable = false, length = 30)
    private String slotCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot_type", nullable = false, length = 30)
    private SlotType slotType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SlotStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "level_id", nullable = false)
    private ParkingLevel level;

    protected ParkingSlot() {
    }

    public ParkingSlot(String slotCode, SlotType slotType) {
        this.slotCode = slotCode;
        this.slotType = slotType;
        this.status = SlotStatus.AVAILABLE;
    }

    public UUID getId() {
        return id;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public ParkingLevel getLevel() {
        return level;
    }

    public void attachTo(ParkingLevel level) {
        this.level = level;
    }

    public void detach() {
        this.level = null;
    }

    public boolean isAvailableForAssignment() {
        return status == SlotStatus.AVAILABLE;
    }

    public void markOccupied() {
        this.status = SlotStatus.OCCUPIED;
    }

    public void markAvailable() {
        this.status = SlotStatus.AVAILABLE;
    }

    public void markUnavailable() {
        this.status = SlotStatus.UNAVAILABLE;
    }
}

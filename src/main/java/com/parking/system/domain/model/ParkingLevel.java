package com.parking.system.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "parking_levels",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_parking_level_number",
                columnNames = {"parking_lot_id", "level_number"}
        )
)
public class ParkingLevel {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "level_number", nullable = false)
    private Integer levelNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLot parkingLot;

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ParkingSlot> slots = new ArrayList<>();

    protected ParkingLevel() {
    }

    public ParkingLevel(Integer levelNumber) {
        this.levelNumber = levelNumber;
    }

    public UUID getId() {
        return id;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public List<ParkingSlot> getSlots() {
        return Collections.unmodifiableList(slots);
    }

    public void attachTo(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    public void detach() {
        this.parkingLot = null;
    }

    public void addSlot(ParkingSlot slot) {
        slots.add(slot);
        slot.attachTo(this);
    }

    public void removeSlot(ParkingSlot slot) {
        slots.remove(slot);
        slot.detach();
    }
}

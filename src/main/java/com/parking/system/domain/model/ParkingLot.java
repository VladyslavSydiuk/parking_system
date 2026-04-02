package com.parking.system.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(
        name = "parking_lots",
        uniqueConstraints = @UniqueConstraint(name = "uk_parking_lot_name", columnNames = "name")
)
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ParkingLevel> levels = new ArrayList<>();

    protected ParkingLot() {
    }

    public ParkingLot(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ParkingLevel> getLevels() {
        return Collections.unmodifiableList(levels);
    }

    public void addLevel(ParkingLevel level) {
        levels.add(level);
        level.attachTo(this);
    }

    public void removeLevel(ParkingLevel level) {
        levels.remove(level);
        level.detach();
    }
}

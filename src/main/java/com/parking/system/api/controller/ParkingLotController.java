package com.parking.system.api.controller;

import com.parking.system.api.dto.parkinglot.CreateParkingLevelRequest;
import com.parking.system.api.dto.parkinglot.CreateParkingLotRequest;
import com.parking.system.api.dto.parkinglot.CreateParkingSlotRequest;
import com.parking.system.api.dto.parkinglot.ParkingLevelResponse;
import com.parking.system.api.dto.parkinglot.ParkingLotResponse;
import com.parking.system.api.dto.parkinglot.ParkingSlotResponse;
import com.parking.system.api.dto.parkinglot.UpdateParkingSlotStatusRequest;
import com.parking.system.application.service.ParkingLotService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parking-lots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @GetMapping
    public List<ParkingLotResponse> getParkingLots() {
        return parkingLotService.getParkingLots();
    }

    @GetMapping("/{parkingLotId}")
    public ParkingLotResponse getParkingLot(@PathVariable UUID parkingLotId) {
        return parkingLotService.getParkingLot(parkingLotId);
    }

    @PostMapping
    public ResponseEntity<ParkingLotResponse> createParkingLot(@Valid @RequestBody CreateParkingLotRequest request) {
        ParkingLotResponse response = parkingLotService.createParkingLot(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{parkingLotId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{parkingLotId}")
    public ResponseEntity<Void> deleteParkingLot(@PathVariable UUID parkingLotId) {
        parkingLotService.deleteParkingLot(parkingLotId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{parkingLotId}/levels")
    public ResponseEntity<ParkingLevelResponse> addLevel(
            @PathVariable UUID parkingLotId,
            @Valid @RequestBody CreateParkingLevelRequest request
    ) {
        ParkingLevelResponse response = parkingLotService.addLevel(parkingLotId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{levelId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{parkingLotId}/levels/{levelId}")
    public ResponseEntity<Void> deleteLevel(@PathVariable UUID parkingLotId, @PathVariable UUID levelId) {
        parkingLotService.deleteLevel(parkingLotId, levelId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{parkingLotId}/levels/{levelId}/slots")
    public ResponseEntity<ParkingSlotResponse> addSlot(
            @PathVariable UUID parkingLotId,
            @PathVariable UUID levelId,
            @Valid @RequestBody CreateParkingSlotRequest request
    ) {
        ParkingSlotResponse response = parkingLotService.addSlot(parkingLotId, levelId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{slotId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{parkingLotId}/levels/{levelId}/slots/{slotId}")
    public ResponseEntity<Void> deleteSlot(
            @PathVariable UUID parkingLotId,
            @PathVariable UUID levelId,
            @PathVariable UUID slotId
    ) {
        parkingLotService.deleteSlot(parkingLotId, levelId, slotId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{parkingLotId}/levels/{levelId}/slots/{slotId}/status")
    public ParkingSlotResponse updateSlotStatus(
            @PathVariable UUID parkingLotId,
            @PathVariable UUID levelId,
            @PathVariable UUID slotId,
            @Valid @RequestBody UpdateParkingSlotStatusRequest request
    ) {
        return parkingLotService.updateSlotStatus(parkingLotId, levelId, slotId, request);
    }
}

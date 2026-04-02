package com.parking.system.api.controller;

import com.parking.system.api.dto.parkinglot.CreateParkingLevelRequest;
import com.parking.system.api.dto.parkinglot.CreateParkingLotRequest;
import com.parking.system.api.dto.parkinglot.CreateParkingSlotRequest;
import com.parking.system.api.dto.parkinglot.ParkingLevelResponse;
import com.parking.system.api.dto.parkinglot.ParkingLotResponse;
import com.parking.system.api.dto.parkinglot.ParkingSlotResponse;
import com.parking.system.api.dto.parkinglot.UpdateParkingSlotStatusRequest;
import com.parking.system.application.service.ParkingLotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequestMapping("/api/v1/parking-lots")
@Tag(name = "Parking Lots", description = "Administrative endpoints for parking lots, levels, and parking slots.")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @GetMapping
    @Operation(summary = "Get all parking lots", description = "Returns the full list of parking lots with their levels and slots.")
    public List<ParkingLotResponse> getParkingLots() {
        return parkingLotService.getParkingLots();
    }

    @GetMapping("/{parkingLotId}")
    @Operation(summary = "Get parking lot by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Parking lot returned"),
            @ApiResponse(responseCode = "404", description = "Parking lot was not found", content = @Content(
                    schema = @Schema(implementation = com.parking.system.api.error.ApiErrorResponse.class)
            ))
    })
    public ParkingLotResponse getParkingLot(
            @Parameter(description = "Parking lot identifier")
            @PathVariable Long parkingLotId
    ) {
        return parkingLotService.getParkingLot(parkingLotId);
    }

    @PostMapping
    @Operation(summary = "Create parking lot", description = "Creates a new parking lot.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Parking lot created"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(
                    schema = @Schema(implementation = com.parking.system.api.error.ApiErrorResponse.class)
            )),
            @ApiResponse(responseCode = "409", description = "Parking lot already exists", content = @Content(
                    schema = @Schema(implementation = com.parking.system.api.error.ApiErrorResponse.class)
            ))
    })
    public ResponseEntity<ParkingLotResponse> createParkingLot(@Valid @RequestBody CreateParkingLotRequest request) {
        ParkingLotResponse response = parkingLotService.createParkingLot(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{parkingLotId}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @DeleteMapping("/{parkingLotId}")
    @Operation(summary = "Delete parking lot", description = "Deletes a parking lot if it has no active sessions.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Parking lot deleted"),
            @ApiResponse(responseCode = "404", description = "Parking lot was not found", content = @Content(
                    schema = @Schema(implementation = com.parking.system.api.error.ApiErrorResponse.class)
            )),
            @ApiResponse(responseCode = "409", description = "Parking lot has active sessions", content = @Content(
                    schema = @Schema(implementation = com.parking.system.api.error.ApiErrorResponse.class)
            ))
    })
    public ResponseEntity<Void> deleteParkingLot(
            @Parameter(description = "Parking lot identifier")
            @PathVariable Long parkingLotId
    ) {
        parkingLotService.deleteParkingLot(parkingLotId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{parkingLotId}/levels")
    @Operation(summary = "Add level", description = "Adds a new level to the specified parking lot.")
    public ResponseEntity<ParkingLevelResponse> addLevel(
            @Parameter(description = "Parking lot identifier")
            @PathVariable Long parkingLotId,
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
    @Operation(summary = "Delete level", description = "Deletes a level if it has no active sessions.")
    public ResponseEntity<Void> deleteLevel(
            @Parameter(description = "Parking lot identifier")
            @PathVariable Long parkingLotId,
            @Parameter(description = "Level identifier")
            @PathVariable Long levelId
    ) {
        parkingLotService.deleteLevel(parkingLotId, levelId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{parkingLotId}/levels/{levelId}/slots")
    @Operation(summary = "Add parking slot", description = "Creates a parking slot on the specified level.")
    public ResponseEntity<ParkingSlotResponse> addSlot(
            @Parameter(description = "Parking lot identifier")
            @PathVariable Long parkingLotId,
            @Parameter(description = "Level identifier")
            @PathVariable Long levelId,
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
    @Operation(summary = "Delete parking slot", description = "Deletes a parking slot if it is not occupied.")
    public ResponseEntity<Void> deleteSlot(
            @Parameter(description = "Parking lot identifier")
            @PathVariable Long parkingLotId,
            @Parameter(description = "Level identifier")
            @PathVariable Long levelId,
            @Parameter(description = "Parking slot identifier")
            @PathVariable Long slotId
    ) {
        parkingLotService.deleteSlot(parkingLotId, levelId, slotId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{parkingLotId}/levels/{levelId}/slots/{slotId}/status")
    @Operation(summary = "Update parking slot status", description = "Manually marks a slot as AVAILABLE or UNAVAILABLE.")
    public ParkingSlotResponse updateSlotStatus(
            @Parameter(description = "Parking lot identifier")
            @PathVariable Long parkingLotId,
            @Parameter(description = "Level identifier")
            @PathVariable Long levelId,
            @Parameter(description = "Parking slot identifier")
            @PathVariable Long slotId,
            @Valid @RequestBody UpdateParkingSlotStatusRequest request
    ) {
        return parkingLotService.updateSlotStatus(parkingLotId, levelId, slotId, request);
    }
}

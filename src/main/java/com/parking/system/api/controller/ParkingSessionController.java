package com.parking.system.api.controller;

import com.parking.system.api.dto.session.ActiveParkingSessionResponse;
import com.parking.system.api.dto.session.CheckInRequest;
import com.parking.system.api.dto.session.CheckInResponse;
import com.parking.system.api.dto.session.CheckOutResponse;
import com.parking.system.application.service.ParkingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parking-sessions")
@Tag(name = "Parking Sessions", description = "Vehicle check-in, check-out, and active session tracking.")
public class ParkingSessionController {

    private final ParkingSessionService parkingSessionService;

    public ParkingSessionController(ParkingSessionService parkingSessionService) {
        this.parkingSessionService = parkingSessionService;
    }

    @PostMapping("/check-in")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Check in vehicle", description = "Assigns the first compatible available slot and creates a parking session.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehicle checked in"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(
                    schema = @Schema(implementation = com.parking.system.api.error.ApiErrorResponse.class)
            )),
            @ApiResponse(responseCode = "404", description = "Parking lot was not found", content = @Content(
                    schema = @Schema(implementation = com.parking.system.api.error.ApiErrorResponse.class)
            )),
            @ApiResponse(responseCode = "409", description = "Vehicle already checked in or no slot available", content = @Content(
                    schema = @Schema(implementation = com.parking.system.api.error.ApiErrorResponse.class)
            ))
    })
    public CheckInResponse checkIn(@Valid @RequestBody CheckInRequest request) {
        return parkingSessionService.checkIn(request);
    }

    @PostMapping("/{sessionId}/check-out")
    @Operation(summary = "Check out vehicle", description = "Completes the parking session, frees the slot, and returns fee details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehicle checked out"),
            @ApiResponse(responseCode = "404", description = "Active parking session was not found", content = @Content(
                    schema = @Schema(implementation = com.parking.system.api.error.ApiErrorResponse.class)
            ))
    })
    public CheckOutResponse checkOut(
            @Parameter(description = "Parking session identifier")
            @PathVariable UUID sessionId
    ) {
        return parkingSessionService.checkOut(sessionId);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active parking sessions", description = "Returns all current active parking sessions ordered by entry time.")
    public List<ActiveParkingSessionResponse> getActiveSessions() {
        return parkingSessionService.getActiveSessions();
    }
}

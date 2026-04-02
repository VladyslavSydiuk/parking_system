package com.parking.system.api.controller;

import com.parking.system.api.dto.session.ActiveParkingSessionResponse;
import com.parking.system.api.dto.session.CheckInRequest;
import com.parking.system.api.dto.session.CheckInResponse;
import com.parking.system.api.dto.session.CheckOutResponse;
import com.parking.system.application.service.ParkingSessionService;
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
public class ParkingSessionController {

    private final ParkingSessionService parkingSessionService;

    public ParkingSessionController(ParkingSessionService parkingSessionService) {
        this.parkingSessionService = parkingSessionService;
    }

    @PostMapping("/check-in")
    @ResponseStatus(HttpStatus.CREATED)
    public CheckInResponse checkIn(@Valid @RequestBody CheckInRequest request) {
        return parkingSessionService.checkIn(request);
    }

    @PostMapping("/{sessionId}/check-out")
    public CheckOutResponse checkOut(@PathVariable UUID sessionId) {
        return parkingSessionService.checkOut(sessionId);
    }

    @GetMapping("/active")
    public List<ActiveParkingSessionResponse> getActiveSessions() {
        return parkingSessionService.getActiveSessions();
    }
}

package com.parking.system.application.service;

import com.parking.system.api.dto.session.CheckInRequest;
import com.parking.system.api.dto.session.CheckInResponse;
import com.parking.system.api.dto.session.CheckOutResponse;
import com.parking.system.application.exception.ConflictException;
import com.parking.system.domain.model.ParkingLevel;
import com.parking.system.domain.model.ParkingLot;
import com.parking.system.domain.model.ParkingSlot;
import com.parking.system.domain.model.SlotStatus;
import com.parking.system.domain.model.SlotType;
import com.parking.system.domain.model.VehicleType;
import com.parking.system.infrastructure.persistence.ParkingLotRepository;
import com.parking.system.infrastructure.persistence.ParkingSessionRepository;
import com.parking.system.infrastructure.persistence.ParkingSlotRepository;
import com.parking.system.support.MutableClock;
import com.parking.system.support.TestClockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestClockConfiguration.class)
class ParkingSessionServiceIntegrationTest {

    private static final Instant BASE_TIME = Instant.parse("2026-04-02T09:00:00Z");

    @Autowired
    private ParkingSessionService parkingSessionService;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    @Autowired
    private MutableClock clock;

    @BeforeEach
    void setUpClock() {
        clock.setInstant(BASE_TIME);
    }

    @AfterEach
    void cleanUp() {
        parkingSessionRepository.deleteAll();
        parkingLotRepository.deleteAll();
    }

    @Test
    void shouldAssignFirstCompatibleSlotAndCalculateFeeOnCheckout() {
        ParkingLot parkingLot = createParkingLotWithSlots();

        CheckInResponse checkIn = parkingSessionService.checkIn(
                new CheckInRequest(parkingLot.getId(), "aa-1234-bb", VehicleType.CAR)
        );

        assertThat(checkIn.slotCode()).isEqualTo("C-01");
        assertThat(checkIn.licensePlate()).isEqualTo("AA-1234-BB");

        clock.advance(Duration.ofMinutes(95));

        CheckOutResponse checkOut = parkingSessionService.checkOut(checkIn.sessionId());

        assertThat(checkOut.durationMinutes()).isEqualTo(95);
        assertThat(checkOut.totalFee()).isEqualByComparingTo("4");
        assertThat(parkingSlotRepository.findById(checkIn.slotId()))
                .get()
                .extracting(ParkingSlot::getStatus)
                .isEqualTo(SlotStatus.AVAILABLE);
    }

    @Test
    void shouldRejectDuplicateActiveSessionForSameVehicle() {
        ParkingLot parkingLot = createParkingLotWithSlots();

        parkingSessionService.checkIn(new CheckInRequest(parkingLot.getId(), "AA-1234-BB", VehicleType.CAR));

        assertThatThrownBy(() -> parkingSessionService.checkIn(
                new CheckInRequest(parkingLot.getId(), "aa-1234-bb", VehicleType.CAR)
        )).isInstanceOf(ConflictException.class)
                .hasMessageContaining("already has an active parking session");
    }

    private ParkingLot createParkingLotWithSlots() {
        ParkingLot parkingLot = new ParkingLot("Central Plaza");
        ParkingLevel level = new ParkingLevel(1);
        level.addSlot(new ParkingSlot("C-01", SlotType.COMPACT));
        level.addSlot(new ParkingSlot("L-01", SlotType.LARGE));
        parkingLot.addLevel(level);
        return parkingLotRepository.save(parkingLot);
    }
}

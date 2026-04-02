package com.parking.system.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.parking.system.infrastructure.persistence.ParkingLotRepository;
import com.parking.system.infrastructure.persistence.ParkingSessionRepository;
import com.parking.system.support.MutableClock;
import com.parking.system.support.TestClockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestClockConfiguration.class)
class ParkingSystemApiIntegrationTest {

    private static final Instant BASE_TIME = Instant.parse("2026-04-02T09:00:00Z");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MutableClock clock;

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

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
    void shouldRunParkingFlowThroughApi() throws Exception {
        String parkingLotResponse = mockMvc.perform(post("/api/v1/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Central Plaza"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Central Plaza"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode parkingLotJson = objectMapper.readTree(parkingLotResponse);
        String parkingLotId = parkingLotJson.get("id").asText();

        String levelResponse = mockMvc.perform(post("/api/v1/parking-lots/{parkingLotId}/levels", parkingLotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "levelNumber": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.levelNumber").value(1))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String levelId = objectMapper.readTree(levelResponse).get("id").asText();

        String slotResponse = mockMvc.perform(post("/api/v1/parking-lots/{parkingLotId}/levels/{levelId}/slots", parkingLotId, levelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "slotCode": "c-01",
                                  "slotType": "COMPACT"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slotCode").value("C-01"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String slotId = objectMapper.readTree(slotResponse).get("id").asText();

        String sessionResponse = mockMvc.perform(post("/api/v1/parking-sessions/check-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "parkingLotId": "%s",
                                  "licensePlate": "aa-1234-bb",
                                  "vehicleType": "CAR"
                                }
                                """.formatted(parkingLotId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licensePlate").value("AA-1234-BB"))
                .andExpect(jsonPath("$.slotCode").value("C-01"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String sessionId = objectMapper.readTree(sessionResponse).get("sessionId").asText();

        mockMvc.perform(get("/api/v1/parking-sessions/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sessionId").value(sessionId))
                .andExpect(jsonPath("$[0].slotId").value(slotId));

        mockMvc.perform(patch("/api/v1/parking-lots/{parkingLotId}/levels/{levelId}/slots/{slotId}/status", parkingLotId, levelId, slotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "UNAVAILABLE"
                                }
                                """))
                .andExpect(status().isConflict());

        clock.advance(Duration.ofMinutes(61));

        mockMvc.perform(post("/api/v1/parking-sessions/{sessionId}/check-out", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.durationMinutes").value(61))
                .andExpect(jsonPath("$.totalFee").value(4));

        mockMvc.perform(get("/api/v1/parking-sessions/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnBadRequestForInvalidParkingLotPayload() throws Exception {
        mockMvc.perform(post("/api/v1/parking-lots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "   "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("name must not be blank"));
    }
}

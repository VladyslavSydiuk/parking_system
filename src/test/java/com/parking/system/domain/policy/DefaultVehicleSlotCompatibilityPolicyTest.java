package com.parking.system.domain.policy;

import com.parking.system.domain.model.SlotType;
import com.parking.system.domain.model.VehicleType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultVehicleSlotCompatibilityPolicyTest {

    private final DefaultVehicleSlotCompatibilityPolicy policy = new DefaultVehicleSlotCompatibilityPolicy();

    @ParameterizedTest
    @CsvSource({
            "MOTORCYCLE, MOTORCYCLE, true",
            "MOTORCYCLE, COMPACT, true",
            "MOTORCYCLE, LARGE, true",
            "MOTORCYCLE, HANDICAPPED, false",
            "CAR, COMPACT, true",
            "CAR, LARGE, true",
            "CAR, MOTORCYCLE, false",
            "TRUCK, LARGE, true",
            "TRUCK, COMPACT, false",
            "TRUCK, HANDICAPPED, false"
    })
    void shouldEvaluateVehicleSlotCompatibility(
            VehicleType vehicleType,
            SlotType slotType,
            boolean expectedCompatibility
    ) {
        assertThat(policy.isCompatible(vehicleType, slotType)).isEqualTo(expectedCompatibility);
    }
}

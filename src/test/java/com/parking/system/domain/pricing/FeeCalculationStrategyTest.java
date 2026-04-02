package com.parking.system.domain.pricing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeeCalculationStrategyTest {

    @ParameterizedTest
    @MethodSource("feeScenarios")
    void shouldCalculateRoundedUpHourlyFees(
            FeeCalculationStrategy strategy,
            Instant entryTime,
            Instant exitTime,
            BigDecimal expectedFee
    ) {
        assertThat(strategy.calculate(entryTime, exitTime)).isEqualByComparingTo(expectedFee);
    }

    @Test
    void shouldRejectNegativeParkingDuration() {
        FeeCalculationStrategy strategy = new CarFeeCalculationStrategy();

        assertThatThrownBy(() -> strategy.calculate(
                Instant.parse("2026-04-02T12:00:00Z"),
                Instant.parse("2026-04-02T11:59:00Z")
        )).isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<org.junit.jupiter.params.provider.Arguments> feeScenarios() {
        Instant base = Instant.parse("2026-04-02T09:00:00Z");

        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        new MotorcycleFeeCalculationStrategy(), base, base.plusSeconds(5 * 60), BigDecimal.valueOf(1)
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        new MotorcycleFeeCalculationStrategy(), base, base.plusSeconds(61 * 60), BigDecimal.valueOf(2)
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        new CarFeeCalculationStrategy(), base, base.plusSeconds(61 * 60), BigDecimal.valueOf(4)
                ),
                org.junit.jupiter.params.provider.Arguments.of(
                        new TruckFeeCalculationStrategy(), base, base.plusSeconds(121 * 60), BigDecimal.valueOf(9)
                )
        );
    }
}

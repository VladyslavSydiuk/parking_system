package com.parking.system.domain.pricing;

import com.parking.system.domain.model.VehicleType;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

public abstract class HourlyRateFeeCalculationStrategy implements FeeCalculationStrategy {

    private final VehicleType vehicleType;
    private final BigDecimal hourlyRate;

    protected HourlyRateFeeCalculationStrategy(VehicleType vehicleType, BigDecimal hourlyRate) {
        this.vehicleType = vehicleType;
        this.hourlyRate = hourlyRate;
    }

    @Override
    public VehicleType supports() {
        return vehicleType;
    }

    @Override
    public BigDecimal calculate(Instant entryTime, Instant exitTime) {
        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Exit time must not be earlier than entry time");
        }

        long stayedMinutes = Duration.between(entryTime, exitTime).toMinutes();
        long billableHours = Math.max(1, (stayedMinutes + 59) / 60);
        return hourlyRate.multiply(BigDecimal.valueOf(billableHours));
    }
}

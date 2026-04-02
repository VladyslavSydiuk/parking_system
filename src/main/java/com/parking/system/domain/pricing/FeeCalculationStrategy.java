package com.parking.system.domain.pricing;

import com.parking.system.domain.model.VehicleType;

import java.math.BigDecimal;
import java.time.Instant;

public interface FeeCalculationStrategy {

    VehicleType supports();

    BigDecimal calculate(Instant entryTime, Instant exitTime);
}

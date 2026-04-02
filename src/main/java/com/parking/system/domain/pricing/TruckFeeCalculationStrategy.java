package com.parking.system.domain.pricing;

import com.parking.system.domain.model.VehicleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TruckFeeCalculationStrategy extends HourlyRateFeeCalculationStrategy {

    public TruckFeeCalculationStrategy() {
        super(VehicleType.TRUCK, BigDecimal.valueOf(3));
    }
}

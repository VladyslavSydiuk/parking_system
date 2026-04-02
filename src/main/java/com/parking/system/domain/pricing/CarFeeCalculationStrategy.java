package com.parking.system.domain.pricing;

import com.parking.system.domain.model.VehicleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CarFeeCalculationStrategy extends HourlyRateFeeCalculationStrategy {

    public CarFeeCalculationStrategy() {
        super(VehicleType.CAR, BigDecimal.valueOf(2));
    }
}

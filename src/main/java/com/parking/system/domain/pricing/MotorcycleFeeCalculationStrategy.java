package com.parking.system.domain.pricing;

import com.parking.system.domain.model.VehicleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MotorcycleFeeCalculationStrategy extends HourlyRateFeeCalculationStrategy {

    public MotorcycleFeeCalculationStrategy() {
        super(VehicleType.MOTORCYCLE, BigDecimal.ONE);
    }
}

package com.parking.system.domain.pricing;

import com.parking.system.domain.model.VehicleType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class FeeCalculationStrategyResolver {

    private final Map<VehicleType, FeeCalculationStrategy> strategiesByVehicleType;

    public FeeCalculationStrategyResolver(List<FeeCalculationStrategy> strategies) {
        Map<VehicleType, FeeCalculationStrategy> strategyMap = new EnumMap<>(VehicleType.class);
        for (FeeCalculationStrategy strategy : strategies) {
            strategyMap.put(strategy.supports(), strategy);
        }
        this.strategiesByVehicleType = Map.copyOf(strategyMap);
    }

    public FeeCalculationStrategy resolve(VehicleType vehicleType) {
        FeeCalculationStrategy strategy = strategiesByVehicleType.get(vehicleType);
        if (strategy == null) {
            throw new IllegalArgumentException("No fee calculation strategy registered for " + vehicleType);
        }
        return strategy;
    }
}

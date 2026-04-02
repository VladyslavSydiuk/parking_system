package com.parking.system.domain.policy;

import com.parking.system.domain.model.SlotType;
import com.parking.system.domain.model.VehicleType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class DefaultVehicleSlotCompatibilityPolicy implements VehicleSlotCompatibilityPolicy {

    private static final Map<VehicleType, Set<SlotType>> COMPATIBILITY_MATRIX = new EnumMap<>(VehicleType.class);

    static {
        COMPATIBILITY_MATRIX.put(VehicleType.MOTORCYCLE, EnumSet.of(SlotType.MOTORCYCLE, SlotType.COMPACT, SlotType.LARGE));
        COMPATIBILITY_MATRIX.put(VehicleType.CAR, EnumSet.of(SlotType.COMPACT, SlotType.LARGE));
        COMPATIBILITY_MATRIX.put(VehicleType.TRUCK, EnumSet.of(SlotType.LARGE));
    }

    @Override
    public boolean isCompatible(VehicleType vehicleType, SlotType slotType) {
        return COMPATIBILITY_MATRIX.getOrDefault(vehicleType, EnumSet.noneOf(SlotType.class)).contains(slotType);
    }
}

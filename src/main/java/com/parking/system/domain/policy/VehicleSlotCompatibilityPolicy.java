package com.parking.system.domain.policy;

import com.parking.system.domain.model.SlotType;
import com.parking.system.domain.model.VehicleType;

public interface VehicleSlotCompatibilityPolicy {

    boolean isCompatible(VehicleType vehicleType, SlotType slotType);
}

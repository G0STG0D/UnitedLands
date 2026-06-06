package org.unitedlands.unitedlands.classes.events.region;

import org.unitedlands.unitedlands.classes.events.base.CostEvent;

public class RegionClaimCostCalculatedEvent extends CostEvent {

    public RegionClaimCostCalculatedEvent(double originalCosts) {
        super(originalCosts);
    }

}

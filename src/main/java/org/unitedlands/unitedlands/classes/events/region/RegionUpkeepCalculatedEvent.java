package org.unitedlands.unitedlands.classes.events.region;

import org.unitedlands.unitedlands.classes.events.base.CostEvent;

public class RegionUpkeepCalculatedEvent extends CostEvent {

    public RegionUpkeepCalculatedEvent(double originalCosts) {
        super(originalCosts);
    }

}

package org.unitedlands.unitedlands.classes.events.settlement;

import org.unitedlands.unitedlands.classes.events.base.CostEvent;

public class SettlementUpkeepCostCalculatedEvent extends CostEvent {

    public SettlementUpkeepCostCalculatedEvent(double originalCosts) {
        super(originalCosts);
    }

}

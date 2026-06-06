package org.unitedlands.unitedlands.classes.events.settlement;

import org.unitedlands.unitedlands.classes.events.base.CostEvent;

public class SettlementClaimCostCalculatedEvent extends CostEvent {

    public SettlementClaimCostCalculatedEvent(double originalCosts) {
        super(originalCosts);
    }

}

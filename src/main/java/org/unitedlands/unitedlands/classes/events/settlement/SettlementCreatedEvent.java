package org.unitedlands.unitedlands.classes.events.settlement;

import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.events.base.SettlementEvent;

public class SettlementCreatedEvent extends SettlementEvent {

    public SettlementCreatedEvent(Settlement settlement) {
        super(settlement);
    }

}

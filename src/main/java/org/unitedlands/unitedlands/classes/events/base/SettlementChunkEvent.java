package org.unitedlands.unitedlands.classes.events.base;

import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.SettlementChunk;

public class SettlementChunkEvent extends SettlementEvent {

    private final SettlementChunk settlementChunk;

    public SettlementChunkEvent(Settlement settlement, SettlementChunk settlementChunk) {
        super(settlement);
        this.settlementChunk = settlementChunk;
    }

    public SettlementChunk getSettlementChunk() {
        return settlementChunk;
    }

}

package org.unitedlands.unitedlands.classes.events.settlement;

import org.unitedlands.unitedlands.classes.Coordinates;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.events.base.SettlementEvent;


public class SettlementUnclaimEvent extends SettlementEvent {

    private final Coordinates chunkCoordinates;

    public SettlementUnclaimEvent(Settlement settlement, Coordinates chunkCoordinates) {
        super(settlement);
        this.chunkCoordinates = chunkCoordinates;
    }

    public Coordinates getChunkCoordinates() {
        return chunkCoordinates;
    }



}

package org.unitedlands.unitedlands.classes.events.base;

import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.Settlement;

public abstract class SettlementPlayerEvent extends SettlementEvent {

    private final Player player;

    public SettlementPlayerEvent(Settlement settlement, Player player) {
        super(settlement);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}

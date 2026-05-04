package org.unitedlands.unitedlands.classes.events.player;

import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.events.base.SettlementPlayerEvent;

public class PlayerEnterSettlementEvent extends SettlementPlayerEvent {

    public PlayerEnterSettlementEvent(Settlement settlement, Player player) {
        super(settlement, player);
    }

}

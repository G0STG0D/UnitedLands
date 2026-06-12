package org.unitedlands.unitedlands.classes.events.settlement;

import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.events.base.SettlementPlayerEvent;
import org.unitedlands.unitedlands.managers.GlobalDataManager;

public class SettlementPlayerLeaveEvent extends SettlementPlayerEvent {

    public SettlementPlayerLeaveEvent(Settlement settlement, Player player) {
        super(settlement, player);
    }

    public Citizen getCitizen() {
        return GlobalDataManager.instance().getCitizen(getPlayer());
    }

}

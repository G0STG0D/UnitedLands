package org.unitedlands.unitedlands.classes.events.player;

import org.unitedlands.unitedlands.classes.Region;
import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.events.base.RegionPlayerEvent;

public class PlayerExitRegionEvent extends RegionPlayerEvent {

    public PlayerExitRegionEvent(Region region, Player player) {
        super(region, player);
    }

}

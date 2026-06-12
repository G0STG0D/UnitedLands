package org.unitedlands.unitedlands.classes.events.player;


import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.events.base.RegionPlayerEvent;

public class PlayerEnterRegionEvent extends RegionPlayerEvent {

    public PlayerEnterRegionEvent(Region region, Player player) {
        super(region, player);
    }

}

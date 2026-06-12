package org.unitedlands.unitedlands.classes.events.base;

import org.unitedlands.unitedlands.classes.Region;
import org.bukkit.entity.Player;

public abstract class RegionPlayerEvent extends RegionEvent {

    private final Player player;

    public RegionPlayerEvent(Region region, Player player) {
        super(region);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

   
}

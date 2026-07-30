package org.unitedlands.unitedlands.classes.events.region;

import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.events.base.RegionEvent;

public class RegionClaimStartEvent extends RegionEvent {

    private final Player player;
    private final Country country;
    private final boolean doubleClaim;

    public RegionClaimStartEvent(Player player, Region region, Country country, boolean doubleClaim) {
        super(region);
        this.player = player;
        this.country = country;
        this.doubleClaim = doubleClaim;
    }

    public Country getCountry() {
        return country;
    }

    public boolean isDoubleClaim() {
        return doubleClaim;
    }

    public Player getPlayer() {
        return player;
    }

}

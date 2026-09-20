package org.unitedlands.unitedlands.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.unitedlands.unitedlands.classes.events.region.RegionClaimStartEvent;
import org.unitedlands.unitedlands.classes.events.region.RegionClaimedEvent;

import org.unitedlands.utils.United;

public class RegionListener implements Listener {

    public RegionListener() {

    }

    @EventHandler
    public void onRegionStartClaimEvent(RegionClaimStartEvent event) {
        var region = event.getRegion();
        United.messenger().send(Bukkit.getServer(), "player.country.claim.start-broadcast", event.getCountry().getCleanName(), region.getCleanName(), United.formatter().formatDuration(region.getClaimEndTime() - region.getClaimStartTime()));
    }

    @EventHandler
    public void onRegionClaimedEvent(RegionClaimedEvent event) {
        United.messenger().send(Bukkit.getServer(), "player.country.claim.claimed-broadcast", event.getCountry().getCleanName(), event.getRegion().getCleanName());
    }

}

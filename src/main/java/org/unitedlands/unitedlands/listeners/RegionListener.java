package org.unitedlands.unitedlands.listeners;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.unitedlands.unitedlands.classes.events.region.RegionClaimStartEvent;
import org.unitedlands.unitedlands.classes.events.region.RegionClaimedEvent;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

public class RegionListener implements Listener {

    private final MessageProvider messageProvider;

    public RegionListener(MessageProvider messageProvider) {
        this.messageProvider = messageProvider;

    }

    @EventHandler
    public void onRegionStartClaimEvent(RegionClaimStartEvent event) {
        Messenger.sendMessage(Bukkit.getServer(), messageProvider.get("country.claim.start-broadcast"),
                Map.of("country", event.getCountry().getCleanName(),
                        "region", event.getRegion().getCleanName()),
                messageProvider.get("prefix"));
    }

    @EventHandler
    public void onRegionClaimedEvent(RegionClaimedEvent event) {
        Messenger.sendMessage(Bukkit.getServer(), messageProvider.get("country.claim.broadcast"),
                Map.of("country", event.getCountry().getCleanName(),
                        "region", event.getRegion().getCleanName()),
                messageProvider.get("prefix"));
    }

}

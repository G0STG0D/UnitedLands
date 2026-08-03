package org.unitedlands.unitedlands.listeners;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.unitedlands.unitedlands.classes.events.region.RegionClaimStartEvent;
import org.unitedlands.unitedlands.classes.events.region.RegionClaimedEvent;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Messenger;

public class RegionListener implements Listener {

    private final MessageProvider messageProvider;

    public RegionListener(MessageProvider messageProvider) {
        this.messageProvider = messageProvider;

    }

    @EventHandler
    public void onRegionStartClaimEvent(RegionClaimStartEvent event) {
        var region = event.getRegion();
        Messenger.sendMessage(Bukkit.getServer(), messageProvider.get(Message.PLAYER__COUNTRY__CLAIM__START_BROADCAST.path()),
                Map.of("country", event.getCountry().getCleanName(),
                        "region", region.getCleanName(),
                        "time", Formatter.formatDuration(region.getClaimEndTime() - region.getClaimStartTime())),
                messageProvider.get(Message.PREFIX.path()));
    }

    @EventHandler
    public void onRegionClaimedEvent(RegionClaimedEvent event) {
        Messenger.sendMessage(Bukkit.getServer(), messageProvider.get(Message.PLAYER__COUNTRY__CLAIM__CLAIMED_BROADCAST.path()),
                Map.of("country", event.getCountry().getCleanName(),
                        "region", event.getRegion().getCleanName()),
                messageProvider.get(Message.PREFIX.path()));
    }

}

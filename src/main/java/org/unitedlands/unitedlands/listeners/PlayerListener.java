package org.unitedlands.unitedlands.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.events.player.PlayerEnterSettlementEvent;
import org.unitedlands.unitedlands.classes.events.player.PlayerExitSettlementEvent;

public class PlayerListener implements Listener {

    private final UnitedLands plugin;

    public PlayerListener(UnitedLands plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEnterSettlement(PlayerEnterSettlementEvent event) {
        plugin.getDisplayManager().showSettlementDisplay(event.getSettlement(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onExitSettlement(PlayerExitSettlementEvent event) {
        plugin.getDisplayManager().hideSettlementDisplay(event.getSettlement(), event.getPlayer());
    }
}

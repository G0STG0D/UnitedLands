package org.unitedlands.unitedlands.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.events.base.PlayerChangeChunkEvent;
import org.unitedlands.unitedlands.classes.events.player.PlayerEnterRegionEvent;
import org.unitedlands.unitedlands.classes.events.player.PlayerEnterSettlementEvent;
import org.unitedlands.unitedlands.classes.events.player.PlayerExitSettlementEvent;
import org.unitedlands.unitedlands.managers.DisplayManager;

public class PlayerListener implements Listener {

    @SuppressWarnings("unused")
    private final UnitedLands plugin;

    public PlayerListener(UnitedLands plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEnterSettlement(PlayerEnterSettlementEvent event) {
        DisplayManager.instance().showSettlementNameDisplay(event.getSettlement(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onExitSettlement(PlayerExitSettlementEvent event) {
        DisplayManager.instance().hideSettlementNameDisplay(event.getSettlement(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEnterRegion(PlayerEnterRegionEvent event) {
        DisplayManager.instance().showRegionName(event.getRegion(), event.getPlayer(), event.getAdditionalDisplay());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChangeChunk(PlayerChangeChunkEvent event) {
        if (DisplayManager.instance().isPlayerViewingMap(event.getPlayer()))
            DisplayManager.instance().showMap(event.getPlayer(), event.getToLocation());
    }
}

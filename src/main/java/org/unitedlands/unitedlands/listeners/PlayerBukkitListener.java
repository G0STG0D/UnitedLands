package org.unitedlands.unitedlands.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.classes.events.base.PlayerChangeChunkEvent;
import org.unitedlands.unitedlands.classes.events.cititen.CitizenCreatedEvent;
import org.unitedlands.unitedlands.classes.events.player.PlayerEnterRegionEvent;
import org.unitedlands.unitedlands.classes.events.player.PlayerEnterSettlementEvent;
import org.unitedlands.unitedlands.classes.events.player.PlayerExitRegionEvent;
import org.unitedlands.unitedlands.classes.events.player.PlayerExitSettlementEvent;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.managers.PlayerCacheManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;

public class PlayerBukkitListener implements Listener {

    @SuppressWarnings("unused")
    private final UnitedLands plugin;

    public PlayerBukkitListener(UnitedLands plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updateCitizenRecord(player);
        updatePlayerLocation(player, new Location(player.getLocation().getWorld(), 0, 0, 0), player.getLocation());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        updatePlayerLocation(player, player.getLocation(), new Location(player.getLocation().getWorld(), 0, 0, 0));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerQuit(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!updatePlayerLocation(player, event.getFrom(), event.getTo())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!updatePlayerLocation(event.getPlayer(), event.getFrom(), event.getTo()))
            event.setCancelled(true);
    }

    private void updateCitizenRecord(Player player) {
        Citizen citizen = GlobalDataManager.instance().getCitizen(player);
        if (citizen == null) {
            citizen = new Citizen(player);
            citizen.setJoined(System.currentTimeMillis());
            citizen.setName(player.getName());
            citizen.setLastLogon(System.currentTimeMillis());

            (new CitizenCreatedEvent(citizen)).callEvent();

            GlobalDataManager.instance().createCitizenDbData(citizen);
        } else {
            citizen.setName(player.getName());
            citizen.setLastLogon(System.currentTimeMillis());
            GlobalDataManager.instance().updateCitizenDbData(citizen);
        }

    }

    private boolean updatePlayerLocation(Player player, Location from, Location to) {

        var fromChunkCoords = CoordinateUtils.locationToChunkCoordinates(from);
        var toChunkCoords = CoordinateUtils.locationToChunkCoordinates(to);
        var fromRegionCoords = CoordinateUtils.locationToRegionCoordinates(from);
        var toRegionCoords = CoordinateUtils.locationToRegionCoordinates(to);

        if (!fromChunkCoords.equals(toChunkCoords)) {

            var playerCache = PlayerCacheManager.instance().getPlayerCache(player);

            // Settlement handling

            boolean enteredSettlement = false;
            boolean leftSettlement = false;
            Settlement lastSettlement = null;
            SettlementChunk settlementChunk = GlobalDataManager.instance().getSettlementChunk(toChunkCoords);

            if (settlementChunk != null) {
                // Entered a valid settlement chunk
                if (!settlementChunk.equals(playerCache.getCachedSettlementChunk())) {
                    // New chunk is different from the cached chunk
                    enteredSettlement = true;
                    if (playerCache.getCachedSettlement() != null) {
                        // Entered from a different settlement
                        leftSettlement = true;
                        lastSettlement = playerCache.getCachedSettlement();
                    }
                    playerCache.updateChunkCache(toChunkCoords, settlementChunk);
                }
            } else {
                // Entered the wilderness
                if (playerCache.getCachedSettlementChunk() != null) {
                    // Entered wilderness from a settlement
                    leftSettlement = true;
                    lastSettlement = playerCache.getCachedSettlement();
                }
                playerCache.clearChunkCache();
            }

            if (enteredSettlement) {
                var enterSettlementEvent = new PlayerEnterSettlementEvent(settlementChunk.getSettlement(), player);
                enterSettlementEvent.callEvent();
                if (enterSettlementEvent.isCancelled())
                    return false;
            }
            if (leftSettlement) {
                var exitSettlementEvent = new PlayerExitSettlementEvent(lastSettlement, player);
                exitSettlementEvent.callEvent();
                if (exitSettlementEvent.isCancelled())
                    return false;
            }


            // Region handling

            Region region = null;
            boolean enteredRegion = false;
            boolean leftRegion = false;
            Region lastRegion = null;

            if (!fromRegionCoords.equals(toRegionCoords)) {
                // Changed region chunk
                region = GlobalDataManager.instance().getRegion(toRegionCoords);
                if (region != null) {
                    // Entered a valid region
                    if (!region.equals(playerCache.getCachedRegion())) {
                        // New region is different from cached region
                        enteredRegion = true;
                        if (playerCache.getCachedRegion() != null) {
                            // Entered from a different region
                            leftRegion = true;
                            lastRegion = playerCache.getCachedRegion();
                        }
                        playerCache.updateRegionCache(toRegionCoords, region);
                    }
                } else {
                    // Entered a regionless zone
                    if (playerCache.getCachedRegion() != null) {
                        // Entered regionless zone from a region
                        leftRegion = true;
                        lastRegion = playerCache.getCachedRegion();
                    }
                    playerCache.clearRegionCache();
                }
            }


            if (enteredRegion) {
                var enterRegionEvent = new PlayerEnterRegionEvent(region, player);
                enterRegionEvent.callEvent();
                if (enterRegionEvent.isCancelled())
                    return false;
            }
            if (leftRegion) {
                var exitRegionEvent = new PlayerExitRegionEvent(lastRegion, player);
                exitRegionEvent.callEvent();
                if (exitRegionEvent.isCancelled())
                    return false;
            }

            // General event

            var chunkChangeEvent = new PlayerChangeChunkEvent(player, from, to);
            chunkChangeEvent.callEvent();
            if (chunkChangeEvent.isCancelled())
                return false;

            playerCache.calculateMemberships();
            return true;

        }

        return true;
    }

}

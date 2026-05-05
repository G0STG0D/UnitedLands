package org.unitedlands.unitedlands.listeners;

import java.util.HashSet;
import java.util.Set;

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
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.events.base.PlayerChangeChunkEvent;
import org.unitedlands.unitedlands.classes.events.player.PlayerEnterSettlementEvent;
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
        if (!updatePlayerLocation(player, player.getLocation(),
                new Location(player.getLocation().getWorld(), 0, 0, 0))) {
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

        if (!fromChunkCoords.equals(toChunkCoords)) {

            Set<String> notificationStrings = new HashSet<>();

            var playerCache = PlayerCacheManager.instance().getPlayerCache(player);

            var fromRegionCoords = CoordinateUtils.locationToRegionCoordinates(from);
            var toRegionCoords = CoordinateUtils.locationToRegionCoordinates(to);

            boolean enteredSettlement = false;
            boolean leftSettlement = false;
            Settlement lastSettlement = null;

            var settlementChunk = GlobalDataManager.instance().getSettlementChunk(toChunkCoords);
            if (settlementChunk != null) {
                if (!settlementChunk.equals(playerCache.getCachedSettlementChunk())) {
                    if (playerCache.getCachedSettlementChunk() == null)
                        enteredSettlement = true;

                    // TODO: Settlement to Settlement display

                    playerCache.updateChunkCache(toChunkCoords, settlementChunk);
                    notificationStrings.add(settlementChunk.getSettlement().getCleanName());
                }
            } else {
                if (playerCache.getCachedSettlementChunk() != null) {
                    leftSettlement = true;
                    lastSettlement = playerCache.getCachedSettlement();
                }
                playerCache.clearChunkCache();
            }

            if (!fromRegionCoords.equals(toRegionCoords)) {
                var region = GlobalDataManager.instance().getRegion(toRegionCoords);

                if (region != null) {
                    if (!region.equals(playerCache.getCachedRegion())) {
                        playerCache.updateRegionCache(toRegionCoords, region);
                        notificationStrings.add(region.getCleanName());
                    }
                } else {
                    playerCache.clearRegionCache();
                }
            }

            if (enteredSettlement) {
                var enterEvent = new PlayerEnterSettlementEvent(settlementChunk.getSettlement(), player);
                enterEvent.callEvent();
                if (enterEvent.isCancelled())
                    return false;
            } else if (leftSettlement) {
                var exitEvent = new PlayerExitSettlementEvent(lastSettlement, player);
                exitEvent.callEvent();
                if (exitEvent.isCancelled())
                    return false;
            }

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

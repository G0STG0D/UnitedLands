package org.unitedlands.unitedlands.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;

public class ExplosionListener implements Listener {

    @SuppressWarnings("unused")
    private final UnitedLands plugin;

    public ExplosionListener(UnitedLands plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFireSpread(BlockSpreadEvent event) {
        if (!(event.getSource().getType() == Material.FIRE))
            return;
        var location = event.getBlock().getLocation();
        if (!isFireAllowed(location)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        var location = event.getBlock().getLocation();
        if (!isExplosionAllowed(location)) {
            event.blockList().clear();
            return;
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        var location = event.getLocation();
        if (!isExplosionAllowed(location)) {
            event.blockList().clear();
            return;
        }
    }

    private boolean isExplosionAllowed(Location location) {
        var chunkCoordinates = CoordinateUtils.locationToChunkCoordinates(location);
        var settlementchunk = GlobalDataManager.instance().getSettlementChunk(chunkCoordinates);
        if (settlementchunk != null) {
            if (!settlementchunk.allowExplosions()) {
                return false;
            }
        } else {
            var regionCoords = CoordinateUtils.locationToRegionCoordinates(location);
            var region = GlobalDataManager.instance().getRegion(regionCoords);
            if (region != null) {
                if (!region.allowExplosions()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isFireAllowed(Location location) {
        var chunkCoordinates = CoordinateUtils.locationToChunkCoordinates(location);
        var settlementchunk = GlobalDataManager.instance().getSettlementChunk(chunkCoordinates);
        if (settlementchunk != null) {
            if (!settlementchunk.allowFire()) {
                return false;
            }
        } else {
            var regionCoords = CoordinateUtils.locationToRegionCoordinates(location);
            var region = GlobalDataManager.instance().getRegion(regionCoords);
            if (region != null) {
                if (!region.allowFire()) {
                    return false;
                }
            }
        }
        return true;
    }

}

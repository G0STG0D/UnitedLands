package org.unitedlands.unitedlands.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;

public class MobListener implements Listener {

    @SuppressWarnings("unused")
    private final UnitedLands plugin;

    public MobListener(UnitedLands plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        if (event.getSpawnReason() == SpawnReason.CUSTOM || event.getSpawnReason() == SpawnReason.COMMAND)
            return;

        var creatureType = event.getEntityType().toString();

        var isMonster = Settings.getBlacklistedMonsters().contains(creatureType);
        var isAnimal = Settings.getBlacklistedAnimals().contains(creatureType);

        if (!isMonster && !isAnimal)
            return;

        var location = event.getLocation();
        var chunkCoordinates = CoordinateUtils.locationToChunkCoordinates(location);

        var settlementChunk = GlobalDataManager.instance().getSettlementChunk(chunkCoordinates);
        if (settlementChunk != null) {
            if (!settlementChunk.allowMonsters() && isMonster) {
                event.setCancelled(true);
                return;
            }
            if (!settlementChunk.allowAnimals() && isAnimal) {
                event.setCancelled(true);
                return;
            }
        } else {
            var regionCoordinates = CoordinateUtils.locationToRegionCoordinates(location);
            var region = GlobalDataManager.instance().getRegion(regionCoordinates);
            if (region != null) {
                if (!region.allowMonsters() && isMonster) {
                    event.setCancelled(true);
                    return;
                }
                if (!region.allowAnimals() && isAnimal) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

    }

}

package org.unitedlands.unitedlands.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.PlayerCache;

public class PlayerCacheManager {

    private static PlayerCacheManager instance;

    public static PlayerCacheManager instance() {
        return instance;
    }

    private final UnitedLands plugin;
    private Map<UUID, PlayerCache> playerCache = new HashMap<>();

    public PlayerCacheManager(UnitedLands plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public PlayerCache getPlayerCache(Player player) {
        return getPlayerCache(player.getUniqueId());
    }

    public PlayerCache getPlayerCache(Citizen citizen) {
        return getPlayerCache(citizen.getUuid());
    }

    public PlayerCache getPlayerCache(UUID playerUuid) {
        return playerCache.computeIfAbsent(playerUuid, k -> new PlayerCache(Bukkit.getPlayer(playerUuid), plugin));
    }

}

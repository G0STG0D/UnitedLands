package org.unitedlands.unitedlands.utils;

import org.bukkit.Location;
import org.unitedlands.unitedlands.classes.Coordinates;

public class CoordinateUtils {

    public static Coordinates chunkToWorldCoordinates(Coordinates chunkCoordinates) {
        return new Coordinates(chunkCoordinates.getX() * 16, chunkCoordinates.getZ() * 16,
                chunkCoordinates.getWorldName());
    }

    public static Coordinates locationToChunkCoordinates(Location location) {
        return new Coordinates(location.getBlockX() >> 4, location.getBlockZ() >> 4, location.getWorld().getName());
    }

    public static Coordinates locationToChunkCenterCoordinates(Location location) {
        var chunk = location.getChunk();
        return new Coordinates((chunk.getX() << 4) + 8, (chunk.getZ() << 4) + 8, location.getWorld().getName());
    }

}

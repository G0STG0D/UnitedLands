package org.unitedlands.unitedlands.utils;

import org.bukkit.Location;
import org.unitedlands.unitedlands.classes.Coordinates;
import org.unitedlands.unitedlands.classes.Settings;

public class CoordinateUtils {

    public static Coordinates chunkToRegionCoordinates(Coordinates chunkCoordinates) {
        int regionChunkSize = Settings.regionChunkSize;
        return new Coordinates(Math.floorDiv((int) chunkCoordinates.getX(), regionChunkSize),
                Math.floorDiv((int) chunkCoordinates.getZ(), regionChunkSize), chunkCoordinates.getWorldName());
    }

    public static Coordinates chunkToWorldCoordinates(Coordinates chunkCoordinates) {
        return new Coordinates(chunkCoordinates.getX() * 16, chunkCoordinates.getZ() * 16, chunkCoordinates.getWorldName());
    }

    public static Coordinates locationToRegionCoordinates(Location location) {
        int regionBlockSize = Settings.regionChunkSize * 16;
        return new Coordinates(Math.floorDiv((int) location.getX(), regionBlockSize),
                Math.floorDiv((int) location.getZ(), regionBlockSize), location.getWorld().getName());
    }

    public static Coordinates locationToChunkCoordinates(Location location) {
        return new Coordinates(location.getChunk().getX(), location.getChunk().getZ(), location.getWorld().getName());
    }

    public static Coordinates worldToRegionCoordinates(Coordinates worldCoords) {
        int regionBlockSize = Settings.regionChunkSize * 16;
        return new Coordinates(Math.floorDiv(worldCoords.getX(), regionBlockSize),
                Math.floorDiv(worldCoords.getZ(), regionBlockSize), worldCoords.getWorldName());
    }

    public static Coordinates regionToWorldCoordinates(Coordinates regionCoords) {
        int regionBlockSize = Settings.regionChunkSize * 16;
        return new Coordinates(regionCoords.getX() * regionBlockSize, regionCoords.getZ() * regionBlockSize,
                regionCoords.getWorldName());
    }

}

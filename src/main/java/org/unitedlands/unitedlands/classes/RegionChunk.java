package org.unitedlands.unitedlands.classes;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.unitedlands.unitedlands.classes.db.Identifiable;
import org.unitedlands.unitedlands.classes.interfaces.CoordinateHolder;
import org.unitedlands.unitedlands.managers.GlobalDataManager;

import com.j256.ormlite.field.DatabaseField;

public class RegionChunk implements Identifiable, CoordinateHolder {

    private final int RENDER_OFFSET = 1; 

    @DatabaseField(id = true, width = 36, canBeNull = false)
    private UUID uuid;
    @DatabaseField(width = 255)
    private String name;
    @DatabaseField(width = 255, columnName = "world_name")
    private String worldName;
    @DatabaseField(width = 255, columnName = "region_uuid")
    private UUID regionUuid;

    @DatabaseField()
    private int x;
    @DatabaseField()
    private int z;

    private World world;
    private Region region;
    private Coordinates coordinates;

    private final int size;

    public RegionChunk() {
        size = Settings.regionChunkSize * 16;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Coordinates getCoordinates() {
        if (coordinates == null)
            coordinates = new Coordinates(x, z, worldName);
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
        this.x = coordinates.getX();
        this.z = coordinates.getZ();
        this.worldName = coordinates.getWorldName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public World getWorld() {
        if (world == null && worldName != null)
            world = Bukkit.getWorld(worldName);
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
        this.worldName = world.getName();
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public Region getRegion() {
        if (region == null)
            region = GlobalDataManager.instance().getRegion(regionUuid);
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
        this.regionUuid = region.getUuid();
    }

    public UUID getRegionUuid() {
        return regionUuid;
    }

    public int getSize() {
        return size;
    }

    public Coordinates getWorldCoords() {
        return new Coordinates(this.coordinates.getX() * size, this.coordinates.getZ() * size, this.worldName);
    }

    public Coordinates getCenter() {
        var worldCoords = getWorldCoords();
        return new Coordinates(worldCoords.getX() + (size / 2), worldCoords.getZ() - (size / 2), this.worldName);
    }

    public Coordinates getLowerLeft() {
        var worldCoords = getWorldCoords();
        return new Coordinates(worldCoords.getX() + RENDER_OFFSET, worldCoords.getZ() + size - RENDER_OFFSET, this.worldName);
    }

    public Coordinates getLowerRight() {
        var worldCoords = getWorldCoords();
        return new Coordinates(worldCoords.getX() + size, worldCoords.getZ() + size - RENDER_OFFSET, this.worldName);
    }

    public Coordinates getUpperLeft() {
        var worldCoords = getWorldCoords();
        return new Coordinates(worldCoords.getX() + RENDER_OFFSET, worldCoords.getZ(), this.worldName);
    }

    public Coordinates getUpperRight() {
        var worldCoords = getWorldCoords();
        return new Coordinates(worldCoords.getX() + size, worldCoords.getZ(), this.worldName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RegionChunk other = (RegionChunk) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

}

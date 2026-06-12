package org.unitedlands.unitedlands.classes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

public class Coordinates {
    private int x;
    private int z;
    private final String worldName;

    private transient World world;

    public Coordinates(String worldName) {
        this.worldName = worldName;
    }

    public Coordinates(int x, int z, String worldName) {
        this.x = x;
        this.z = z;
        this.worldName = worldName;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getWorldName() {
        return worldName;
    }

    public World getWorld() {
        if (world == null && worldName != null && !worldName.isEmpty())
            world = Bukkit.getWorld(worldName);
        return world;
    }

    public Coordinates add(int x, int z) {
        this.x += x;
        this.z += z;

        return this;
    }

    public double distance(Coordinates other) {
        double xDiff = getX() - other.getX();
        double zDiff = getZ() - other.getZ();
        return Math.sqrt((xDiff * xDiff) + (zDiff * zDiff));
    }

    public Coordinates clone() {
        return new Coordinates(this.x, this.z, this.worldName);
    }

    @Override
    public String toString() {
        return "{ x: " + x + ", z: " + z + ", world: " + worldName + " }";
    }

    public String toCleanString() {
        return x + ", " + z + " (" + worldName + ")";
    }

    public String toCleanShortString() {
        return x + ", " + z;
    }

    public Location toLocation() {
        var world = Bukkit.getWorld(this.worldName);
        var y = world.getHighestBlockYAt(x, z);
        return new Location(world, x, y, z);
    }

    public Location getLowerCorner() {
        return new Location(getWorld(), getX() * 16, getWorld().getMinHeight(), getZ() * 16);
    }

    public Location getUpperCorner() {
        return new Location(getWorld(), (getX() * 16) + 16, getWorld().getMaxHeight(), (getZ() * 16) + 16);
    }

    public BoundingBox getBoundingBox() {
        return BoundingBox.of(getLowerCorner().getBlock(), getUpperCorner().getBlock());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + z;
        result = prime * result + ((worldName == null) ? 0 : worldName.hashCode());
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
        Coordinates other = (Coordinates) obj;
        if (x != other.x)
            return false;
        if (z != other.z)
            return false;
        if (worldName == null) {
            if (other.worldName != null)
                return false;
        } else if (!worldName.equals(other.worldName))
            return false;
        return true;
    }

}

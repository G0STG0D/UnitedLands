package org.unitedlands.unitedlands.classes.webservices.dto;

import org.bukkit.Location;

public class LocationDTO {
    public double x;
    public double y;
    public double z;
    public double yaw;
    public double pitch;
    public String world;

    public LocationDTO(Location loc)
    {
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
        yaw = loc.getYaw();
        pitch = loc.getPitch();
        world = loc.getWorld().getName();
    }
}

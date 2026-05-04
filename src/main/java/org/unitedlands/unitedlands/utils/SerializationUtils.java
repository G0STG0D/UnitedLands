package org.unitedlands.unitedlands.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SerializationUtils {

    public static String serializeLocation(Location l)
    {
        return l.getWorld().getName() + ";" 
             + l.getX() + ";" 
             + l.getY() + ";" 
             + l.getZ() + ";" 
             + l.getYaw() + ";" 
             + l.getPitch();
    }

    public static Location deserializeLocation(String s)
    {
        var ls = s.split(";");
        return new Location(
            Bukkit.getWorld(ls[0]), 
            Double.parseDouble(ls[1]), 
            Double.parseDouble(ls[2]), 
            Double.parseDouble(ls[3]), 
            Float.parseFloat(ls[4]),
            Float.parseFloat(ls[5])
        );
    }
}

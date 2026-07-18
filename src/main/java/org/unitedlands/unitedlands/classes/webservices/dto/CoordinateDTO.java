package org.unitedlands.unitedlands.classes.webservices.dto;

import org.unitedlands.unitedlands.classes.Coordinates;

public class CoordinateDTO {
    public int x;
    public int z;
    public String world;

    public CoordinateDTO(Coordinates c)
    {
        x = c.getX();
        z = c.getZ();
        world = c.getWorldName();
    }
}

package org.unitedlands.unitedlands.classes.webservices.dto;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.unitedlands.unitedlands.classes.Region;

public class RegionDTO {

    public String uuid;
    public String slug;
    public String name;
    public CoordinateDTO home;
    public Set<SettlementDTO> settlements = new HashSet<>();
    public CountryDTO country;

    public RegionDTO(UUID uuid, String slug, String name)
    {
        this.uuid = uuid.toString();
        this.slug = slug;
        this.name = name;
    }

    public RegionDTO(Region r)
    {
        this.uuid = r.getUuid().toString();
        this.slug = r.getName();
        this.name = r.getCleanName();
        this.home = new CoordinateDTO(r.getHomeChunkCoordinates());
    }

}

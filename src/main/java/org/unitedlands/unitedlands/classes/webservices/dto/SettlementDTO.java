package org.unitedlands.unitedlands.classes.webservices.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.CostUtils;

public class SettlementDTO {
    public String uuid;
    public String slug;
    public String name;
    public String board;
    public Integer color;
    public CititzenDTO mayor;
    public RegionDTO region;
    public CountryDTO country;

    public Double balance;
    public Long founded;
    public LocationDTO spawn;

    public Map<String, Boolean> toggles;

    public Integer size;
    public Double upkeep;

    public SettlementDTO(UUID id, String slug, String name) {
        this.uuid = id.toString();
        this.slug = slug;
        this.name = name;
    }

    public SettlementDTO(Settlement s) {

        this.uuid = s.getUuid().toString();
        this.slug = s.getName();
        this.name = s.getCleanName();
        this.board = s.getTownBoard();
        this.color = s.getStrokeColor();

        var m = s.getMayor();
        if (m != null)
            this.mayor = new CititzenDTO(m.getUuid(), m.getName());

        var r = s.getRegion();
        if (r != null)
            this.region = new RegionDTO(r.getUuid(), r.getName(), r.getCleanName());
        
        var c = s.getCountry();
        if (c != null)
            this.country = new CountryDTO(c.getUuid(), c.getName(), c.getCleanName());

        this.balance = UnitedLandsEconomyManager.instance().getBalance(s.getUuid()).doubleValue();
        this.founded = s.getFoundingTimestamp();
        this.spawn = new LocationDTO(s.getSpawn());

        this.toggles = new HashMap<>();
        this.toggles.put("public", s.isPublic());
        this.toggles.put("pvp", s.allowPvp());
        this.toggles.put("animals", s.allowAnimals());
        this.toggles.put("monsters", s.allowMonsters());
        this.toggles.put("fire", s.allowFire());
        this.toggles.put("explosions", s.allowExplosions());
        this.toggles.put("open", false);
        this.toggles.put("neutral", false);

        this.size = s.getChunks().size();
        this.upkeep = CostUtils.getSettlementUpkeep(s);
    }

}

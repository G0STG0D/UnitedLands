package org.unitedlands.unitedlands.classes.webservices.dto;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.CostUtils;

public class CountryDTO {
    public String uuid;
    public String slug;
    public String name;
    public String board;
    public Integer color;
    public CititzenDTO leader;
    public SettlementDTO capital;
    public Set<RegionDTO> regions = new HashSet<>();
    public Set<SettlementDTO> settlements = new HashSet<>();
    public Set<CountryDTO> allies = new HashSet<>();
    public Double balance;
    public Double upkeep;
    public Long founded;
    public LocationDTO spawn;

    public CountryDTO(UUID id, String slug, String name) {
        this.uuid = id.toString();
        this.slug = slug;
        this.name = name;
    }

    public CountryDTO(Country c) {

        this.uuid = c.getUuid().toString();
        this.slug = c.getName();
        this.name = c.getCleanName();
        this.board = "";
        this.color = c.getStrokeColor();

        for (var r : c.getRegions())
            this.regions.add(new RegionDTO(r.getUuid(), r.getName(), r.getCleanName()));

        var l = c.getLeader();
        if (l != null) {
            this.leader = new CititzenDTO(l.getUuid(), l.getName());
        }

        var cp = c.getCapital();
        if (cp != null) {
            this.capital = new SettlementDTO(cp.getUuid(), cp.getName(), cp.getCleanName());
        }

        for (var s : c.getSettlements())
            this.settlements.add(new SettlementDTO(s.getUuid(), s.getName(), s.getCleanName()));
        
        this.balance = UnitedLandsEconomyManager.instance().getBalance(c.getUuid()).doubleValue();
        this.upkeep = CostUtils.getCountryUpkeep(c);

        this.founded = c.getFoundingTimestamp();
        this.spawn = new LocationDTO(c.getSpawn());

    }
}

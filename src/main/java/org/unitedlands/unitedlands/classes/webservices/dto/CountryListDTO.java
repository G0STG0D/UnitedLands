package org.unitedlands.unitedlands.classes.webservices.dto;

import java.util.UUID;

import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.CostUtils;

public class CountryListDTO {

    public String uuid;
    public String slug;
    public String name;
    public String board;
    public Integer color;
    public CititzenDTO leader;
    public SettlementDTO capital;
    public Double balance;
    public Double upkeep;
    public Integer size;
    public Integer settlementCount;
    public Integer citizenCount;
    public Long founded;
    public LocationDTO spawn;

    public CountryListDTO(UUID id, String slug, String name) {
        this.uuid = id.toString();
        this.slug = slug;
        this.name = name;
    }

    public CountryListDTO(Country c) {

        this.uuid = c.getUuid().toString();
        this.slug = c.getName();
        this.name = c.getCleanName();
        this.board = "";
        this.color = c.getFillColor();

        var l = c.getLeader();
        if (l != null) {
            this.leader = new CititzenDTO(l.getUuid(), l.getName());
        }

        var cp = c.getCapital();
        if (cp != null) {
            this.capital = new SettlementDTO(cp.getUuid(), cp.getName(), cp.getCleanName());
        }

        this.size = c.getRegions().size();
        this.settlementCount = c.getSettlements().size();
        this.citizenCount = c.getCitizens().size();
        
        this.balance = UnitedLandsEconomyManager.instance().getBalance(c.getUuid()).doubleValue();
        this.upkeep = CostUtils.getCountryUpkeep(c);

        this.founded = c.getFoundingTimestamp();
        this.spawn = new LocationDTO(c.getSpawn());

    }
}

package org.unitedlands.unitedlands.classes.webservices.dto;

import java.util.Set;
import java.util.UUID;

import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;

public class CititzenDTO {
    public String uuid;
    public String username;
    public  SettlementDTO settlement;
    public CountryDTO country;
    public Double balance;
    public Long registered;
    public Long lastOnline;
    public Set<String> settlementRanks;
    public Set<String> countryRanks;

    public CititzenDTO(UUID id, String name)
    {
        this.uuid = id.toString();
        this.username = name;
    }

    public CititzenDTO(Citizen c)
    {
        this.uuid = c.getUuid().toString();
        this.username = c.getName();

        if (c.getSettlement() != null)
            this.settlement = new SettlementDTO(c.getSettlement());

        if (c.getCountry() != null)
            this.country = new CountryDTO(c.getCountry());

        this.balance = UnitedLandsEconomyManager.instance().getBalance(c.getUuid()).doubleValue();
        this.registered = c.getJoined();
        this.lastOnline = c.getLastLogon();
        this.settlementRanks = c.getSettlementRanks();
        this.countryRanks = c.getCountryRanks();
    }


}

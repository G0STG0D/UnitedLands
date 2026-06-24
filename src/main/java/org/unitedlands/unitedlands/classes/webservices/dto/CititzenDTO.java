package org.unitedlands.unitedlands.classes.webservices.dto;

import java.util.Set;
import java.util.UUID;

import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.managers.EconomyManager;

public class CititzenDTO {
    public String uuid;
    public String username;
    public  SettlementDTO settlement;
    // public CountryDTO country;
    public Double balance;
    public Long registered;
    public Long lastOnline;
    public Set<String> settlementRanks;
    public Set<String> countryRanks;

    public CititzenDTO(UUID id, String name)
    {
        uuid = id.toString();
        username = name;
    }

    public CititzenDTO(Citizen c)
    {
        uuid = c.getUuid().toString();
        username = c.getName();

        if (c.getSettlement() != null)
            settlement = new SettlementDTO(c.getSettlement());

        balance = EconomyManager.instance().getBalance(c.getUuid()).doubleValue();
        registered = c.getJoined();
        lastOnline = c.getLastLogon();
        settlementRanks = c.getSettlementRanks();
        countryRanks = c.getCountryRanks();
    }


}

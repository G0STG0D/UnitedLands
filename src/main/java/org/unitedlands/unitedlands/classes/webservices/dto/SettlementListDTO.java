package org.unitedlands.unitedlands.classes.webservices.dto;

import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.CostUtils;

public class SettlementListDTO {

    public String uuid;
    public String slug;
    public String name;
    public Integer color;
    public CititzenDTO mayor;
    public RegionDTO region;
    public CountryDTO country;
    public Double balance;
    public Long founded;
    public Integer size;
    public Integer citizenCount;
    public Double upkeep;
    public boolean isPublic;
    public boolean isOpen;

    public SettlementListDTO(Settlement s) {
        this.uuid = s.getUuid().toString();
        this.slug = s.getName();
        this.name = s.getCleanName();
       
        this.color = s.getFillColor();

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
        this.size = s.getChunks().size();
        this.citizenCount = s.getCitizens().size();
        this.upkeep = CostUtils.getSettlementUpkeep(s);
        this.isPublic = s.isPublic();
        this.isOpen = false;
    }
}

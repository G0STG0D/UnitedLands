package org.unitedlands.unitedlands.classes.webservices.dto;

import java.util.HashMap;
import java.util.Map;

import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.unitedlands.utils.CostUtils;

public class SettlementDTO {
    public String uuid;
    public String slug;
    public String name;
    public String board;
    public Integer color;
    public CititzenDTO mayor;

    public Double balance;
    public Long founded;
    public LocationDTO spawn;

    public Map<String, Boolean> toggles;

    public Double upkeep;

    public SettlementDTO(Settlement s) {

        uuid = s.getUuid().toString();
        slug = s.getName();
        name = s.getCleanName();
        board = s.getTownBoard();
        color = s.getFillColor();

        var m = s.getMayor();
        if (m != null)
            mayor = new CititzenDTO(m.getUuid(), m.getName());

        balance = EconomyManager.instance().getBalance(s.getUuid()).doubleValue();
        founded = s.getFoundingTimestamp();
        spawn = new LocationDTO(s.getSpawn());

        toggles = new HashMap<>();
        toggles.put("public", s.isPublic());
        toggles.put("pvp", s.allowPvp());
        toggles.put("animals", s.allowAnimals());
        toggles.put("monsters", s.allowMonsters());
        toggles.put("fire", s.allowFire());
        toggles.put("explosions", s.allowExplosions());
        toggles.put("open", false);
        toggles.put("neutral", false);

        upkeep = CostUtils.getSettlementUpkeep(s);
    }

}

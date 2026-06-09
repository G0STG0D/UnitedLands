package org.unitedlands.unitedlands.integrations.Towny;

import java.util.List;
import java.util.stream.Collectors;

import org.unitedlands.unitedlands.UnitedLands;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;

public class TownyProvider {

    public final UnitedLands plugin;

    private TownyAPI townyAPI;

    public TownyProvider(UnitedLands plugin) {
        this.plugin = plugin;

        townyAPI = TownyAPI.getInstance();
    }

    public List<String> getTownNames() {
        return townyAPI.getTowns().stream().map(Town::getName).collect(Collectors.toList());
    }

    public List<Town> getTowns() {
        return townyAPI.getTowns();
    }

    public Town getTown(String name) {
        return townyAPI.getTown(name);
    }

}

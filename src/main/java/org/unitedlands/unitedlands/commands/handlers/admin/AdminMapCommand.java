package org.unitedlands.unitedlands.commands.handlers.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;

public class AdminMapCommand extends BaseCommandHandler<UnitedLands> {

    public AdminMapCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    List<String> modes = List.of("settlements", "regions", "regionsdebug", "countries", "all");

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
        } else if (args.length == 2) {
            return modes;
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var renderer = Pl3xMapRenderer.instance();

        switch (args[1]) {
            case "settlements":
                renderer.renderSettlements(UnitedLandsDataManager.instance().getSettlements());
                break;
            case "regions":
                renderer.renderPolyRegions(UnitedLandsDataManager.instance().getRegions(), false);
                break;
            case "regionsdebug":
                renderer.renderPolyRegions(UnitedLandsDataManager.instance().getRegions(), true);
                break;
            case "countries":
                renderer.renderCountries(UnitedLandsDataManager.instance().getCountries());
                break;
            case "all":
                renderer.renderSettlements(UnitedLandsDataManager.instance().getSettlements());
                renderer.renderPolyRegions(UnitedLandsDataManager.instance().getRegions(), false);
                renderer.renderCountries(UnitedLandsDataManager.instance().getCountries());
                break;            
        }

    }

}

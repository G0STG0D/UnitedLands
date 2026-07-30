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
                renderer.addSettlementsToRenderQueue(UnitedLandsDataManager.instance().getSettlements());
                break;
            case "regions":
                renderer.setDebugMode(false);
                renderer.addRegionsToRenderQueue(UnitedLandsDataManager.instance().getRegions());
                break;
            case "regionsdebug":
                renderer.setDebugMode(true);
                renderer.addRegionsToRenderQueue(UnitedLandsDataManager.instance().getRegions());
                break;
            case "countries":
                renderer.addCountriesToRenderQueue(UnitedLandsDataManager.instance().getCountries());
                break;
            case "all":
                renderer.setDebugMode(false);
                renderer.addSettlementsToRenderQueue(UnitedLandsDataManager.instance().getSettlements());
                renderer.addRegionsToRenderQueue(UnitedLandsDataManager.instance().getRegions());
                renderer.addCountriesToRenderQueue(UnitedLandsDataManager.instance().getCountries());
                break;
        }

    }

}

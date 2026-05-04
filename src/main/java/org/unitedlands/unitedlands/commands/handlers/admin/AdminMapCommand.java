package org.unitedlands.unitedlands.commands.handlers.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.managers.GlobalDataManager;

public class AdminMapCommand extends BaseCommandHandler<UnitedLands> {

    public AdminMapCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    List<String> modes = List.of("settlements", "regions", "countries", "all");

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

        var renderer = plugin.getMapRenderer();

        switch (args[1]) {
            case "settlements":
                renderer.renderSettlements(GlobalDataManager.instance().getSettlements());
                break;
            case "regions":
                renderer.renderRegions(GlobalDataManager.instance().getRegions());
                renderer.renderRegions(GlobalDataManager.instance().getRegions());
                break;
            case "all":
                renderer.renderSettlements(GlobalDataManager.instance().getSettlements());
                renderer.renderRegions(GlobalDataManager.instance().getRegions());
                renderer.renderRegions(GlobalDataManager.instance().getRegions());
                break;            
        }

    }

}

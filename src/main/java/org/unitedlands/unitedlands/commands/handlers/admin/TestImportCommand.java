package org.unitedlands.unitedlands.commands.handlers.admin;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.RegionGenerator;
import org.unitedlands.utils.Logger;

public class TestImportCommand extends BaseCommandHandler<UnitedLands> {

    public TestImportCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] arg1) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var worldName = "world_earth";

        String file = "earth.png";
        boolean save = false;

        if (args.length >= 1) {
            worldName = args[0];
        }

        if (args.length >= 2) {
            file = args[1];
        }

        if (args.length >= 3) {
            save = args[2].equalsIgnoreCase("--save");
        }

        final boolean shouldSave = save;
        RegionGenerator.importRegionsAsync(worldName, file).thenAccept(regions -> {

            GlobalDataManager.instance().clearData();
            var counter = 1;
            for (var region : regions) {

                if (shouldSave) {
                    GlobalDataManager.instance().createRegionDbData(region);
                } else {
                    GlobalDataManager.instance().registerRegion(region);
                }

                Logger.log("Registered region " + region.getName() + " (" + counter + "/" + regions.size() + ")");
                counter++;
            }

            Pl3xMapRenderer.instance().renderRegions(GlobalDataManager.instance().getRegions());

        });

    }

}

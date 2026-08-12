package org.unitedlands.unitedlands.commands.handlers.admin;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;

@UnitedSubCommand(
        parent = CmdAdmin.class,
        name = "maprender",
        description = "Admin map render commands",
        usage = "/ula maprender <layer>",
        catchAll = true
)
public class CmdAdminMapRender implements UnitedCommandExecutor {

    List<String> modes = List.of("settlements", "regions", "regionsdebug", "countries", "all");

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return modes;
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (!(args.length == 1)) {
            sendUsage(sender);
            return;
        }

        var renderer = Pl3xMapRenderer.instance();

        switch (args[0]) {
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

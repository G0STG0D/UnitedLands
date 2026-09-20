package org.unitedlands.unitedlands.commands.handlers.region;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.infoscreen.RegionInfoScreen;

import org.unitedlands.unitedlands.managers.PlayerCacheManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
    parent          = CmdRegion.class,
    name            = "info",
    description     = "Shows information about a region",
    usage           = "/region info <region_name>",
    playerOnly      = true
)
public class CmdRegionInfo implements UnitedCommandExecutor {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return UnitedLandsDataManager.instance().getRegionNames();
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        Region region = null;
        if (args.length == 0) {
            var playerCache = PlayerCacheManager.instance().getPlayerCache((Player) sender);
            region = playerCache.getCachedRegion();
            if (region == null) {
                United.messenger().send(sender, "player.region.info.not-in-region");
                return;
            }
        } else {
            region = UnitedLandsDataManager.instance().getRegion(args[0]);
            if (region == null) {
                United.messenger().send(sender, "general-errors.region-not-found", args[0]);
                return;
            }
        }

        var infoscreen = new RegionInfoScreen(region, (Player) sender);
        infoscreen.send(sender);
    }

}

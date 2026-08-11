package org.unitedlands.unitedlands.commands.handlers.region;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.infoscreen.RegionInfoScreen;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.PlayerCacheManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

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
                Messenger.sendMessage(sender, MessageProvider.instance().get(Message.PLAYER__REGION__INFO__NOT_IN_REGION.path()),
                        null, MessageProvider.instance().get(Message.PREFIX.path()));
                return;
            }
        } else {
            region = UnitedLandsDataManager.instance().getRegion(args[0]);
            if (region == null) {
                Messenger.sendMessage(sender, MessageProvider.instance().get(Message.GENERAL_ERRORS__REGION_NOT_FOUND.path()),
                        Map.of("region", args[0]), MessageProvider.instance().get(Message.PREFIX.path()));
                return;
            }
        }

        var infoscreen = new RegionInfoScreen(UnitedLands.instance(), MessageProvider.instance(), region);
        infoscreen.send(sender);
    }

}

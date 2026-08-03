package org.unitedlands.unitedlands.commands.handlers.region;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.infoscreen.RegionInfoScreen;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.PlayerCacheManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class RegionInfoCommand extends BaseCommandHandler<UnitedLands> {

    public RegionInfoCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

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
                Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__REGION__INFO__NOT_IN_REGION.path()),
                        null, messageProvider.get(Message.PREFIX.path()));
                return;
            }
        } else {
            region = UnitedLandsDataManager.instance().getRegion(args[0]);
            if (region == null) {
                Messenger.sendMessage(sender, messageProvider.get(Message.GENERAL_ERRORS__REGION_NOT_FOUND.path()),
                        Map.of("region", args[0]), messageProvider.get(Message.PREFIX.path()));
                return;
            }
        }

        var infoscreen = new RegionInfoScreen(plugin, messageProvider, region);
        infoscreen.send(sender);
    }

}

package org.unitedlands.unitedlands.commands.handlers.regions;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;

public class RegionSetNameCommand extends BaseCommandHandler<UnitedLands> {

    public RegionSetNameCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length > 2)
            return;

        Player player = (Player) sender;

        Region region;

        if (args.length == 0) {
            region = GlobalDataManager.instance().getRegion(CoordinateUtils.locationToRegionCoordinates(player.getLocation()));
        } else {
            region = GlobalDataManager.instance().getRegion(args[0]);
        }

        if (region != null) {
            if (args.length == 1) {
                region.setName(args[0]);
            } else {
                region.setName(args[1]);
            }

            GlobalDataManager.instance().updateRegionDbData(region);
            
            plugin.getMapRenderer().removeRegion(region);
            plugin.getMapRenderer().renderRegion(region);
        }
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        if (args.length == 1)
        {
            return GlobalDataManager.instance().getRegionNames();
        }
        return null;
    }

}

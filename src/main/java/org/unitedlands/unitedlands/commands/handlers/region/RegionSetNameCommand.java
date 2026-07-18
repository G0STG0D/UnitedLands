package org.unitedlands.unitedlands.commands.handlers.region;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.PermissionManager;
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

        if (!PermissionManager.instance().hasGlobalOverrides(player))
            return;

        Region region;

        if (args.length == 0) {
            region = UnitedLandsDataManager.instance()
                    .getRegion(CoordinateUtils.locationToChunkCenterCoordinates(player.getLocation()));
        } else {
            region = UnitedLandsDataManager.instance().getRegion(args[0]);
        }

        if (region != null) {
            if (args.length == 1) {
                region.setName(args[0]);
            } else {
                region.setName(args[1]);
            }

            UnitedLandsDataManager.instance().updateRegionDbData(region);

            Pl3xMapRenderer.instance().renderPolyRegion(region);
        }
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        if (args.length == 1) {
            return UnitedLandsDataManager.instance().getRegionNames();
        }
        return null;
    }

}

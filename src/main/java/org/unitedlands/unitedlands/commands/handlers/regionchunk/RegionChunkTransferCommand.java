package org.unitedlands.unitedlands.commands.handlers.regionchunk;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;

public class RegionChunkTransferCommand extends BaseCommandHandler<UnitedLands> {

    public RegionChunkTransferCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1)
            return;

        Player player = (Player) sender;

        var regionChunk = GlobalDataManager.instance().getRegionChunk(CoordinateUtils.locationToRegionCoordinates(player.getLocation()));

        if (regionChunk == null)
            return;
        if (regionChunk.getRegion() == null)
            return;

        var sourceRegion = regionChunk.getRegion();

        var targetRegion = GlobalDataManager.instance().getRegion(args[0]);
        if (targetRegion == null)
            return;

        sourceRegion.removeChunk(regionChunk);
        regionChunk.setRegion(targetRegion);
        targetRegion.addChunk(regionChunk);

        GlobalDataManager.instance().updateRegionChunkDbData(regionChunk);
        GlobalDataManager.instance().updateRegionDbData(sourceRegion);
        GlobalDataManager.instance().updateRegionDbData(targetRegion);

        var mapRenderer = plugin.getMapRenderer();

        mapRenderer.removeRegion(sourceRegion);
        mapRenderer.removeRegion(targetRegion);

        mapRenderer.renderRegion(sourceRegion);
        mapRenderer.renderRegion(targetRegion);

    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return GlobalDataManager.instance().getRegionNames();
        }
        return null;
    }

}

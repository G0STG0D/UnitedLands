package org.unitedlands.unitedlands.commands.handlers.admin;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Coordinates;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.ColorUtils;
import org.unitedlands.unitedlands.utils.PolygonUtils;
import org.unitedlands.unitedlands.utils.SvgUtils;
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

        if (args.length >= 1) {
            worldName = args[0];
        }

        if (args.length >= 2) {
            file = args[1];
        }

        File importFolder = new File(UnitedLands.getInstance().getDataFolder(), "import");
        File imageFile = new File(importFolder, file + ".svg");
        if (!imageFile.exists()) {
            Logger.logError("Image not found: " + file, "UnitedLands");
            return;
        }

        try {
            var parsed = SvgUtils.loadFile(imageFile);

            World world = Bukkit.getWorld(worldName);
            var counter = 1;

            for (var entry : parsed.entrySet()) {

                boolean create = false;

                var region = UnitedLandsDataManager.instance().getRegion(entry.getKey());
                if (region == null) {
                    Logger.log("Region " + entry.getKey() + " not found, creating...");
                    create = true;
                    region = new Region();
                    region.setUuid(UUID.randomUUID());
                    region.setName(entry.getKey());
                } else {
                    Logger.log("Region " + entry.getKey() + " found, updating polygon...");
                }

                var val = entry.getValue();
                region.setPolygon(val.vertices);
                
                var center = PolygonUtils.calculatePolygonCenter(val.vertices);
                region.setHomeChunkCoordinates(new Coordinates((int)center[0] >> 4, (int)center[1] >> 4, worldName));

                if (val.color != null) {
                    region.setDebugStrokeColor(ColorUtils.hexToColor(val.color).getRGB());
                    region.setDebugFillColor(ColorUtils.hexToColor(val.color + "40").getRGB());
                }
                region.setWorld(world);

                if (create) {
                    UnitedLandsDataManager.instance().createRegionDbData(region);
                    Logger.log("Registered new region " + region.getName() + " (" + counter + "/" +
                            parsed.size() + ")");
                } else {
                    UnitedLandsDataManager.instance().updateRegionDbData(region);
                    Logger.log("Updated region " + region.getName() + " (" + counter + "/" +
                            parsed.size() + ")");
                }

                counter++;
            }

            UnitedLandsDataManager.instance().buildRegionIndex();

            Pl3xMapRenderer.instance().renderPolyRegions(UnitedLandsDataManager.instance().getRegions(), false);

        } catch (Exception ex) {
            Logger.logError("Error parsing " + file + ": " + ex.getMessage(), "UnitedLands");
            ex.printStackTrace();
            return;
        }

        // RegionGenerator.importRegionsAsync(worldName, file).thenAccept(regions -> {

        // GlobalDataManager.instance().clearData();
        // var counter = 1;
        // for (var region : regions) {

        // if (shouldSave) {
        // GlobalDataManager.instance().createRegionDbData(region);
        // } else {
        // GlobalDataManager.instance().registerRegion(region);
        // }

        // Logger.log("Registered region " + region.getName() + " (" + counter + "/" +
        // regions.size() + ")");
        // counter++;
        // }

        // Pl3xMapRenderer.instance().renderRegions(GlobalDataManager.instance().getRegions(),
        // isDebug);

        // });

    }

}

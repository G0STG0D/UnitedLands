package org.unitedlands.unitedlands.commands.handlers.admin;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.RegionGenerator;
import org.unitedlands.utils.Logger;

public class TestOutputCommand extends BaseCommandHandler<UnitedLands> {

    private final int WIDTH = 40; // Number of region chunks for the width
    private final int HEIGHT = 40; // Number of region chunks for the height
    private final int SPACING = 8; // Width / height of one region chunk
    private final int JITTER = 2; // random variation

    public TestOutputCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] arg1) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var worldName = ((Player) sender).getWorld().getName();

        int width = WIDTH;
        int height = HEIGHT;
        int spacing = SPACING;
        int jitter = JITTER;
        String mode = "euclid";

        if (args.length >= 1) {
            try {
                worldName = args[0];
            } catch (Exception ignore) {

            }
        }

        if (args.length >= 2) {
            try {
                width = Integer.parseInt(args[1]);
            } catch (Exception ignore) {

            }
        }

        if (args.length >= 3) {
            try {
                height = Integer.parseInt(args[2]);
            } catch (Exception ignore) {

            }
        }

        if (args.length >= 4) {
            try {
                spacing = Integer.parseInt(args[3]);
            } catch (Exception ignore) {

            }
        }

        if (args.length >= 5) {
            try {
                jitter = Integer.parseInt(args[4]);
            } catch (Exception ignore) {

            }
        }

        if (args.length >= 6) {
            try {
                mode = args[5];
            } catch (Exception ignore) {

            }
        }

        RegionGenerator.generateRegionsAsync(worldName, width, height, spacing, jitter, mode).thenAccept(regions -> {

            Logger.log("Starting rendering mode...");

            GlobalDataManager.instance().clearData();
            for (var region : regions) {
                for (var regionChunk : region.getChunks()) {
                    GlobalDataManager.instance().registerRegionChunk(regionChunk);
                }
                GlobalDataManager.instance().registerRegion(region);
                Logger.log("Registered region " + region.getName());
            }

            plugin.getMapRenderer().renderRegions(GlobalDataManager.instance().getRegions());

        });

    }

}

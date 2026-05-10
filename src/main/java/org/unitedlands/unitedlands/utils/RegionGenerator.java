package org.unitedlands.unitedlands.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.unitedlands.classes.ConfigFile;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Coordinates;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.RegionChunk;
import org.unitedlands.utils.Logger;

public class RegionGenerator {

    public static CompletableFuture<Collection<Region>> importRegionsAsync(
            String worldName,
            String file) {

        return CompletableFuture.supplyAsync(() -> {

            Logger.log("Starting asynchronous region import...");
            long startTime = System.currentTimeMillis();

            File importFolder = new File(UnitedLands.getInstance().getDataFolder(), "import");
            File imageFile = new File(importFolder, file + ".png");

            ConfigFile configImport = new ConfigFile(UnitedLands.getInstance(), "import/" + file + ".yml");
            var configSection = configImport.get();
            var nameColorKeys = configSection.getKeys(false);

            Map<String, String> colorNames = new HashMap<>();
            for (var key : nameColorKeys) {
                var newKey = "#" + key.toUpperCase();
                var name = configSection.getString(key);
                colorNames.put(newKey, name);
                Logger.log("Added color name key " + newKey + ": " + name);
            }

            configImport.reload();

            if (!imageFile.exists()) {
                Logger.logError("Image not found: " + file, "UnitedLands");
                return null;
            }

            BufferedImage img;
            try {
                img = ImageIO.read(imageFile);
            } catch (Exception ex) {
                Logger.logError("Failed to read image: unsupported format", "UnitedLands");
                return null;
            }

            int widthOffset = img.getWidth() / 2;
            int heightOffset = img.getHeight() / 2;
            World world = Bukkit.getWorld(worldName);

            Random rnd = new Random();
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

            Map<Integer, Region> colorMap = new HashMap<>();
            Integer ignore = Color.BLACK.getRGB();

            for (int x = 0; x < img.getWidth(); x++) {
                for (int y = 0; y < img.getHeight(); y++) {
                    var pxcolor = img.getRGB(x, y);
                    if (pxcolor == ignore)
                        continue;

                    Region region;
                    if (colorMap.containsKey(pxcolor)) {
                        region = colorMap.get(pxcolor);
                    } else {
                        region = new Region();
                        region.setUuid(UUID.randomUUID());

                        var hexColor = ColorUtils.argbToHex(pxcolor);
                        if (colorNames.containsKey(hexColor)) {
                            region.setName(colorNames.get(hexColor));
                        } else {
                            region.setName("" + chars.charAt(rnd.nextInt(chars.length()))
                                    + chars.charAt(rnd.nextInt(chars.length()))
                                    + chars.charAt(rnd.nextInt(chars.length()))
                                    + chars.charAt(rnd.nextInt(chars.length()))
                                    + chars.charAt(rnd.nextInt(chars.length())));
                        }

                        region.setWorld(world);
                        colorMap.put(pxcolor, region);
                    }

                    var c = new Coordinates(worldName);
                    c.setX(x - widthOffset);
                    c.setZ(y - heightOffset);

                    RegionChunk rc = new RegionChunk();
                    rc.setUuid(UUID.randomUUID());
                    rc.setCoordinates(c);
                    rc.setWorld(world);
                    rc.setRegion(region);

                    region.addChunk(rc);
                }
            }

            Logger.log("Generated " + colorMap.values().size() + " regions in "
                    + (System.currentTimeMillis() - startTime) + "ms");

            return colorMap.values();
        });
    }

    public static CompletableFuture<Collection<Region>> generateRegionsAsync(
            String worldName,
            int width,
            int height,
            int spacing,
            int jitter,
            String mode) {

        return CompletableFuture.supplyAsync(() -> {

            Logger.log("Starting asynchronous region generation...");
            long startTime = System.currentTimeMillis();

            int widthOffset = width / 2;
            int heightOffset = height / 2;

            var horizontalCenterCount = (width / spacing);
            var verticalCenterCount = (height / spacing);
            var halfSpacing = spacing / 2;

            boolean avoidOceanRegions = false;

            Map<Coordinates, Region> regionCenters = new HashMap<>();
            Set<RegionChunk> generatedChunks = new HashSet<>();

            Random rnd = new Random();
            String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

            World world = Bukkit.getWorld(worldName);

            for (int x = 0; x < horizontalCenterCount; x++) {
                for (int y = 0; y < verticalCenterCount; y++) {

                    Region region = new Region();
                    region.setUuid(UUID.randomUUID());
                    region.setName("" + chars.charAt(rnd.nextInt(chars.length()))
                            + chars.charAt(rnd.nextInt(chars.length()))
                            + chars.charAt(rnd.nextInt(chars.length())) + chars.charAt(rnd.nextInt(chars.length()))
                            + chars.charAt(rnd.nextInt(chars.length())));
                    region.setWorld(world);

                    var c = new Coordinates(worldName);

                    var rx = halfSpacing + (x * spacing) + rnd.nextInt(-1 * jitter, jitter + 1);
                    var rz = halfSpacing + (y * spacing) + rnd.nextInt(-1 * jitter, jitter + 1);

                    c.setX(Math.max(-widthOffset, Math.min(widthOffset, rx - widthOffset)));
                    c.setZ(Math.max(-heightOffset, Math.min(heightOffset, rz - heightOffset)));

                    if (avoidOceanRegions) {
                        var sampleChunk = new RegionChunk();
                        sampleChunk.setCoordinates(c);

                        var sampleCenter = sampleChunk.getCenter();

                        Block b = world.getHighestBlockAt(sampleCenter.getX(), sampleCenter.getZ());
                        var biome = b.getBiome();

                        if (!biome.key().asMinimalString().contains("ocean")) {
                            regionCenters.put(c, region);
                        }
                    } else {
                        regionCenters.put(c, region);
                    }
                }
            }

            for (int x = -widthOffset; x < widthOffset; x++) {
                for (int y = -heightOffset; y < heightOffset; y++) {

                    Coordinates closestRegionCenterCoord = regionCenters.keySet().stream().findFirst().orElse(null);
                    float closestDistance = Float.MAX_VALUE;
                    for (var c : regionCenters.keySet()) {
                        float dist = getDistance(x, y, c.getX(), c.getZ(), mode);
                        if (dist < closestDistance) {
                            closestDistance = dist;
                            closestRegionCenterCoord = c;
                        }
                    }

                    var finalClosestRegionCenterCoord = closestRegionCenterCoord;
                    var regionCenterCoord = regionCenters.keySet().stream().filter(
                            c -> c.getX() == finalClosestRegionCenterCoord.getX()
                                    && c.getZ() == finalClosestRegionCenterCoord.getZ())
                            .findFirst().orElse(null);
                    var region = regionCenters.get(regionCenterCoord);

                    RegionChunk rc = new RegionChunk();
                    rc.setUuid(UUID.randomUUID());
                    rc.setCoordinates(new Coordinates(x, y, worldName));
                    rc.setWorld(world);
                    rc.setRegion(region);

                    region.addChunk(rc);
                    generatedChunks.add(rc);
                }
            }

            Logger.log("Generated " + regionCenters.values().size() + " regions in "
                    + (System.currentTimeMillis() - startTime) + "ms");

            return regionCenters.values();
        });
    }

    private static float getDistance(int x, int y, int x2, int y2, String mode) {

        if (mode.equalsIgnoreCase("manhatten")) {
            var dX = Math.abs(x - x2);
            var dY = Math.abs(y - y2);
            return dX + dY;
        } else {
            var dX = x - x2;
            var dY = y - y2;
            return dX * dX + dY * dY;
        }
    }

}

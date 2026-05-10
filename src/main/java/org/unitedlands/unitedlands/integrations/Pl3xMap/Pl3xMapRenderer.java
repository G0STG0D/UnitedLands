package org.unitedlands.unitedlands.integrations.Pl3xMap;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.unitedlands.unitedlands.classes.Coordinates;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.classes.interfaces.CoordinateHolder;
import org.unitedlands.unitedlands.classes.map.LayerOptions;
import org.unitedlands.utils.Logger;

import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.markers.layer.SimpleLayer;
import net.pl3x.map.core.markers.marker.MultiPolygon;
import net.pl3x.map.core.markers.marker.Polygon;
import net.pl3x.map.core.markers.marker.Polyline;
import net.pl3x.map.core.markers.option.Fill;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.markers.option.Popup;

public class Pl3xMapRenderer {

    private static Pl3xMapRenderer instance;
    public static Pl3xMapRenderer instance() {
        return instance;
    }

    public Pl3xMapRenderer() {
        instance = this;
    }

    private enum DIRECTION {
        RIGHT, DOWN, UP, LEFT
    };

    class ChunkCluster {
        private Set<CoordinateHolder> chunks = new HashSet<>();
    }

    // #region Country rendering

    // *********************************************************
    // Country rendering
    // *********************************************************

    public void renderCountry(Country country) {
        renderCountries(List.of(country));
    }

    public void renderCountries(Collection<Country> countries) {

        Logger.log("Starting country map rendering...", "UnitedLands");

        if (countries == null || countries.isEmpty()) {
            Logger.log("No countries to render.", "UnitedLands");
            return;
        }

        var startTime = System.currentTimeMillis();
        List<CompletableFuture<Void>> futures = countries.stream()
                .map(country -> CompletableFuture.runAsync(() -> renderCountryAsync(country)))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    var executionTime = System.currentTimeMillis() - startTime;
                    Logger.log("Created " + countries.size() + " countries in map overlay in " + executionTime + "ms");
                })
                .exceptionally(ex -> {
                    Logger.logError("Error rendering countries: " + ex.getMessage(), "UnitedLands");
                    ex.printStackTrace();
                    return null;
                });

    }

    private void renderCountryAsync(Country country) {

        SimpleLayer layer = getOrCreateSimpleLayer(country.getWorldName(),
                "countries",
                "Countries",
                1,
                1000);

        var key = "country-" + country.getUuid().toString();
        if (layer.hasMarker(key))
            layer.removeMarker(key);

        var popup = new Popup(
                "<div><p><strong>" + country.getCleanName() + "</strong></p><p><strong>Owner: </strong>");

        int strokeWidth = Settings.defaultCountryStrokeWidth;

        var markerOptions = Options.builder()
                .fill(false)
                .stroke(true)
                .strokeColor(country.getStrokeColor())
                .strokeWeight(strokeWidth)
                .tooltipContent(country.getCleanName()).build()
                .setPopup(popup);

        var countryChunks = country.getRegions().stream().flatMap(r -> r.getChunks().stream())
                .collect(Collectors.toSet());

        var chunkClusters = findClusters(countryChunks);
        List<Polygon> polygons = new ArrayList<>();

        int i = 0;
        for (var cluster : chunkClusters) {

            List<Polyline> clusterLines = new ArrayList<>();

            clusterLines.add(new Polyline("border-" + country.getUuid().toString() + "-" + i,
                    generatePolygon(cluster.chunks)));

            var holeClusters = findHoles(cluster, country.getWorldName());
            int j = 0;
            for (var holeCluster : holeClusters) {
                clusterLines.add(new Polyline(
                        "hole-" + country.getUuid().toString() + "-" + j,
                        generatePolygon(holeCluster.chunks)));
                j++;
            }

            polygons.add(new Polygon("cluster-" + country.getUuid().toString() + "-" + i, clusterLines));
        }
        MultiPolygon mapPolygon = new MultiPolygon(key, polygons);

        mapPolygon.setOptions(markerOptions);

        layer.addMarker(mapPolygon);
    }

    public void removeCountry(Country country) {

        net.pl3x.map.core.world.World mapworld = Pl3xMap.api().getWorldRegistry().get(country.getWorldName());
        var settlementsLayer = mapworld.getLayerRegistry().get("countries");

        if (settlementsLayer != null) {
            var settlemenMarkers = settlementsLayer.getMarkers();
            var poly = settlemenMarkers.stream()
                    .filter(m -> m.getKey().equals("settlement-" + country.getUuid().toString())).findFirst()
                    .orElse(null);
            if (poly != null) {
                Logger.log("Removing country...", "UnitedLands");
                settlemenMarkers.remove(poly);
                return;
            }
        }
    }

    // #endregion

    // #region Region rendering

    // *********************************************************
    // Region rendering
    // *********************************************************

    public void renderRegion(Region region) {
        renderRegions(List.of(region));
    }

    public void renderRegions(Collection<Region> regions) {

        Logger.log("Starting region map rendering...", "UnitedLands");

        if (regions == null || regions.isEmpty()) {
            Logger.log("No regions to render.", "UnitedLands");
            return;
        }

        var startTime = System.currentTimeMillis();
        List<CompletableFuture<Void>> futures = regions.stream()
                .map(region -> CompletableFuture.runAsync(() -> renderRegionAsync(region)))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    var executionTime = System.currentTimeMillis() - startTime;
                    Logger.log("Created " + regions.size() + " regions in map overlay in " + executionTime + "ms");
                })
                .exceptionally(ex -> {
                    Logger.logError("Error rendering regions: " + ex.getMessage(), "UnitedLands");
                    return null;
                });

    }

    private void renderRegionAsync(Region region) {
        SimpleLayer layer;
        if (!region.hasCountry()) {
            layer = getOrCreateSimpleLayer(region.getWorldName(),
                    "unclaimedregions",
                    "Unclaimed Regions",
                    8,
                    0);
        } else {
            layer = getOrCreateSimpleLayer(region.getWorldName(),
                    "claimedregions",
                    "Claimed Regions",
                    6,
                    0);
        }

        var key = "region-" + region.getUuid().toString();
        if (layer.hasMarker(key))
            layer.removeMarker(key);

        var popup = new Popup(
                "<div><p><strong>" + region.getCleanName() + "</strong></p><p><strong>Owner: </strong>"
                        + (region.hasFounder() ? region.getFounderName() : "-") + "</p></div>");

        String dash = Settings.defaultRegionDash;
        int strokeWidth = Settings.defaultRegionStrokeWidth;

        if (region.hasCountry()) {
            dash = Settings.countryRegionDash;
            strokeWidth = Settings.countryRegionStrokeWidth;
        }

        var tooltip = region.getCleanName() + (region.hasCountry() ? " (" + region.getCountry().getCleanName() + ")" : "");
        var markerOptions = Options.builder()
                .fill(true)
                .fillType(Fill.Type.NONZERO)
                .fillColor(region.getFillColor())
                .stroke(true)
                .strokeColor(region.getStrokeColor())
                .strokeDashPattern(dash)
                .strokeWeight(strokeWidth)
                .tooltipContent(tooltip).build()
                .setPopup(popup);

        var chunkClusters = findClusters(region.getChunks());
        List<Polygon> polygons = new ArrayList<>();

        int i = 0;
        for (var cluster : chunkClusters) {

            List<Polyline> clusterLines = new ArrayList<>();

            clusterLines.add(new Polyline("border-" + region.getUuid().toString() + "-" + i,
                    generatePolygon(cluster.chunks)));

            var holeClusters = findHoles(cluster, region.getWorldName());
            int j = 0;
            for (var holeCluster : holeClusters) {
                clusterLines.add(new Polyline(
                        "hole-" + region.getUuid().toString() + "-" + j,
                        generatePolygon(holeCluster.chunks)));
                j++;
            }

            polygons.add(new Polygon("cluster-" + region.getUuid().toString() + "-" + i, clusterLines));
        }
        MultiPolygon mapPolygon = new MultiPolygon(key, polygons);

        mapPolygon.setOptions(markerOptions);

        layer.addMarker(mapPolygon);
    }

    public void removeRegion(Region region) {
        String key = "region-" + region.getUuid().toString();
        var unclaimedLayer = getOrCreateSimpleLayer(region.getWorldName(),
                "unclaimedregions",
                "Unclaimed Regions",
                30,
                30);
        if (unclaimedLayer.hasMarker(key))
            unclaimedLayer.removeMarker(key);

        var claimedLayer = getOrCreateSimpleLayer(region.getWorldName(),
                "claimedregions",
                "Claimed Regions",
                20,
                20);
        if (claimedLayer.hasMarker(key))
            claimedLayer.removeMarker(key);
    }

    // #endregion

    // #region Settlement rendering

    // *********************************************************
    // Settlement rendering
    // *********************************************************

    public void renderSettlement(Settlement settlement) {
        renderSettlements(List.of(settlement));
    }

    public void renderSettlements(Collection<Settlement> settlements) {

        Logger.log("Starting settlement map rendering...", "UnitedLands");

        if (settlements == null || settlements.isEmpty()) {
            Logger.log("No settlements to render.", "UnitedLands");
            return;
        }

        var startTime = System.currentTimeMillis();
        List<CompletableFuture<Void>> futures = settlements.stream()
                .map(settlement -> CompletableFuture.runAsync(() -> renderSettlementAsync(settlement)))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    var executionTime = System.currentTimeMillis() - startTime;
                    Logger.log(
                            "Created " + settlements.size() + " settlements in map overlay in " + executionTime + "ms");
                })
                .exceptionally(ex -> {
                    Logger.logError("Error rendering settlements: " + ex.getMessage(), "UnitedLands");
                    return null;
                });
    }

    private void renderSettlementAsync(Settlement settlement) {

        SimpleLayer layer = getOrCreateSimpleLayer(settlement.getWorldName(),
                "settlements",
                "Settlements",
                1,
                1000);

        String uuid = settlement.getUuid().toString();
        String key = "settlement-" + uuid;
        if (layer.hasMarker(key))
            layer.removeMarker(key);

        var popup = new Popup(
                "<div><p><strong>" + settlement.getCleanName() + "</strong></p><p><strong>Owner: </strong>"
                        + (settlement.hasFounder() ? settlement.getFounderName() : "-") + "</p></div>");

        int strokeWidth = Settings.defaultSettlementStrokeWidth;
        String dash = Settings.defaultSettlementDash;

        if (settlement.hasCountry()) {
            strokeWidth = Settings.countrySettlementStrokeWidth;
            dash = Settings.countrySettlementDash;
        }

        var markerOptions = Options.builder()
                .fill(true)
                .fillType(Fill.Type.EVENODD)
                .fillColor(settlement.getFillColor())
                .stroke(true)
                .strokeColor(settlement.getStrokeColor())
                .strokeWeight(strokeWidth)
                .strokeDashPattern(dash)
                .tooltipContent(settlement.getCleanName()).build()
                .setPopup(popup);

        var chunkClusters = findClusters(settlement.getChunks());
        List<Polygon> polygons = new ArrayList<>();

        int i = 0;
        for (var cluster : chunkClusters) {

            List<Polyline> clusterLines = new ArrayList<>();

            clusterLines.add(new Polyline("border-" + uuid + "-" + i,
                    generatePolygon(cluster.chunks)));

            var holeClusters = findHoles(cluster, settlement.getWorldName());
            int j = 0;
            for (var holeCluster : holeClusters) {
                clusterLines.add(new Polyline(
                        "hole-" + uuid + "-" + j,
                        generatePolygon(holeCluster.chunks)));
                j++;
            }

            polygons.add(new Polygon("cluster-" + uuid + "-" + i, clusterLines));
        }
        MultiPolygon mapPolygon = new MultiPolygon(key, polygons);

        mapPolygon.setOptions(markerOptions);

        layer.addMarker(mapPolygon);
    }

    public void removeSettlement(Settlement settlement) {

        net.pl3x.map.core.world.World mapworld = Pl3xMap.api().getWorldRegistry().get(settlement.getWorldName());
        var settlementsLayer = mapworld.getLayerRegistry().get("settlements");

        if (settlementsLayer != null) {
            var settlemenMarkers = settlementsLayer.getMarkers();
            var poly = settlemenMarkers.stream()
                    .filter(m -> m.getKey().equals("settlement-" + settlement.getUuid().toString())).findFirst()
                    .orElse(null);
            if (poly != null) {
                Logger.log("Removing settlement...", "UnitedLands");
                settlemenMarkers.remove(poly);
                return;
            }
        }
    }

    // #endregion

    // #region Helpers & Utilities

    // *******************************
    // Chunk cluster detections
    // *******************************

    public Set<ChunkCluster> findClusters(Set<? extends CoordinateHolder> chunks) {
        if (chunks == null || chunks.isEmpty())
            return Collections.emptySet();

        Map<String, CoordinateHolder> lookup = new HashMap<>((4 * chunks.size()) / 3);
        for (CoordinateHolder chunk : chunks) {
            lookup.put(toKey(chunk.getCoordinates()), chunk);
        }

        Set<ChunkCluster> clusters = new HashSet<>();

        while (!lookup.isEmpty()) {
            ChunkCluster cluster = new ChunkCluster();
            Deque<String> stack = new ArrayDeque<>();

            stack.push(lookup.keySet().iterator().next());

            while (!stack.isEmpty()) {
                String key = stack.pop();
                CoordinateHolder chunk = lookup.remove(key);

                if (chunk == null)
                    continue;

                cluster.chunks.add(chunk);

                Coordinates c = chunk.getCoordinates();
                int[] offsets = { -1, 1 };
                for (int i = 0; i < 2; i++) {
                    for (int dir : offsets) {
                        int nx = c.getX() + (i == 0 ? dir : 0);
                        int nz = c.getZ() + (i == 1 ? dir : 0);
                        String neighbourKey = toKey(nx, nz, c.getWorldName());
                        if (lookup.containsKey(neighbourKey))
                            stack.push(neighbourKey);
                    }
                }
            }

            if (!cluster.chunks.isEmpty())
                clusters.add(cluster);
        }

        return clusters;
    }

    private String toKey(Coordinates c) {
        return toKey(c.getX(), c.getZ(), c.getWorldName());
    }

    private String toKey(int x, int z, String world) {
        return x + "," + z + "," + world;
    }

    // *********************************************
    // Negative space detection
    // *********************************************

    public Set<ChunkCluster> findHoles(ChunkCluster cluster, String worldName) {
        if (cluster.chunks.isEmpty())
            return Collections.emptySet();

        // Build a lookup set of all occupied coordinates in this cluster
        Set<String> occupied = new HashSet<>();
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (CoordinateHolder chunk : cluster.chunks) {
            Coordinates c = chunk.getCoordinates();
            occupied.add(toKey(c.getX(), c.getZ(), worldName));
            minX = Math.min(minX, c.getX());
            maxX = Math.max(maxX, c.getX());
            minZ = Math.min(minZ, c.getZ());
            maxZ = Math.max(maxZ, c.getZ());
        }

        // Expand bounding box by 1 in all directions so the flood-fill
        // seed is guaranteed to be outside the cluster
        minX--;
        maxX++;
        minZ--;
        maxZ++;

        // Flood-fill from the top-left corner outward, through all empty chunks
        // within the expanded bounding box. Any empty chunk reachable from here
        // is NOT a hole.
        Set<String> reachable = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        String seedKey = toKey(minX, minZ, worldName);
        stack.push(seedKey);
        reachable.add(seedKey);

        int[] offsets = { -1, 1 };
        while (!stack.isEmpty()) {
            String key = stack.pop();
            // Decode the key back to coordinates for neighbour calculation
            String[] parts = key.split(",");
            int x = Integer.parseInt(parts[0]);
            int z = Integer.parseInt(parts[1]);

            for (int i = 0; i < 2; i++) {
                for (int dir : offsets) {
                    int nx = x + (i == 0 ? dir : 0);
                    int nz = z + (i == 1 ? dir : 0);

                    // Stay within the expanded bounding box
                    if (nx < minX || nx > maxX || nz < minZ || nz > maxZ)
                        continue;

                    String neighbourKey = toKey(nx, nz, worldName);
                    if (!occupied.contains(neighbourKey) && reachable.add(neighbourKey))
                        stack.push(neighbourKey);
                }
            }
        }

        // Collect all empty chunks inside the (unexpanded) bounding box
        // that were never reached — these are holes
        Set<SettlementChunk> holeChunks = new HashSet<>();
        for (int x = minX + 1; x <= maxX - 1; x++) {
            for (int z = minZ + 1; z <= maxZ - 1; z++) {
                String key = toKey(x, z, worldName);
                if (!occupied.contains(key) && !reachable.contains(key)) {
                    // Create a synthetic SettlementChunk to represent this empty position
                    SettlementChunk hole = new SettlementChunk(new Coordinates(x, z, worldName));
                    holeChunks.add(hole);
                }
            }
        }

        // Group the hole chunks into connected components — each is a distinct hole
        return findClusters(holeChunks);
    }

    // *******************************
    // Polygon creation
    // *******************************

    private List<Point> generatePolygon(Set<CoordinateHolder> chunks) {

        Set<Coordinates> polygon = new LinkedHashSet<>();

        var rightmostChunk = findRightmostChunk(chunks);

        var originPoint = rightmostChunk.getUpperLeft();

        Queue<CoordinateHolder> chunksToVisit = new ArrayDeque<>(1);
        chunksToVisit.add(rightmostChunk);

        DIRECTION currDir = DIRECTION.RIGHT;

        boolean isOriginPoint = true;
        while (!chunksToVisit.isEmpty()) {

            var chunk = chunksToVisit.poll();

            switch (currDir) {
                case RIGHT: {
                    var upperLeft = chunk.getUpperLeft();
                    if (!isOriginPoint && upperLeft.equals(originPoint))
                        continue;
                    else if (isOriginPoint)
                        isOriginPoint = false;

                    var upCoords = chunk.getCoordinates().clone().add(0, -1);
                    var rightCoords = chunk.getCoordinates().clone().add(1, 0);
                    // Check if there's a chunk above
                    if (isChunkAtCoordinates(chunks, upCoords)) {
                        chunksToVisit.add(getChunkAtCoordinates(chunks, upCoords));
                        currDir = DIRECTION.UP;
                        polygon.add(chunk.getUpperLeft());
                        // Check if there's a chunk to the right
                    } else if (isChunkAtCoordinates(chunks, rightCoords)) {
                        chunksToVisit.add(getChunkAtCoordinates(chunks, rightCoords));
                        // We're the rightmost, so switch direction to down and queue the same block
                    } else {
                        chunksToVisit.add(chunk);
                        currDir = DIRECTION.DOWN;
                        polygon.add(chunk.getUpperRight());
                    }
                    break;
                }
                case LEFT: {
                    var downCoords = chunk.getCoordinates().clone().add(0, 1);
                    var leftCoords = chunk.getCoordinates().clone().add(-1, 0);
                    // Check if there's a chunk below
                    if (isChunkAtCoordinates(chunks, downCoords)) {
                        chunksToVisit.add(getChunkAtCoordinates(chunks, downCoords));
                        currDir = DIRECTION.DOWN;
                        polygon.add(chunk.getLowerRight());
                        // Check if there's a chunk to the right
                    } else if (isChunkAtCoordinates(chunks, leftCoords)) {
                        chunksToVisit.add(getChunkAtCoordinates(chunks, leftCoords));
                        // We're the the leftmost, so switch direction to up
                    } else {
                        chunksToVisit.add(chunk);
                        currDir = DIRECTION.UP;
                        polygon.add(chunk.getLowerLeft());

                    }
                    break;
                }
                case DOWN: {
                    var rightCoords = chunk.getCoordinates().clone().add(1, 0);
                    var downCoords = chunk.getCoordinates().clone().add(0, 1);
                    // Check if there's a chunk to the right
                    if (isChunkAtCoordinates(chunks, rightCoords)) {
                        chunksToVisit.add(getChunkAtCoordinates(chunks, rightCoords));
                        currDir = DIRECTION.RIGHT;
                        polygon.add(chunk.getUpperRight());
                        // Check if there's a chunk below
                    } else if (isChunkAtCoordinates(chunks, downCoords)) {
                        chunksToVisit.add(getChunkAtCoordinates(chunks, downCoords));
                        // We're the the bottom most, so switch direction to left
                    } else {
                        chunksToVisit.add(chunk);
                        currDir = DIRECTION.LEFT;
                        polygon.add(chunk.getLowerRight());
                    }
                    break;
                }
                case UP: {
                    var leftCoords = chunk.getCoordinates().clone().add(-1, 0);
                    var upCoords = chunk.getCoordinates().clone().add(0, -1);
                    // Check if there's a chunk to the left
                    if (isChunkAtCoordinates(chunks, leftCoords)) {
                        chunksToVisit.add(getChunkAtCoordinates(chunks, leftCoords));
                        currDir = DIRECTION.LEFT;
                        polygon.add(chunk.getLowerLeft());
                        // Check if there's a chunk below
                    } else if (isChunkAtCoordinates(chunks, upCoords)) {
                        chunksToVisit.add(getChunkAtCoordinates(chunks, upCoords));
                        // We're the the top most, so switch direction to right
                    } else {
                        chunksToVisit.add(chunk);
                        currDir = DIRECTION.RIGHT;
                        polygon.add(chunk.getUpperLeft());

                    }
                    break;
                }
            }
        }

        List<Point> points = new ArrayList<>();
        for (var p : polygon) {
            points.add(new Point(p.getX(), p.getZ()));
        }
        return points;
    }

    // *******************************
    // Helpers
    // *******************************

    private SimpleLayer getOrCreateSimpleLayer(String world, String key, String name, int priority, int zindex) {

        net.pl3x.map.core.world.World mapworld = Pl3xMap.api().getWorldRegistry().get(world);
        SimpleLayer layer = (SimpleLayer) mapworld.getLayerRegistry().get(key);

        if (layer == null) {
            var options = new LayerOptions(name, true, false, priority, zindex);
            layer = new SimpleLayer(key, options::getName);
            layer.setDefaultHidden(options.isDefaultHidden());
            layer.setPriority(options.getLayerPriority());
            layer.setZIndex(options.getZIndex());
            layer.setShowControls(options.showControls());
            mapworld.getLayerRegistry().register(layer);
        }

        return layer;
    }

    private boolean isChunkAtCoordinates(Set<CoordinateHolder> chunks, Coordinates coords) {
        return chunks.stream().anyMatch(c -> c.getCoordinates().equals(coords));
    }

    private CoordinateHolder getChunkAtCoordinates(Set<CoordinateHolder> chunks, Coordinates coords) {
        return chunks.stream().filter(c -> c.getCoordinates().equals(coords)).findFirst().orElse(null);
    }

    private CoordinateHolder findRightmostChunk(Set<CoordinateHolder> chunks) {
        var rightmost = chunks.stream().findAny().orElse(null);
        for (var chunk : chunks) {
            if (chunk.getCoordinates().getX() > rightmost.getCoordinates().getX()
                    || (chunk.getCoordinates().getX() == rightmost.getCoordinates().getX()
                            && chunk.getCoordinates().getZ() < rightmost.getCoordinates().getZ())) {
                rightmost = chunk;
            }
        }
        return rightmost;
    }
}

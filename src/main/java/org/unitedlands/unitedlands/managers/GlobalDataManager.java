package org.unitedlands.unitedlands.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Coordinates;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.RegionChunk;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.utils.Logger;

public class GlobalDataManager {

    private static GlobalDataManager instance;

    public static GlobalDataManager instance() {
        return instance;
    }

    private final DatabaseManager databaseManager;

    private Map<UUID, Citizen> citizens = new HashMap<>();
    private Map<UUID, Settlement> settlements = new HashMap<>();
    private Map<Coordinates, SettlementChunk> settlementChunks = new HashMap<>();
    private Map<UUID, Region> regions = new HashMap<>();
    private Map<Coordinates, RegionChunk> regionChunks = new HashMap<>();
    private Map<UUID, Country> countries = new HashMap<>();

    public GlobalDataManager(UnitedLands plugin, Pl3xMapRenderer mapRenderer) {
        instance = this;
        
        databaseManager = new DatabaseManager(plugin);
        databaseManager.initialize();
    }

    public void loadDataFromDatabase() {

        CompletableFuture<List<Country>> countryFuture = databaseManager
                .getCountryService()
                .getAllAsync();
        CompletableFuture<List<Region>> regionFuture = databaseManager
                .getRegionService()
                .getAllAsync();
        CompletableFuture<List<RegionChunk>> regionChunkFuture = databaseManager
                .getRegionChunkService()
                .getAllAsync();
        CompletableFuture<List<Settlement>> settlementFuture = databaseManager
                .getSettlementService()
                .getAllAsync();
        CompletableFuture<List<SettlementChunk>> settlementChunkFuture = databaseManager
                .getSettlementChunkService()
                .getAllAsync();
        CompletableFuture<List<Citizen>> citizenFuture = databaseManager
                .getCitizenService()
                .getAllAsync();

        try {
            CompletableFuture
                    .allOf(countryFuture, settlementFuture, settlementChunkFuture, regionFuture, regionChunkFuture,
                            citizenFuture)
                    .thenRun(() -> {

                        buildCountries(countryFuture.join());
                        buildRegions(regionFuture.join(), regionChunkFuture.join());
                        buildSettlements(settlementFuture.join(), settlementChunkFuture.join());
                        buildCitizens(citizenFuture.join());

                        Pl3xMapRenderer.instance().renderRegions(getRegions());
                        Pl3xMapRenderer.instance().renderCountries(getCountries());
                        Pl3xMapRenderer.instance().renderSettlements(getSettlements());

                    }).get();

        } catch (Exception ex) {
            Logger.logError("Initialization failed: " + ex.getMessage(), "UnitedLands");
            throw new RuntimeException("App init failed", ex);
        }

    }

    public void buildCitizens(List<Citizen> loadedCitizens) {
        for (Citizen citizen : loadedCitizens) {
            citizens.put(citizen.getUuid(), citizen);
        }
        Logger.log("Loaded " + loadedCitizens.size() + " citizens to memory.", "UnitedLands");
    }

    private void buildSettlements(List<Settlement> loadedSettlements, List<SettlementChunk> loadedSettlementChunks) {
        for (var settlement : loadedSettlements) {
            settlements.put(settlement.getUuid(), settlement);

            if (settlement.hasRegion()) {
                settlement.getRegion().addSettlement(settlement);
            }
            if (settlement.hasCountry()) {
                settlement.getCountry().addSettlement(settlement);
            }
        }
        Logger.log("Loaded " + loadedSettlements.size() + " settlements to memory.", "UnitedLands");

        for (var settlementChunk : loadedSettlementChunks) {
            settlementChunks.put(settlementChunk.getCoordinates(), settlementChunk);
            settlements.get(settlementChunk.getSettlementUuid()).addChunk(settlementChunk);
        }
        Logger.log("Loaded " + loadedSettlementChunks.size() + " settlement chunks to memory.", "UnitedLands");
    }

    public void buildRegions(List<Region> loadedRegions, List<RegionChunk> loadedRegionChunks) {
        for (var region : loadedRegions) {
            regions.put(region.getUuid(), region);

            if (region.hasCountry()) {
                region.getCountry().addRegion(region);
            }
        }
        Logger.log("Loaded " + regions.size() + " regions to memory.", "UnitedLands");

        for (var regionChunk : loadedRegionChunks) {
            regionChunks.put(regionChunk.getCoordinates(), regionChunk);
            regions.get(regionChunk.getRegionUuid()).addChunk(regionChunk);
        }
        Logger.log("Loaded " + loadedRegionChunks.size() + " region chunks to memory.", "UnitedLands");
    }

    public void buildCountries(List<Country> loadedCountries) {
        for (var country : loadedCountries) {
            countries.put(country.getUuid(), country);
        }
        Logger.log("Loaded " + countries.size() + " countries to memory.", "UnitedLands");
    }

    public void clearData() {
        settlements = new HashMap<>();
        settlementChunks = new HashMap<>();
        regions = new HashMap<>();
        regionChunks = new HashMap<>();
        countries = new HashMap<>();
    }

    // **************************************************
    // Citizens
    // **************************************************

    // Database operations

    public void createCitizenDbData(Citizen citizen) {
        databaseManager.getCitizenService().createAsync(citizen);
        registerCitizen(citizen);
    }

    public void updateCitizenDbData(Citizen citizen) {
        databaseManager.getCitizenService().updateAsync(citizen);
    }

    public void removeCitizenDbData(Citizen citizen) {
        databaseManager.getCitizenService().deleteAsync(citizen);
        unregisterCitizen(citizen);
    }

    // Cache operations

    public Citizen getCitizen(Player player) {
        return citizens.get(player.getUniqueId());
    }

    public Citizen getCitizen(UUID uuid) {
        return citizens.get(uuid);
    }

    public Citizen getCitizen(String name) {
        CompletableFuture<Citizen> future = CompletableFuture.supplyAsync(() -> {
            return citizens.values().stream().filter(c -> name.equalsIgnoreCase(c.getName())).findFirst().orElse(null);
        });
        return future.join();
    }

    public List<String> getCitizenNames() {
        CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
            return citizens.values().stream().map(Citizen::getName).collect(Collectors.toList());
        });
        return future.join();
    }

    public void registerCitizen(Citizen citizen) {
        citizens.put(citizen.getUuid(), citizen);
    }

    public void unregisterCitizen(Citizen citizen) {
        citizens.remove(citizen.getUuid(), citizen);
    }

    // **************************************************
    // Settlements
    // **************************************************

    // Database operations

    public void createSettlementDbData(Settlement settlement) {
        databaseManager.getSettlementChunkService().createAllAsync(settlement.getChunks());
        databaseManager.getSettlementService().createAsync(settlement);
        registerSettlement(settlement);
    }

    public void updateSettlementDbData(Settlement settlement) {
        databaseManager.getSettlementService().updateAsync(settlement);
    }

    public void removeSettlementDbData(Settlement settlement) {
        databaseManager.getSettlementChunkService().deleteAllAsync(settlement.getChunks());
        databaseManager.getSettlementService().deleteAsync(settlement);
        unregisterSettlement(settlement);
    }

    // Cache operations

    public void registerSettlement(Settlement settlement) {
        for (var chunk : settlement.getChunks())
            registerSettlementChunk(chunk);
        settlements.put(settlement.getUuid(), settlement);
    }

    public void unregisterSettlement(Settlement settlement) {
        for (var chunk : settlement.getChunks())
            unregisterSettlementChunk(chunk);
        settlements.remove(settlement.getUuid());
    }

    public Collection<Settlement> getSettlements() {
        return settlements.values();
    }

    public Settlement getSettlement(Location location) {
        var settlementChunk = settlementChunks.get(CoordinateUtils.locationToChunkCoordinates(location));
        if (settlementChunk != null)
            return settlementChunk.getSettlement();

        return null;
    }

    public Settlement getSettlement(Coordinates settlementCoordinates) {
        var settlementChunk = settlementChunks.get(settlementCoordinates);
        if (settlementChunk != null)
            return settlementChunk.getSettlement();

        return null;
    }

    public Settlement getSettlement(String name) {
        return settlements.values().stream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
    }

    public Settlement getSettlement(UUID settlementId) {
        return settlements.get(settlementId);
    }

    public List<String> getSettlementNames() {
        CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
            return settlements.values().stream().map(Settlement::getName).collect(Collectors.toList());
        });
        return future.join();
    }

    // **************************************************
    // Settlement Chunks
    // **************************************************

    // Database operations

    public void createSettlementChunkDbData(SettlementChunk settlementChunk) {
        databaseManager.getSettlementChunkService().createAsync(settlementChunk);
        registerSettlementChunk(settlementChunk);
    }

    public void updateSettlementChunkDbData(SettlementChunk settlementChunk) {
        databaseManager.getSettlementChunkService().updateAsync(settlementChunk);
    }

    public void removeSettlementChunkDbData(SettlementChunk settlementChunk) {
        databaseManager.getSettlementChunkService().deleteAsync(settlementChunk);
        unregisterSettlementChunk(settlementChunk);
    }

    // Cache operations

    public void registerSettlementChunk(SettlementChunk settlementChunk) {
        settlementChunks.put(settlementChunk.getCoordinates(), settlementChunk);
    }

    public void unregisterSettlementChunk(SettlementChunk settlementChunk) {
        settlementChunks.remove(settlementChunk.getCoordinates());
    }

    public SettlementChunk getSettlementChunk(Coordinates regionCoordinates) {
        return settlementChunks.get(regionCoordinates);
    }

    // **************************************************
    // Regions
    // **************************************************

    // Database operations

    public void createRegionDbData(Region region) {
        for (var chunk : region.getChunks())
            databaseManager.getRegionChunkService().createAsync(chunk);
        databaseManager.getRegionService().createAsync(region);
        registerRegion(region);
    }

    public void updateRegionDbData(Region region) {
        databaseManager.getRegionService().updateAsync(region);
    }

    public void removeRegionDbData(Region region) {
        for (var chunk : region.getChunks())
            databaseManager.getRegionChunkService().deleteAsync(chunk);
        databaseManager.getRegionService().deleteAsync(region);
        unregisterRegion(region);
    }

    // Cache operations

    public void registerRegion(Region region) {
        for (var chunk : region.getChunks())
            registerRegionChunk(chunk);
        regions.put(region.getUuid(), region);
    }

    public void unregisterRegion(Region region) {
        for (var chunk : region.getChunks())
            unregisterRegionChunk(chunk);
        regions.remove(region.getUuid());
    }

    public Collection<Region> getRegions() {
        return regions.values();
    }

    public Region getRegion(Coordinates regionCoordinates) {
        var regionChunk = regionChunks.get(regionCoordinates);
        if (regionChunk != null)
            return regionChunk.getRegion();

        return null;
    }

    public Region getRegion(String name) {
        return regions.values().stream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
    }

    public Region getRegion(UUID regionId) {
        return regions.get(regionId);
    }

    public List<String> getRegionNames() {
        CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
            return regions.values().stream().map(Region::getName).collect(Collectors.toList());
        });
        return future.join();
    }

    // **************************************************
    // Region Chunks
    // **************************************************

    // Database operations

    public void createRegionChunkDbData(RegionChunk regionChunk) {
        databaseManager.getRegionChunkService().createAsync(regionChunk);
        registerRegionChunk(regionChunk);
    }

    public void updateRegionChunkDbData(RegionChunk regionChunk) {
        databaseManager.getRegionChunkService().updateAsync(regionChunk);
    }

    public void removeRegionChunkDbData(RegionChunk regionChunk) {
        databaseManager.getRegionChunkService().deleteAsync(regionChunk);
        unregisterRegionChunk(regionChunk);
    }

    // Cache operations

    public RegionChunk getRegionChunk(Coordinates regionCoordinates) {
        return regionChunks.get(regionCoordinates);
    }

    public void registerRegionChunk(RegionChunk regionChunk) {
        regionChunks.put(regionChunk.getCoordinates(), regionChunk);
    }

    public void unregisterRegionChunk(RegionChunk regionChunk) {
        regionChunks.remove(regionChunk.getCoordinates());
    }

    // **************************************************
    // Countries
    // **************************************************

    // Database operations

    public void createCountryDbData(Country country) {
        databaseManager.getCountryService().createAsync(country);
        registerCountry(country);
    }

    public void updateCountryDbData(Country country) {
        databaseManager.getCountryService().updateAsync(country);
    }

    public void removeCountryDbData(Country country) {
        databaseManager.getCountryService().deleteAsync(country);
        unregisterCountry(country);
    }

    // Cache operations

    public void registerCountry(Country country) {
        countries.put(country.getUuid(), country);
    }

    public void unregisterCountry(Country country) {
        countries.remove(country.getUuid());
    }

    public Collection<Country> getCountries() {
        return countries.values();
    }

    public Country getCountry(String name) {
        return countries.values().stream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
    }

    public Country getCountry(UUID countryId) {
        return countries.get(countryId);
    }

    public List<String> getCountryNames() {
        CompletableFuture<List<String>> future = CompletableFuture.supplyAsync(() -> {
            return countries.values().stream().map(Country::getName).collect(Collectors.toList());
        });
        return future.join();
    }

    public Set<Citizen> getCountryCitizens(Country country) {
        CompletableFuture<Set<Citizen>> future = CompletableFuture.supplyAsync(() -> settlements.values().stream()
                .filter(s -> country.equals(s.getCountry()))
                .flatMap(s -> s.getCitizens().stream())
                .collect(Collectors.toSet()));
        return future.join();
    }

}

package org.unitedlands.unitedlands.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Coordinates;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.RegionChunk;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.utils.Logger;

public class GlobalDataManager {

    private static GlobalDataManager instance;

    public static GlobalDataManager instance() {
        return instance;
    }

    private final DatabaseManager databaseManager;
    private final Pl3xMapRenderer mapRenderer;

    private Map<UUID, Citizen> citizens = new HashMap<>();
    private Map<UUID, Settlement> settlements = new HashMap<>();
    private Map<Coordinates, SettlementChunk> settlementChunks = new HashMap<>();
    private Map<UUID, Region> regions = new HashMap<>();
    private Map<Coordinates, RegionChunk> regionChunks = new HashMap<>();
    private Map<UUID, Country> countries = new HashMap<>();

    public GlobalDataManager(DatabaseManager databaseManager, Pl3xMapRenderer mapRenderer) {
        instance = this;
        this.databaseManager = databaseManager;
        this.mapRenderer = mapRenderer;
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

                        mapRenderer.renderSettlements(getSettlements());
                        mapRenderer.renderRegions(getRegions());
                        mapRenderer.renderCountries(getCountries());

                    }).get();
        } catch (Exception ex) {
            Logger.logError("Initialization failed: " + ex.getMessage(), "UnitedRegions");
            throw new RuntimeException("App init failed", ex);
        }

    }

    public void buildCitizens(List<Citizen> loadedCitizens) {
        for (Citizen citizen : loadedCitizens) {
            citizens.put(citizen.getUuid(), citizen);
        }
        Logger.log("Loaded " + loadedCitizens.size() + " citizens to memory.", "UnitedRegions");
    }

    private void buildSettlements(List<Settlement> loadedSettlements, List<SettlementChunk> loadedSettlementChunks) {
        for (var settlement : loadedSettlements) {
            settlements.put(settlement.getUuid(), settlement);

            if (settlement.hasRegion()) {
                settlement.getRegion().addSettlement(settlement);
            }
        }
        Logger.log("Loaded " + loadedSettlements.size() + " settlements to memory.", "UnitedRegions");

        for (var settlementChunk : loadedSettlementChunks) {
            settlementChunks.put(settlementChunk.getCoordinates(), settlementChunk);
            settlements.get(settlementChunk.getSettlementUuid()).addChunk(settlementChunk);
        }
        Logger.log("Loaded " + loadedSettlementChunks.size() + " settlement chunks chunks to memory.", "UnitedRegions");
    }

    public void buildRegions(List<Region> loadedRegions, List<RegionChunk> loadedRegionChunks) {
        for (var region : loadedRegions) {
            regions.put(region.getUuid(), region);

            if (region.hasCountry()) {
                region.getCountry().addRegion(region);
            }
        }
        Logger.log("Loaded " + regions.size() + " regions to memory.", "UnitedRegions");

        for (var regionChunk : loadedRegionChunks) {
            regionChunks.put(regionChunk.getCoordinates(), regionChunk);
            regions.get(regionChunk.getRegionUuid()).addChunk(regionChunk);
        }
        Logger.log("Loaded " + loadedRegionChunks.size() + " regions chunks to memory.", "UnitedRegions");
    }

    public void buildCountries(List<Country> loadedCountries) {
        for (var country : loadedCountries) {
            countries.put(country.getUuid(), country);
        }
        Logger.log("Loaded " + countries.size() + " countries to memory.", "UnitedRegions");
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
        DatabaseManager.instance().getCitizenService().createAsync(citizen);
        registerCitizen(citizen);
    }

    public void updateCitizenDbData(Citizen citizen) {
        DatabaseManager.instance().getCitizenService().updateAsync(citizen);
    }

    public void removeCitizenDbData(Citizen citizen) {
        DatabaseManager.instance().getCitizenService().deleteAsync(citizen);
        unregisterCitizen(citizen);
    }

    // Cache operations

    public Citizen getCitizen(Player player) {
        return citizens.get(player.getUniqueId());
    }

    public Citizen getCitizen(UUID uuid) {
        return citizens.get(uuid);
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
        DatabaseManager.instance().getSettlementChunkService().createAllAsync(settlement.getChunks());
        DatabaseManager.instance().getSettlementService().createAsync(settlement);
        registerSettlement(settlement);
    }

    public void updateSettlementDbData(Settlement settlement) {
        DatabaseManager.instance().getSettlementService().updateAsync(settlement);
    }

    public void removeSettlementDbData(Settlement settlement) {
        DatabaseManager.instance().getSettlementChunkService().deleteAllAsync(settlement.getChunks());
        DatabaseManager.instance().getSettlementService().deleteAsync(settlement);
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
        DatabaseManager.instance().getSettlementChunkService().createAsync(settlementChunk);
        registerSettlementChunk(settlementChunk);
    }

    public void updateSettlementChunkDbData(SettlementChunk settlementChunk) {
        DatabaseManager.instance().getSettlementChunkService().updateAsync(settlementChunk);
    }

    public void removeSettlementChunkDbData(SettlementChunk settlementChunk) {
        DatabaseManager.instance().getSettlementChunkService().deleteAsync(settlementChunk);
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
        DatabaseManager.instance().getRegionChunkService().createAllAsync(region.getChunks());
        DatabaseManager.instance().getRegionService().createAsync(region);
        registerRegion(region);
    }

    public void updateRegionDbData(Region region) {
        DatabaseManager.instance().getRegionService().updateAsync(region);
    }

    public void removeRegionDbData(Region region) {
        DatabaseManager.instance().getRegionChunkService().deleteAllAsync(region.getChunks());
        DatabaseManager.instance().getRegionService().deleteAsync(region);
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
        DatabaseManager.instance().getRegionChunkService().createAsync(regionChunk);
        registerRegionChunk(regionChunk);
    }

    public void updateRegionChunkDbData(RegionChunk regionChunk) {
        DatabaseManager.instance().getRegionChunkService().updateAsync(regionChunk);
    }

    public void removeRegionChunkDbData(RegionChunk regionChunk) {
        DatabaseManager.instance().getRegionChunkService().deleteAsync(regionChunk);
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
        DatabaseManager.instance().getCountryService().createAsync(country);
        registeCountry(country);
    }

    public void updateCountryDbData(Country country) {
        DatabaseManager.instance().getCountryService().updateAsync(country);
    }

    public void removeCountryDbData(Country country) {
        DatabaseManager.instance().getCountryService().deleteAsync(country);
        unregisterCountry(country);
    }

    // Cache operations

    public void registeCountry(Country country) {
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

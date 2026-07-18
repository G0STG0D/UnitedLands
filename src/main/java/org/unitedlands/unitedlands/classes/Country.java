package org.unitedlands.unitedlands.classes;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.ColorUtils;
import org.unitedlands.unitedlands.utils.CostUtils;
import org.unitedlands.unitedlands.utils.SerializationUtils;
import org.unitedlands.utils.Logger;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public class Country extends GeopolObject {

    @DatabaseField(canBeNull = true, columnName = "stroke_color")
    private @Nullable Integer strokeColor;
    @DatabaseField(canBeNull = true, columnName = "fill_color")
    private @Nullable Integer fillColor;

    @DatabaseField(canBeNull = true, columnName = "bonus_regions")
    private int bonusRegions = 0;

    @DatabaseField(canBeNull = true, columnName = "spawn_serialized")
    private String spawnSerialized;

    @DatabaseField(canBeNull = false, width = 36, columnName = "capital_uuid")
    private UUID capitalUuid;

    @DatabaseField(dataType = DataType.LONG_STRING, columnName = "settlement_claim_whitelist")
    private String settlementClaimWhitelistSerialized;

    @DatabaseField(dataType = DataType.LONG_STRING, columnName = "allies_serialized")
    private String alliesSerialized;

    private transient Location spawn;
    private transient Settlement capital;
    private transient Set<Region> regions = new HashSet<>();
    private transient Set<Settlement> settlements = new HashSet<>();
    private transient Set<Settlement> settlementClaimWhitelist = new HashSet<>();
    private transient Set<Country> allies = new HashSet<>();

    public @Nullable Integer getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String hexColor) {
        this.strokeColor = ColorUtils.hexToColor(hexColor).getRGB();
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor.getRGB();
    }

    public @Nullable Integer getFillColor() {
        return fillColor;
    }

    public void setFillColor(String hexColor) {
        this.fillColor = ColorUtils.hexToColor(hexColor).getRGB();
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor.getRGB();
    }

    public int getBonusRegions() {
        return bonusRegions;
    }

    public void setBonusRegions(int bonusRegions) {
        this.bonusRegions = bonusRegions;
    }

    public String getSpawnSerialized() {
        return spawnSerialized;
    }

    public void setSpawnSerialized(String spawnSerialized) {
        this.spawnSerialized = spawnSerialized;
    }

    public void setSpawn(Location location) {
        this.spawn = location;
        this.spawnSerialized = SerializationUtils.serializeLocation(location);
    }

    public Location getSpawn() {
        if (spawn == null && spawnSerialized != null)
            this.spawn = SerializationUtils.deserializeLocation(spawnSerialized);
        return spawn;
    }

    public boolean hasCapital() {
        return capitalUuid != null;
    }

    public Settlement getCapital() {
        if (capital == null && capitalUuid != null) {
            capital = UnitedLandsDataManager.instance().getSettlement(capitalUuid);
        }
        return capital;
    }

    public void setCapital(Settlement settlement) {
        if (settlement == null) {
            throw new IllegalArgumentException("Capital cannot be null");
        }
        this.capital = settlement;
        this.capitalUuid = settlement.getUuid();
    }

    public Set<Region> getRegions() {
        return this.regions;
    }

    public void addRegion(Region region) {
        regions.add(region);
    }

    public void removeRegion(Region region) {
        regions.remove(region);
    }

    public Set<Settlement> getSettlements() {
        return this.settlements;
    }

    public void addSettlement(Settlement settlement) {
        settlements.add(settlement);
    }

    public void removeSettlement(Settlement settlement) {
        settlements.remove(settlement);
    }

    public Set<Citizen> getCitizens() {
        CompletableFuture<Set<Citizen>> future = CompletableFuture.supplyAsync(() -> {
            return settlements.stream().map(Settlement::getCitizens).flatMap(Set::stream).collect(Collectors.toSet());
        });
        return future.join();
    }

    public Citizen getLeader() {
        CompletableFuture<Citizen> future = CompletableFuture.supplyAsync(() -> {
            var citizens = settlements.stream().map(Settlement::getCitizens).flatMap(Set::stream)
                    .collect(Collectors.toSet());
            return citizens.stream().filter(c -> c.hasCountryRank("leader")).findFirst().orElse(null);
        });
        return future.join();
    }

    public int getRegionCount() {
        return getRegions().size();
    }

    public int getSettlementCount() {
        return getSettlements().size();
    }

    public int getCitizenCount() {
        return getCitizens().size();
    }

    public Set<Settlement> getSettlementClaimWhitelist() {
        if (settlementClaimWhitelist == null) {
            if (settlementClaimWhitelistSerialized != null) {
                try {
                    settlementClaimWhitelist = Arrays.stream(settlementClaimWhitelistSerialized.split("#"))
                            .map(c -> UnitedLandsDataManager.instance().getSettlement(UUID.fromString(c)))
                            .collect(Collectors.toSet());
                } catch (Exception ex) {
                    Logger.logError(
                            "Unable to parse settlement_claim_whitelist of " + getName() + ": " + ex.getMessage());
                    settlementClaimWhitelist = new HashSet<>();
                }
            } else {
                settlementClaimWhitelist = new HashSet<>();
            }
        }
        return settlementClaimWhitelist;
    }

    public void setSettlementClaimWhitelist(Set<Settlement> settlementClaimWhitelist) {
        this.settlementClaimWhitelist = settlementClaimWhitelist;
        if (settlementClaimWhitelist != null && !settlementClaimWhitelist.isEmpty()) {
            try {
                this.settlementClaimWhitelistSerialized = settlementClaimWhitelist.stream()
                        .map(c -> c.getUuid().toString())
                        .collect(Collectors.joining("#"));
            } catch (Exception ex) {
                Logger.logError("Unable to parse settlement_claim_whitelist for " + getName() + ": " + ex.getMessage());
                this.settlementClaimWhitelistSerialized = null;
            }
        } else {
            this.settlementClaimWhitelistSerialized = null;
        }
    }

    public Set<Country> getAllies() {
        if (allies == null) {
            if (alliesSerialized != null) {
                try {
                    allies = Arrays.stream(alliesSerialized.split("#"))
                            .map(c -> UnitedLandsDataManager.instance().getCountry(UUID.fromString(c)))
                            .collect(Collectors.toSet());
                } catch (Exception ex) {
                    Logger.logError("Unable to parse allies of " + getName() + ": " + ex.getMessage());
                    allies = new HashSet<>();
                }
            } else {
                allies = new HashSet<>();
            }
        }
        return allies;
    }

    public void setAllies(Set<Country> allies) {
        this.allies = allies;
        if (allies != null && !allies.isEmpty()) {
            try {
                this.alliesSerialized = allies.stream()
                        .map(c -> c.getUuid().toString())
                        .collect(Collectors.joining("#"));
            } catch (Exception ex) {
                Logger.logError("Unable to parse allies for " + getName() + ": " + ex.getMessage());
                this.alliesSerialized = null;
            }
        } else {
            this.alliesSerialized = null;
        }
    }

    // TODO: Neutrality
    public boolean isNeutral() {
        return false;
    }

    public BigDecimal getBalance() {
        return UnitedLandsEconomyManager.instance().getBalance(uuid);
    }

    public double getUpkeep() {
        return CostUtils.getCountryUpkeep(this);
    }

    @Override
    public void saveMetadata() {
        UnitedLandsDataManager.instance().updateCountryDbData(this);
    }

}

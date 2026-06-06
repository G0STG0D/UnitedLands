package org.unitedlands.unitedlands.classes;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.ColorUtils;
import org.unitedlands.unitedlands.utils.SerializationUtils;

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

    private transient Location spawn;
    private transient Settlement capital;
    private transient Set<Region> regions = new HashSet<>();

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
            capital = GlobalDataManager.instance().getSettlement(capitalUuid);
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

}

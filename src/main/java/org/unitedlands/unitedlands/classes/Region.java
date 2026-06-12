package org.unitedlands.unitedlands.classes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.SerializationUtils;

import com.j256.ormlite.field.DatabaseField;

public class Region extends GeopolObject implements PermissionHolder {

    @DatabaseField(canBeNull = true, columnName = "home_chunk_x")
    private int homeChunkCoordinatesX;
    @DatabaseField(canBeNull = true, columnName = "home_chunk_z")
    private int homeChunkCoordinatesZ;
    @DatabaseField(canBeNull = true, columnName = "spawn_serialized")
    private String spawnSerialized;

    @DatabaseField(width = 36, columnName = "country_uuid")
    private UUID countryUuid;

    @DatabaseField(canBeNull = false, columnName = "break_permissions")
    private int breakPermissions = LocationMembership.OWNER | LocationMembership.TRUSTED;
    @DatabaseField(canBeNull = false, columnName = "place_permissions")
    private int placePermissions = LocationMembership.OWNER | LocationMembership.TRUSTED;
    @DatabaseField(canBeNull = false, columnName = "container_permissions")
    private int containerPermissions = LocationMembership.OWNER | LocationMembership.TRUSTED;
    @DatabaseField(canBeNull = false, columnName = "switch_permissions")
    private int switchPermissions = LocationMembership.OWNER | LocationMembership.TRUSTED;
    @DatabaseField(canBeNull = false, columnName = "block_use_permissions")
    private int blockUsePermissions = LocationMembership.OWNER | LocationMembership.TRUSTED;
    @DatabaseField(canBeNull = false, columnName = "interact_permissions")
    private int interactPermissions = LocationMembership.OWNER | LocationMembership.TRUSTED;

    @DatabaseField(columnName = "allow_pvp", canBeNull = false)
    private boolean allowPvp = true;
    @DatabaseField(columnName = "allow_monsters", canBeNull = false)
    private boolean allowMonsters = true;
    @DatabaseField(columnName = "allow_animals", canBeNull = false)
    private boolean allowAnimals = true;
    @DatabaseField(columnName = "allow_fire", canBeNull = false)
    private boolean allowFire = true;
    @DatabaseField(columnName = "allow_explosions", canBeNull = false)
    private boolean allowExplosions = true;

    private transient Coordinates homeChunkCoordinates;
    private transient Location spawn;
    private transient Country country;

    private transient Set<Settlement> settlements = new HashSet<>();
    private transient Set<RegionChunk> chunks = new HashSet<>();

    public Region() {

    }

    public int getHomeChunkCoordinatesX() {
        return homeChunkCoordinatesX;
    }

    public void setHomeChunkCoordinatesX(int homeChunkCoordinatedX) {
        this.homeChunkCoordinatesX = homeChunkCoordinatedX;
    }

    public int getHomeChunkCoordinatesZ() {
        return homeChunkCoordinatesZ;
    }

    public void setHomeChunkCoordinatesZ(int homeChunkCoordinatesZ) {
        this.homeChunkCoordinatesZ = homeChunkCoordinatesZ;
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

    public void setHomeChunkCoordinates(Coordinates coordinates) {
        this.homeChunkCoordinates = coordinates;
        this.homeChunkCoordinatesX = coordinates.getX();
        this.homeChunkCoordinatesZ = coordinates.getZ();
    }

    public Coordinates getHomeChunkCoordinates() {
        if (this.homeChunkCoordinates == null)
            homeChunkCoordinates = new Coordinates(this.homeChunkCoordinatesX, this.homeChunkCoordinatesZ,
                    this.worldName);
        return homeChunkCoordinates;
    }

    public void setCountry(Country country) {
        this.country = country;
        this.countryUuid = country.getUuid();
    }

    public Country getCountry() {
        if (this.country == null && this.countryUuid != null)
            country = GlobalDataManager.instance().getCountry(countryUuid);
        return country;
    }

    public void removeCountry() {
        this.country = null;
        this.countryUuid = null;
    }

    public boolean hasCountry() {
        return getCountry() != null;
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

    public Set<RegionChunk> getChunks() {
        return chunks;
    }

    public void setChunks(Set<RegionChunk> chunks) {
        this.chunks = chunks;
    }

    public void addChunk(RegionChunk chunk) {
        this.chunks.add(chunk);
    }

    public void removeChunk(RegionChunk chunk) {
        this.chunks.remove(chunk);
    }

    public boolean hasChunkAtCoordinates(Coordinates coords) {
        return chunks.stream().anyMatch(c -> c.getCoordinates().equals(coords));
    }

    public RegionChunk getChunkAtCoordinates(Coordinates coords) {
        return chunks.stream().filter(c -> c.getCoordinates().equals(coords)).findFirst().orElse(null);
    }

    @Override
    public int getBreakPermissions() {
        return breakPermissions;
    }

    @Override
    public int getPlacePermissions() {
        return placePermissions;
    }

    @Override
    public int getContainerPermissions() {
        return containerPermissions;
    }

    @Override
    public int getSwitchPermissions() {
        return switchPermissions;
    }

    @Override
    public int getBlockUsePermissions() {
        return blockUsePermissions;
    }

    @Override
    public int getInteractPermissions() {
        return interactPermissions;
    }

    public boolean allowPvp() {
        return allowPvp;
    }

    public void setAllowPvp(boolean allowPvp) {
        this.allowPvp = allowPvp;
    }

    public boolean allowMonsters() {
        return allowMonsters;
    }

    public void setAllowMonsters(boolean allowMonsters) {
        this.allowMonsters = allowMonsters;
    }

    public boolean allowAnimals() {
        return allowAnimals;
    }

    public void setAllowAnimals(boolean allowAnimals) {
        this.allowAnimals = allowAnimals;
    }

    public boolean allowFire() {
        return allowFire;
    }

    public void setAllowFire(boolean allowFire) {
        this.allowFire = allowFire;
    }

    public boolean allowExplosions() {
        return allowExplosions;
    }

    public void setAllowExplosions(boolean allowExplosions) {
        this.allowExplosions = allowExplosions;
    }

    public Integer getFillColor() {
        if (hasCountry())
            return country.getFillColor();
        return Settings.defaultRegionFillColour;
    }

    public Integer getStrokeColor() {
        if (hasCountry())
            return country.getStrokeColor();
        return Settings.defaultRegionStrokeColour;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Region other = (Region) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

}

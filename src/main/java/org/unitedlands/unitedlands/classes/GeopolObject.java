package org.unitedlands.unitedlands.classes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.unitedlands.unitedlands.classes.db.Identifiable;
import org.unitedlands.unitedlands.classes.interfaces.MetadataHolder;
import org.unitedlands.unitedlands.classes.metadata.MetaDataField;
import org.unitedlands.unitedlands.utils.JsonUtils;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public class GeopolObject implements Identifiable, MetadataHolder {

    @DatabaseField(id = true, width = 36, canBeNull = false)
    protected UUID uuid;
    @DatabaseField(width = 255)
    protected String name;
    @DatabaseField(width = 255, columnName = "world_name")
    protected String worldName;

    @DatabaseField(width = 36, columnName = "founder_uuid")
    protected UUID founderUuid;
    @DatabaseField(width = 128, columnName = "founder_name")
    protected String founderName;
    @DatabaseField(columnName = "founding_timestamp")
    protected long foundingTimestamp;

    @DatabaseField(canBeNull = true, dataType = DataType.LONG_STRING, columnName = "metadata_serialized", columnDefinition = "MEDIUMTEXT")
    private String metadataSerialized;

    protected transient World world;
    protected transient Map<String, MetaDataField<?>> metadata;

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public String getCleanName() {
        return name.replace("_", " ");
    }

    public void setName(String name) {
        this.name = name;
    }

    public World getWorld() {
        if (world == null && worldName != null)
            world = Bukkit.getWorld(worldName);
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
        this.worldName = world.getName();
    }

    public String getWorldName() {
        return worldName;
    }

    public void setFounder(OfflinePlayer player) {
        this.founderUuid = player.getUniqueId();
        this.founderName = player.getName();
    }

    public OfflinePlayer getFounder() {
        return Bukkit.getOfflinePlayer(founderUuid);
    }

    public UUID getFounderUuid() {
        return this.founderUuid;
    }

    public String getFounderName() {
        return this.founderName;
    }

    public boolean hasFounder() {
        return founderUuid != null;
    }

    public long getFoundingTimestamp() {
        return foundingTimestamp;
    }

    public void setFoundingTimestamp(long foundingTimestamp) {
        this.foundingTimestamp = foundingTimestamp;
    }

    @Override
    public Map<String, MetaDataField<?>> getMetadata() {
        if (metadata == null && metadataSerialized != null && !metadataSerialized.isEmpty()) {
            var t = new TypeToken<Collection<MetaDataField<?>>>() {
            };
            Collection<MetaDataField<?>> parsedData = JsonUtils.deserialize(metadataSerialized, t);
            metadata = new HashMap<>();
            for (var m : parsedData)
                metadata.put(m.getKey(), m);
        }
        return metadata;
    }

    @Override
    public boolean hasMetadata(String key) {
        return getMetadata().containsKey(key);
    }

    @Override
    public MetaDataField<?> getMetadata(String key) {
        if (getMetadata() == null)
            return null;
        return getMetadata().get(key);
    }

    @Override
    public void addMetadata(MetaDataField<?> data) {
        if (getMetadata() == null)
            metadata = new HashMap<>();
        metadata.put(data.getKey(), data);
        metadataSerialized = JsonUtils.serialize(metadata.values());
    }

    @Override
    public void removeMetadata(String key) {
        if (getMetadata() == null)
            return;
        metadata.remove(key);
        metadataSerialized = JsonUtils.serialize(metadata.values());
    }

    public void saveMetadata() {
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
        GeopolObject other = (GeopolObject) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.uuid))
            return false;
        return true;
    }

}

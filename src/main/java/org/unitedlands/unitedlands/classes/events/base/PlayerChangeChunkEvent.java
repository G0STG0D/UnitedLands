package org.unitedlands.unitedlands.classes.events.base;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerChangeChunkEvent extends PlayerEvent {

    private Chunk fromChunk;
    private Location fromLocation;
    private Chunk toChunk;
    private Location toLocation;

    public PlayerChangeChunkEvent(Player player, Location fromLocation, Location toLocation) {
        super(player);
        this.fromLocation = fromLocation;
        this.fromChunk = fromLocation.getChunk();
        this.toLocation = toLocation;
        this.toChunk = toLocation.getChunk();
    }

    public Chunk getFromChunk() {
        return fromChunk;
    }

    public Location getFromLocation() {
        return fromLocation;
    }

    public Chunk getToChunk() {
        return toChunk;
    }

    public Location getToLocation() {
        return toLocation;
    }

}

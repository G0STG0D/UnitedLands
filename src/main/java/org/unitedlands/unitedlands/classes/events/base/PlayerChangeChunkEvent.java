package org.unitedlands.unitedlands.classes.events.base;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.Coordinates;
import org.unitedlands.unitedlands.utils.CoordinateUtils;

public class PlayerChangeChunkEvent extends PlayerEvent {

    private Chunk fromChunk;
    private Location fromLocation;
    private Coordinates fromCoordinates;

    private Chunk toChunk;
    private Location toLocation;
    private Coordinates toCoordinates;

    public PlayerChangeChunkEvent(Player player, Location fromLocation, Location toLocation) {
        super(player);
        this.fromLocation = fromLocation;
        this.fromChunk = fromLocation.getChunk();
        this.fromCoordinates = CoordinateUtils.locationToChunkCoordinates(fromLocation);
        this.toLocation = toLocation;
        this.toChunk = toLocation.getChunk();
        this.toCoordinates = CoordinateUtils.locationToChunkCoordinates(toLocation);

    }

    public PlayerChangeChunkEvent(Player player, Location fromLocation, Location toLocation, Coordinates fromCoordinateds,
            Coordinates toCoordinates) {
        super(player);
        this.fromLocation = fromLocation;
        this.fromChunk = fromLocation.getChunk();
        this.fromCoordinates = fromCoordinateds;
        this.toLocation = toLocation;
        this.toChunk = toLocation.getChunk();
        this.toCoordinates = toCoordinates;
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

    public Coordinates getFromCoordinates() {
        return fromCoordinates;
    }

    public Coordinates getToCoordinates() {
        return toCoordinates;
    }

}

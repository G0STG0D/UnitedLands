package org.unitedlands.unitedlands.classes.events.base;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.PermissionType;

public class PermissablePlayerActionEvent extends PlayerEvent {

    private final Location location;
    private final PermissionType permissionType;

    public PermissablePlayerActionEvent(Player player, Location location, PermissionType permissionType) {
        super(player);
        this.location = location;
        this.permissionType = permissionType;
    }

    public Location getLocation() {
        return location;
    }

    public PermissionType getPermissionType() {
        return permissionType;
    }


}

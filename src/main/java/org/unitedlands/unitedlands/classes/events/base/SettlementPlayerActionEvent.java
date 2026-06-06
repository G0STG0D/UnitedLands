package org.unitedlands.unitedlands.classes.events.base;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.PermissionType;
import org.unitedlands.unitedlands.classes.Settlement;

public class SettlementPlayerActionEvent extends SettlementEvent {

    private final Player player;
    private final Location location;
    private final PermissionType permissionType;

    public SettlementPlayerActionEvent(Settlement settlement, Player player, Location location, PermissionType permissionType) {
        super(settlement);
        this.player = player;
        this.location = location;
        this.permissionType = permissionType;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location;
    }

    public PermissionType getPermissionType() {
        return permissionType;
    }


}

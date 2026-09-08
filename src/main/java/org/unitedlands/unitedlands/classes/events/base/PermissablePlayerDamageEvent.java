package org.unitedlands.unitedlands.classes.events.base;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PermissablePlayerDamageEvent extends PlayerEvent {

    public static enum DamageType {
        PVP, PVE
    }

    private final Location location;
    private final DamageType damageType;

    public PermissablePlayerDamageEvent(Player player, Location location, DamageType damageType) {
        super(player);
        this.location = location;
        this.damageType = damageType;
    }

    public Location getLocation() {
        return location;
    }

    public DamageType getDamageType() {
        return damageType;
    }

}

package org.unitedlands.unitedlands.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.unitedlands.unitedlands.classes.PermissionType;
import org.unitedlands.unitedlands.classes.events.base.PermissablePlayerDamageEvent;
import org.unitedlands.unitedlands.classes.events.base.PermissablePlayerDamageEvent.DamageType;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

public class PlayerDamageListener implements Listener {

    @EventHandler
    private void onPlayerDamageByPlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() == null)
            return;

        if (!(event.getDamager() instanceof Player attacker))
            return;

        if ((event.getEntity() instanceof Player)) {
            handlePvpAttack(event, attacker);
        } else {
            if (event.getEntity() instanceof LivingEntity livingEntity) {
                handlePveAttack(event, livingEntity, attacker);
            }
        }

    }

    private void handlePvpAttack(EntityDamageByEntityEvent event, Player attacker) {
        var coords = CoordinateUtils.locationToChunkCoordinates(event.getEntity().getLocation());

        var settlementChunk = UnitedLandsDataManager.instance().getSettlementChunk(coords);
        if (settlementChunk != null) {
            if (!settlementChunk.allowPvp()) {
                cancelAttack(event, attacker, DamageType.PVP);
                return;
            }
            if (!settlementChunk.getSettlement().allowPvp()) {
                cancelAttack(event, attacker, DamageType.PVP);
                return;
            }
        }

        var region = UnitedLandsDataManager.instance().getRegion(coords);
        if (region != null && !region.allowPvp()) {
            cancelAttack(event, attacker, DamageType.PVP);
            return;
        }
    }

    private void handlePveAttack(EntityDamageByEntityEvent event, LivingEntity victim, Player attacker) {

        // Hostile mobs are never protected
        if (victim instanceof Monster)
            return;

        var coords = CoordinateUtils.locationToChunkCoordinates(event.getEntity().getLocation());
        var settlementChunk = UnitedLandsDataManager.instance().getSettlementChunk(coords);
        if (settlementChunk != null) {
            if (!PermissionManager.instance().checkLocationPermissions(attacker, victim.getLocation(),
                    PermissionType.INTERACT)) {
                cancelAttack(event, attacker, DamageType.PVE);
                return;
            }
        }

        var region = UnitedLandsDataManager.instance().getRegion(coords);
        if (region != null && !region.allowPvp()) {
            if (!PermissionManager.instance().checkLocationPermissions(attacker, victim.getLocation(),
                    PermissionType.INTERACT)) {
                cancelAttack(event, attacker, DamageType.PVE);
                return;
            }
        }
    }

    private void cancelAttack(EntityDamageByEntityEvent event, Player attacker, DamageType type) {

        // Call event. Other plugins might override the cancellation (e.g. UnitedWars in
        // war zones).
        PermissablePlayerDamageEvent damageEvent = new PermissablePlayerDamageEvent(attacker, event.getEntity().getLocation(), type);
        damageEvent.setCancelled(true);
        damageEvent.callEvent();

        if (damageEvent.isCancelled()) {
            Messenger.sendMessage(Bukkit.getServer(), MessageProvider.instance().get(Message.GENERAL_ERRORS__DAMAGE_DISABLED.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            event.setCancelled(true);
        }

    }

}

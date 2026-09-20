package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;

import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdSettlement.class,
        name = "spawn",
        description = "Lets the player teleport to a settlement",
        usage = "/settlement spawn [settlement_name]",
        playerOnly = true
)
public class CmdSettlementSpawn extends SettlementCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return UnitedLandsDataManager.instance().getSettlementNames();
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;
        Settlement targetSettlement = null;

        if (args.length == 0) {
            var citizen = getCitizen(player);
            if (citizen == null)
                return;
            var settlement = getCitizenSettlement(citizen);
            if (settlement == null)
                return;
            targetSettlement = settlement;
        } else {
            var settlement = UnitedLandsDataManager.instance().getSettlement(args[0]);
            if (settlement == null) {
                United.messenger().send(player, "general-errors.settlement-not-found", args[0]);
                return;
            }

            if (!settlement.isPublic() && !PermissionManager.instance().hasGlobalOverrides(player)) {
                United.messenger().send(sender, "player.settlement.spawn.not-public");
                return;
            }

            targetSettlement = settlement;
        }

        if (targetSettlement.getSpawn() == null) {
            United.messenger().send(sender, "player.settlement.spawn.no-spawn");
            return;
        }

        final Settlement finalSettlement = targetSettlement;

        if (PermissionManager.instance().hasGlobalOverrides(player)) {
            player.teleport(finalSettlement.getSpawn());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        } else {

            United.messenger().send(sender, "player.settlement.spawn.tp-start");

            new BukkitRunnable() {
                int counter = 0;
                int maxExecutions = 3;

                Location startBlock = player.getLocation().getBlock().getLocation();

                @Override
                public void run() {
                    counter++;

                    if (counter <= maxExecutions) {
                        United.messenger().sendRaw(sender, (maxExecutions - counter + 1) + "...");
                    }

                    if (!player.getLocation().getBlock().getLocation().equals(startBlock)) {
                        United.messenger().send(sender, "player.settlement.spawn.tp-cancel");
                        this.cancel();
                    }

                    if (counter > maxExecutions) {
                        player.teleport(finalSettlement.getSpawn());
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        this.cancel();
                    }
                }
            }.runTaskTimer(UnitedLands.instance(), 0L, 20L);
        }

    }

}

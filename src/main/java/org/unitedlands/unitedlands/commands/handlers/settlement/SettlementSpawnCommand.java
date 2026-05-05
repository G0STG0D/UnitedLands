package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.Messenger;

public class SettlementSpawnCommand extends SettlementCommandHandler {

    public SettlementSpawnCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return GlobalDataManager.instance().getSettlementNames();
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
            var settlement = GlobalDataManager.instance().getSettlement(args[0]);
            if (settlement == null) {
                Messenger.sendMessage(player, messageProvider.get("errors.settlement-not-found"),
                        Map.of("settlement", args[0]), messageProvider.get("prefix"));
                return;
            }

            if (!settlement.isPublic() && !PermissionManager.instance().hasGlobalOverrides(player)) {
                Messenger.sendMessage(sender, messageProvider.get("teleport.not-public"), null,
                        messageProvider.get("prefix"));
                return;
            }

            targetSettlement = settlement;
        }

        if (targetSettlement.getSpawn() == null) {
            Messenger.sendMessage(sender, messageProvider.get("teleport.no-spawn"), null,
                    messageProvider.get("prefix"));
            return;
        }

        Messenger.sendMessage(sender, messageProvider.get("teleport.start"), null, messageProvider.get("prefix"));

        final Settlement finalSettlement = targetSettlement;

        if (PermissionManager.instance().hasGlobalOverrides(player)) {
            player.teleport(finalSettlement.getSpawn());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        } else {
            new BukkitRunnable() {
                int counter = 0;
                int maxExecutions = 3;

                Location startBlock = player.getLocation().getBlock().getLocation();

                @Override
                public void run() {
                    counter++;

                    if (counter <= maxExecutions) {
                        Messenger.sendMessage(sender, (maxExecutions - counter + 1) + "...", null,
                                messageProvider.get("prefix"));
                    }

                    if (!player.getLocation().getBlock().getLocation().equals(startBlock)) {
                        Messenger.sendMessage(sender, messageProvider.get("teleport.cancel"), null,
                                messageProvider.get("prefix"));
                        this.cancel();
                    }

                    if (counter > maxExecutions) {
                        player.teleport(finalSettlement.getSpawn());
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L);
        }

    }

}

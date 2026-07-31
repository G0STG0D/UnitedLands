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
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.Messenger;

public class SettlementSpawnCommand extends SettlementCommandHandler {

    public SettlementSpawnCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

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
                Messenger.sendMessage(player, messageProvider.get(Message.GENERAL_ERRORS__SETTLEMENT_NOT_FOUND.path()),
                        Map.of("settlement", args[0]), messageProvider.get(Message.PREFIX.path()));
                return;
            }

            if (!settlement.isPublic() && !PermissionManager.instance().hasGlobalOverrides(player)) {
                Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__SETTLEMENT__SPAWN__NOT_PUBLIC.path()), null,
                        messageProvider.get(Message.PREFIX.path()));
                return;
            }

            targetSettlement = settlement;
        }

        if (targetSettlement.getSpawn() == null) {
            Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__SETTLEMENT__SPAWN__NO_SPAWN.path()), null,
                    messageProvider.get(Message.PREFIX.path()));
            return;
        }

        final Settlement finalSettlement = targetSettlement;

        if (PermissionManager.instance().hasGlobalOverrides(player)) {
            player.teleport(finalSettlement.getSpawn());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        } else {

            Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__SETTLEMENT__SPAWN__TP_START.path()), null,
                    messageProvider.get(Message.PREFIX.path()));

            new BukkitRunnable() {
                int counter = 0;
                int maxExecutions = 3;

                Location startBlock = player.getLocation().getBlock().getLocation();

                @Override
                public void run() {
                    counter++;

                    if (counter <= maxExecutions) {
                        Messenger.sendMessage(sender, (maxExecutions - counter + 1) + "...", null,
                                messageProvider.get(Message.PREFIX.path()));
                    }

                    if (!player.getLocation().getBlock().getLocation().equals(startBlock)) {
                        Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__SETTLEMENT__SPAWN__TP_CANCEL.path()), null,
                                messageProvider.get(Message.PREFIX.path()));
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

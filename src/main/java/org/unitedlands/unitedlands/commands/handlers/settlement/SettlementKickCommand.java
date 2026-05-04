package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementKickCommand extends SettlementCommandHandler {

    public SettlementKickCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            var player = (Player) sender;
            var citizen = getCitizen(player);
            if (citizen == null)
                return null;
            var settlement = citizen.getSettlement();
            if (settlement == null)
                return null;
            return settlement.getCitizens().stream().map(Citizen::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;
        var settlement = getCitizenSettlement(citizen);
        if (settlement == null)
            return;

        if (!hasPermission("settlement.kick", citizen))
            return;

        var targetPlayer = Bukkit.getPlayerExact(args[0]);
        if (targetPlayer == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.player-not-found"),
                    Map.of("name", args[0]), messageProvider.get("prefix"));
            return;
        }

        if (player.equals(targetPlayer)) {
            Messenger.sendMessage(player, messageProvider.get("settlement.kick.cannot-kick-yourself"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var targetCitizen = getCitizen(targetPlayer);
        if (targetCitizen == null)
            return;

        if (!settlement.getCitizens().contains(targetCitizen)) {
            Messenger.sendMessage(player, messageProvider.get("settlement.kick.not-in-settlement"),
                    null, messageProvider.get("prefix"));
            return;
        }

        settlement.removeCitizen(targetCitizen);
        targetCitizen.removeSettlementRanks();
        targetCitizen.removeSettlement();

        GlobalDataManager.instance().updateCitizenDbData(targetCitizen);

        if (targetPlayer.isOnline()) {
            Messenger.sendMessage(targetPlayer, messageProvider.get("settlement.kick.kicked"),
                    Map.of("settlement", settlement.getCleanName()), messageProvider.get("prefix"));
        }

        Messenger.sendMessage(player, messageProvider.get("settlement.kick.success"),
                Map.of("name", targetPlayer.getName()), messageProvider.get("prefix"));
    }

}

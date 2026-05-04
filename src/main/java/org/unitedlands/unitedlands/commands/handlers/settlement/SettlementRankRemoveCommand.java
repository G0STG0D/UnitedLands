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
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.Messenger;

public class SettlementRankRemoveCommand extends SettlementCommandHandler {

    public SettlementRankRemoveCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {

        var citizen = getCitizen((Player) sender);
        if (citizen == null)
            return null;
        var settlement = getCitizenSettlement(citizen);
        if (settlement == null)
            return null;

        switch (args.length) {
            case 1:
                return settlement.getCitizens().stream().map(Citizen::getName).collect(Collectors.toList());
            case 2:
                var targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer == null)
                    return null;
                var targetCitizen = GlobalDataManager.instance().getCitizen(targetPlayer);
                if (targetCitizen == null)
                    return null;
                return targetCitizen.getSettlementRanks().stream().collect(Collectors.toList());
            default:
                break;
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            // TODO: Usage
            return;
        }

        var player = (Player) sender;

        var citizen = getCitizen((Player) sender);
        if (citizen == null)
            return;
        var settlement = getCitizenSettlement(citizen);
        if (settlement == null)
            return;

        var targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.player-not-found"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var targetCitizen = getCitizen(targetPlayer);
        if (targetCitizen == null)
            return;

        if (!PermissionManager.instance().getSettlementRanks().contains(args[1])) {
            Messenger.sendMessage(player, messageProvider.get("settlement.ranks.unknown-rank"),
                    Map.of("rank", args[1]), messageProvider.get("prefix"));
            return;
        }

        if (!targetCitizen.getSettlementRanks().contains(args[1])) {
            Messenger.sendMessage(player, messageProvider.get("settlement.ranks.rank-not-owned"),
                    Map.of("rank", args[1], "name", targetCitizen.getName()),
                    messageProvider.get("prefix"));
            return;
        }

        if (args[1].equals("mayor")) {

            if (!hasPermission("settlement.manage.ranks.mayor", citizen))
                return;

            Messenger.sendMessage(player, messageProvider.get("settlement.ranks.cannot-remove-mayor"),
                    null, messageProvider.get("prefix"));
            return;

        } else {

            if (!hasPermission("settlement.manage.ranks.other", citizen))
                return;

            targetCitizen.removeSettlementRank(args[1]);
            GlobalDataManager.instance().updateCitizenDbData(targetCitizen);

            if (targetPlayer.isOnline()) {
                Messenger.sendMessage(targetPlayer, messageProvider.get("settlement.ranks.lost"),
                        Map.of("rank", args[1]), messageProvider.get("prefix"));
            }

        }

        Messenger.sendMessage(player, messageProvider.get("settlement.ranks.removed"),
                Map.of("rank", args[1], "name", targetCitizen.getName()),
                messageProvider.get("prefix"));

    }

}

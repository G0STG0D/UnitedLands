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
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.ConfirmationManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.Messenger;

public class SettlementRankAddCommand extends SettlementCommandHandler {

    public SettlementRankAddCommand(UnitedLands plugin, IMessageProvider messageProvider) {
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
                return PermissionManager.instance().getSettlementRanks();
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

        // Concrete permission checks are further down
        var context = validate(sender, null);
        if (context == null)
            return;
        
        var targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            Messenger.sendMessage(context.player(), messageProvider.get("errors.player-not-found"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var targetCitizen = getCitizen(targetPlayer);
        if (targetCitizen == null)
            return;

        if (!context.settlement().equals(targetCitizen.getSettlement())) {
            Messenger.sendMessage(context.player(), messageProvider.get("settlement.ranks.not-in-settlement"),
                    Map.of("name", args[0]), messageProvider.get("prefix"));
            return;
        }

        if (!PermissionManager.instance().getSettlementRanks().contains(args[1])) {
            Messenger.sendMessage(context.player(), messageProvider.get("settlement.ranks.unknown-rank"),
                    Map.of("rank", args[1]), messageProvider.get("prefix"));
            return;
        }

        if (targetCitizen.getSettlementRanks().contains(args[1])) {
            Messenger.sendMessage(context.player(), messageProvider.get("settlement.ranks.rank-already-owned"),
                    Map.of("rank", args[1], "name", targetCitizen.getName()),
                    messageProvider.get("prefix"));
            return;
        }

        if (args[1].equals("mayor")) {

            if (!hasPermission("settlement.manage.ranks.mayor", context.citizen()))
                return;

            var confirmation = new Confirmation("new-mayor");
            confirmation.setRunnable(() -> {

                var currentMayor = context.settlement().getMayor();
                currentMayor.removeSettlementRank("mayor");
                targetCitizen.addSettlementRank("mayor");

                UnitedLandsDataManager.instance().updateCitizenDbData(currentMayor);
                UnitedLandsDataManager.instance().updateCitizenDbData(targetCitizen);

                if (currentMayor.getPlayer().isOnline()) {
                    Messenger.sendMessage(currentMayor.getPlayer().getPlayer(),
                            messageProvider.get("settlement.ranks.lost"),
                            Map.of("rank", "mayor"), messageProvider.get("prefix"));
                }
                if (targetPlayer.isOnline()) {
                    Messenger.sendMessage(targetPlayer, messageProvider.get("settlement.ranks.received"),
                            Map.of("rank", "mayor"), messageProvider.get("prefix"));
                }

            })
                    .setTitle("<yellow>Are you sure you want to give the mayorship to " + args[0]
                            + " permanently?</yellow>")
                    .setSender(context.player())
                    .setReceiver(context.player())
                    .setTimeoutSeconds(30)
                    .setAcceptCommand("/approve new-mayor")
                    .setCancelCommand("/reject new-mayor")
                    .setDiscriminator(context.settlement().getName())
                    .send();

            ConfirmationManager.instance().queueConfirmation(confirmation);

        } else {

            if (!hasPermission("settlement.manage.ranks.other", context.citizen()))
                return;

            targetCitizen.addSettlementRank(args[1]);
            UnitedLandsDataManager.instance().updateCitizenDbData(targetCitizen);

            if (targetPlayer.isOnline()) {
                Messenger.sendMessage(targetPlayer, messageProvider.get("settlement.ranks.received"),
                        Map.of("rank", args[1]), messageProvider.get("prefix"));
            }

        }

        Messenger.sendMessage(context.player(), messageProvider.get("settlement.ranks.added"),
                Map.of("rank", args[1], "name", targetCitizen.getName()),
                messageProvider.get("prefix"));

    }

}

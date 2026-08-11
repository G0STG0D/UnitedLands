package org.unitedlands.unitedlands.commands.handlers.settlement.rank;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.ConfirmationManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlementRank.class,
        name = "add",
        description = "Add a settlement rank to a citizen",
        usage = "/settlement rank add <player> <rank>",
        playerOnly = true,
        catchAll = true
)
public class CmdSettlementRankAdd extends SettlementCommandHandler {

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
            sendUsage(sender);
            return;
        }

        // Concrete permission checks are further down
        var context = validate(sender, null);
        if (context == null)
            return;

        var targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.GENERAL_ERRORS__PLAYER_NOT_FOUND.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        var targetCitizen = getCitizen(targetPlayer);
        if (targetCitizen == null)
            return;

        if (!context.settlement().equals(targetCitizen.getSettlement())) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__NOT_IN_SETTLEMENT.path()),
                    Map.of("citizen", args[0]), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (!PermissionManager.instance().getSettlementRanks().contains(args[1])) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.ADMIN__SETTLEMENT__UNKNOWN_RANK.path()),
                    Map.of("rank", args[1]), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (targetCitizen.getSettlementRanks().contains(args[1])) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__ADDRANK__RANK_ALREADY_OWNED.path()),
                    Map.of("citizen", context.citizen().getName(), "rank", args[1], "name", targetCitizen.getName()),
                    MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        // TODO: Move strings to config
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
                    Messenger.sendMessage(currentMayor.getPlayer(),
                            MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__REMOVERANK__LOST.path()),
                            Map.of("rank", "mayor"), MessageProvider.instance().get(Message.PREFIX.path()));
                }
                if (targetPlayer.isOnline()) {
                    Messenger.sendMessage(targetPlayer, MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__ADDRANK__RANK_RECEIVED.path()),
                            Map.of("rank", "mayor"), MessageProvider.instance().get(Message.PREFIX.path()));
                }

            })
                    .setTitle("<yellow>Are you sure you want to give the mayorship to " + args[0]
                            + " permanently?</yellow>")
                    .setSender(context.player())
                    .setReceiver(context.player())
                    .send();

            ConfirmationManager.instance().queueConfirmation(confirmation);

        } else {

            if (!hasPermission("settlement.manage.ranks.other", context.citizen()))
                return;

            targetCitizen.addSettlementRank(args[1]);
            UnitedLandsDataManager.instance().updateCitizenDbData(targetCitizen);

            if (targetPlayer.isOnline()) {
                Messenger.sendMessage(targetPlayer, MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__ADDRANK__RANK_RECEIVED.path()),
                        Map.of("rank", args[1]), MessageProvider.instance().get(Message.PREFIX.path()));
            }

        }

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__ADDRANK__SUCCESS.path()),
                Map.of("rank", args[1], "name", targetCitizen.getName()),
                MessageProvider.instance().get(Message.PREFIX.path()));

    }

}

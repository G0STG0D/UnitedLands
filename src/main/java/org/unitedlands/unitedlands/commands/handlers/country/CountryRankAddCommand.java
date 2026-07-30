package org.unitedlands.unitedlands.commands.handlers.country;

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
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.managers.ConfirmationManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.Messenger;

public class CountryRankAddCommand extends CountryCommandHandler {

    public CountryRankAddCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);

    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {

        var citizen = getCitizen((Player) sender);
        if (citizen == null)
            return null;
        var country = getCitizenCountry(citizen);
        if (country == null)
            return null;

        switch (args.length) {
            case 1:
                return country.getCitizens().stream().map(Citizen::getName).collect(Collectors.toList());
            case 2:
                return PermissionManager.instance().getCountryRanks();
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

        if (!context.country().equals(targetCitizen.getCountry())) {
            Messenger.sendMessage(context.player(), messageProvider.get("country.ranks.not-in-country"),
                    Map.of("name", args[0]), messageProvider.get("prefix"));
            return;
        }

        if (!PermissionManager.instance().getCountryRanks().contains(args[1])) {
            Messenger.sendMessage(context.player(), messageProvider.get("country.ranks.unknown-rank"),
                    Map.of("rank", args[1]), messageProvider.get("prefix"));
            return;
        }

        if (targetCitizen.getCountryRanks().contains(args[1])) {
            Messenger.sendMessage(context.player(), messageProvider.get("country.ranks.rank-already-owned"),
                    Map.of("rank", args[1], "name", targetCitizen.getName()),
                    messageProvider.get("prefix"));
            return;
        }

        if (args[1].equals("leader")) {

            if (!hasPermission("country.manage.ranks.leader", context.citizen()))
                return;

            var confirmation = new Confirmation("new-leader");
            confirmation.setRunnable(() -> {

                var currentLeader = context.country().getLeader();
                currentLeader.removeCountryRank("leader");
                targetCitizen.addCountryRank("leader");

                UnitedLandsDataManager.instance().updateCitizenDbData(currentLeader);
                UnitedLandsDataManager.instance().updateCitizenDbData(targetCitizen);

                if (currentLeader.getPlayer().isOnline()) {
                    Messenger.sendMessage(currentLeader.getPlayer().getPlayer(),
                            messageProvider.get("country.ranks.lost"),
                            Map.of("rank", "leader"), messageProvider.get("prefix"));
                }
                if (targetPlayer.isOnline()) {
                    Messenger.sendMessage(targetPlayer, messageProvider.get("country.ranks.received"),
                            Map.of("rank", "leader"), messageProvider.get("prefix"));
                }

            })
                    .setTitle("<yellow>Are you sure you want to give the leadership to " + args[0]
                            + " permanently?</yellow>")
                    .setSender(context.player())
                    .setReceiver(context.player())
                    .setTimeoutSeconds(30)
                    .setAcceptCommand("/approve new-leader")
                    .setCancelCommand("/reject new-leader")
                    .setDiscriminator(context.country().getName())
                    .send();

            ConfirmationManager.instance().queueConfirmation(confirmation);

        } else {

            if (!hasPermission("country.manage.ranks.other", context.citizen()))
                return;

            targetCitizen.addCountryRank(args[1]);
            UnitedLandsDataManager.instance().updateCitizenDbData(targetCitizen);

            if (targetPlayer.isOnline()) {
                Messenger.sendMessage(targetPlayer, messageProvider.get("country.ranks.received"),
                        Map.of("rank", args[1]), messageProvider.get("prefix"));
            }

        }

        Messenger.sendMessage(context.player(), messageProvider.get("country.ranks.added"),
                Map.of("rank", args[1], "name", targetCitizen.getName()),
                messageProvider.get("prefix"));

    }

}

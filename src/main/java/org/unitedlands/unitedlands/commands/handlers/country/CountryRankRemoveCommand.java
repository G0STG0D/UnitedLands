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
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.Messenger;

public class CountryRankRemoveCommand extends CountryCommandHandler {

    public CountryRankRemoveCommand(UnitedLands plugin, IMessageProvider messageProvider) {
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
                var targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer == null)
                    return null;
                var targetCitizen = UnitedLandsDataManager.instance().getCitizen(targetPlayer);
                if (targetCitizen == null)
                    return null;
                return targetCitizen.getCountryRanks().stream().collect(Collectors.toList());
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

        if (!targetCitizen.getCountryRanks().contains(args[1])) {
            Messenger.sendMessage(context.player(), messageProvider.get("country.ranks.rank-not-owned"),
                    Map.of("rank", args[1], "name", targetCitizen.getName()),
                    messageProvider.get("prefix"));
            return;
        }

        if (args[1].equals("leader")) {

            if (!hasPermission("country.manage.ranks.leader", context.citizen()))
                return;

            Messenger.sendMessage(context.player(), messageProvider.get("country.ranks.cannot-remove-leader"),
                    null, messageProvider.get("prefix"));
            return;

        } else {

            if (!hasPermission("country.manage.ranks.other", context.citizen()))
                return;

            targetCitizen.removeCountryRank(args[1]);
            UnitedLandsDataManager.instance().updateCitizenDbData(targetCitizen);

            if (targetPlayer.isOnline()) {
                Messenger.sendMessage(targetPlayer, messageProvider.get("country.ranks.lost"),
                        Map.of("rank", args[1]), messageProvider.get("prefix"));
            }

        }

        Messenger.sendMessage(context.player(), messageProvider.get("country.ranks.removed"),
                Map.of("rank", args[1], "name", targetCitizen.getName()),
                messageProvider.get("prefix"));

    }

}

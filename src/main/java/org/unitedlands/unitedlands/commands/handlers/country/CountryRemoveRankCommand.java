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
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.Messenger;

public class CountryRemoveRankCommand extends CountryCommandHandler {

    public CountryRemoveRankCommand(UnitedLands plugin, IMessageProvider messageProvider) {
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
            Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__COUNTRY__REMOVERANK__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var context = validate(sender, null);
        if (context == null)
            return;

        var targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.GENERAL_ERRORS__PLAYER_NOT_FOUND.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var targetCitizen = getCitizen(targetPlayer);
        if (targetCitizen == null)
            return;

        if (!context.country().equals(targetCitizen.getCountry())) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__COUNTRY__NOT_IN_COUNTRY.path()),
                    Map.of("name", args[0]), messageProvider.get(Message.PREFIX.path()));
            return;
        }

        if (!PermissionManager.instance().getCountryRanks().contains(args[1])) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.ADMIN__COUNTRY__UNKNOWN_RANK.path()),
                    Map.of("rank", args[1]), messageProvider.get(Message.PREFIX.path()));
            return;
        }

        if (!targetCitizen.getCountryRanks().contains(args[1])) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__COUNTRY__REMOVERANK__RANK_NOT_OWNED.path()),
                    Map.of("rank", args[1], "name", targetCitizen.getName()),
                    messageProvider.get(Message.PREFIX.path()));
            return;
        }

        if (args[1].equals("leader")) {

            if (!hasPermission("country.manage.ranks.leader", context.citizen()))
                return;

            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__COUNTRY__REMOVERANK__CANNOT_REMOVE_LEADER.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;

        } else {

            if (!hasPermission("country.manage.ranks.other", context.citizen()))
                return;

            targetCitizen.removeCountryRank(args[1]);
            UnitedLandsDataManager.instance().updateCitizenDbData(targetCitizen);

            if (targetPlayer.isOnline()) {
                Messenger.sendMessage(targetPlayer, messageProvider.get(Message.PLAYER__COUNTRY__REMOVERANK__RANK_LOST.path()),
                        Map.of("rank", args[1]), messageProvider.get(Message.PREFIX.path()));
            }

        }

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__COUNTRY__REMOVERANK__RANK_REMOVED.path()),
                Map.of("rank", args[1], "name", targetCitizen.getName()),
                messageProvider.get(Message.PREFIX.path()));

    }

}

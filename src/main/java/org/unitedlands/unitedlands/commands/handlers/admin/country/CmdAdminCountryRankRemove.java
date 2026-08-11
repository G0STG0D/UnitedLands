package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminCountryRank.class,
        name = "remove",
        description = "Removes a country rank from a player",
        usage = "/ula country rank remove <player> <rank>",
        catchAll = true
)
public class CmdAdminCountryRankRemove extends CountryAdminCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {

        switch (args.length) {
            case 1:
                return UnitedLandsDataManager.instance().getCountryNames();
            case 2:
                var country = UnitedLandsDataManager.instance().getCountry(args[0]);
                if (country != null)
                    return country.getCitizens().stream().map(Citizen::getName).collect(Collectors.toList());
            case 3:
                return PermissionManager.instance().getCountryRanks();
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 3) {
            sendUsage(sender);
            return;
        }

        var country = getCountry(sender, args[0]);
        if (country == null) {
            return;
        }

        var citizen = getCitizen(sender, args[1]);
        if (citizen == null) {
            return;
        }

        if (!citizen.hasCountry() || !country.equals(citizen.getCountry())) {
            Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__COUNTRY__CITZEN_NOT_IN_COUNTRY.path()),
                    Map.of("citizen", citizen.getName()),
                    MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (!PermissionManager.instance().getCountryRanks().contains(args[2])) {
            Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__COUNTRY__UNKNOWN_RANK.path()), Map.of("rank", args[2]),
                    MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        citizen.removeCountryRank(args[2]);
        UnitedLandsDataManager.instance().updateCitizenDbData(citizen);

        Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__COUNTRY__REMOVERANK__SUCCESS.path()),
                Map.of("rank", args[2], "citizen", citizen.getName(), "country", country.getName()), MessageProvider.instance().get(Message.PREFIX.path()));

    }

}

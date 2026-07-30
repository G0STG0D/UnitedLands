package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.Messenger;

public class AdminCountryAddRankCommand extends CountryAdminCommandHandler {

    public AdminCountryAddRankCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);

    }

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
            Messenger.sendMessage(sender, messageProvider.get("admin.usage.country.addrank"), null, messageProvider.get("prefix"));
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
            Messenger.sendMessage(sender, messageProvider.get("admin.country.addrank.citizen-not-in-country"), Map.of("citizen", citizen.getName()),
                    messageProvider.get("prefix"));
            return;
        }

        if (!PermissionManager.instance().getCountryRanks().contains(args[2])) {
            Messenger.sendMessage(sender, messageProvider.get("admin.country.addrank.unknown-rank"), Map.of("rank", args[2]), messageProvider.get("prefix"));
            return;
        }

        citizen.addCountryRank(args[2]);
        UnitedLandsDataManager.instance().updateCitizenDbData(citizen);

        Messenger.sendMessage(sender, messageProvider.get("admin.country.addrank.success"),
                Map.of("rank", args[2], "citizen", citizen.getName(), "country", country.getName()), messageProvider.get("prefix"));

    }

}

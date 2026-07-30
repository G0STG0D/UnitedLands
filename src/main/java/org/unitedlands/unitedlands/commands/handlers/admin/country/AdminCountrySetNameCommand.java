package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class AdminCountrySetNameCommand extends CountryAdminCommandHandler {

    public AdminCountrySetNameCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 2) {
            Messenger.sendMessage(player, messageProvider.get("admin.usage.country.setname"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var country = getCountry(player, args[0]);
        if (country == null) {
            return;
        }

        country.setName(args[1]);

        UnitedLandsDataManager.instance().updateCountryDbData(country);

        Messenger.sendMessage(player, messageProvider.get("admin.country.setname"),
                Map.of("country", country.getName()), messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        if (args.length == 1) {
            return UnitedLandsDataManager.instance().getCountryNames();
        }
        return null;
    }

}

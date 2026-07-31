package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.ColorUtils;
import org.unitedlands.utils.Messenger;

public class AdminCountrySetColorCommand extends CountryAdminCommandHandler {

    public AdminCountrySetColorCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 2) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__COUNTRY__SETCOLOR__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var country = getCountry(player, args[0]);
        if (country == null) {
            return;
        }

        if (!(args[1].length() == 7) || !ColorUtils.isValidHexColor(args[1])) {
            Messenger.sendMessage(player, messageProvider.get(Message.GENERAL_ERRORS__WRONG_COLOR_FORMAT.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        country.setFillColor(args[1] + "10");
        country.setStrokeColor(args[1]);

        UnitedLandsDataManager.instance().updateCountryDbData(country);

        Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__COUNTRY__SETCOLOR__SUCCESS.path()),
                Map.of("country", country.getName()), messageProvider.get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        if (args.length == 1) {
            return UnitedLandsDataManager.instance().getCountryNames();
        }
        return null;
    }

}

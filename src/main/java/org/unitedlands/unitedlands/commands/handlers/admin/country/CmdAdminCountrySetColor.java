package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.ColorUtils;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminCountrySet.class,
        name = "color",
        description = "Sets a country color",
        usage = "/ula country set color <#hexcolor>",
        catchAll = true
)
public class CmdAdminCountrySetColor extends CountryAdminCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sendUsage(sender);
            return;
        }

        var country = getCountry(sender, args[0]);
        if (country == null) {
            return;
        }

        if (!(args[1].length() == 7) || !ColorUtils.isValidHexColor(args[1])) {
            Messenger.sendMessage(sender, MessageProvider.instance().get(Message.GENERAL_ERRORS__WRONG_COLOR_FORMAT.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        country.setFillColor(args[1] + "10");
        country.setStrokeColor(args[1]);

        UnitedLandsDataManager.instance().updateCountryDbData(country, true);

        Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__COUNTRY__SETCOLOR__SUCCESS.path()),
                Map.of("country", country.getName(), "color", args[1]), MessageProvider.instance().get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        if (args.length == 1) {
            return UnitedLandsDataManager.instance().getCountryNames();
        }
        return null;
    }

}

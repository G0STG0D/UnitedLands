package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.ColorUtils;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
    parent          = CmdCountrySet.class,
    name            = "color",
    description     = "Sets the country color",
    usage           = "/country set color <#hexcolor>",
    playerOnly      = true
)
public class CmdCountrySetColor extends CountryCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "country.setcolor");
        if (context == null)
            return;

        if (!(args[0].length() == 7) || !ColorUtils.isValidHexColor(args[0])) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.GENERAL_ERRORS__WRONG_COLOR_FORMAT.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        context.country().setFillColor(args[0] + "10");
        context.country().setStrokeColor(args[0]);

        UnitedLandsDataManager.instance().updateCountryDbData(context.country());

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__SETCOLOR__SUCCESS.path()),
                Map.of("color", args[0]), MessageProvider.instance().get(Message.PREFIX.path()));
    }

}

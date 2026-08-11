package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
    parent          = CmdCountrySet.class,
    name            = "name",
    description     = "Sets the country name",
    usage           = "/country set name <new_name>",
    playerOnly      = true
)
public class CmdCountrySetName extends CountryCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "country.setname");
        if (context == null)
            return;

        context.country().setName(args[0]);

        UnitedLandsDataManager.instance().updateCountryDbData(context.country());

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__SETNAME__SUCCESS.path()),
                Map.of("country", context.country().getCleanName()), MessageProvider.instance().get(Message.PREFIX.path()));
    }

}

package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.ColorUtils;
import org.unitedlands.utils.Messenger;

public class CountrySetColorCommand extends CountryCommandHandler {

    public CountrySetColorCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1)
            return;

        var context = validate(sender, "country.setcolor");
        if (context == null)
            return;

        if (!(args[0].length() == 7) || !ColorUtils.isValidHexColor(args[0])) {
            Messenger.sendMessage(context.player(), messageProvider.get("country.setcolor.wrong-format"),
                    null, messageProvider.get("prefix"));
            return;
        }

        context.country().setFillColor(args[0] + "10");
        context.country().setStrokeColor(args[0]);

        UnitedLandsDataManager.instance().updateCountryDbData(context.country());

        Messenger.sendMessage(context.player(), messageProvider.get("country.setcolor.set"),
                null, messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        return null;
    }

}

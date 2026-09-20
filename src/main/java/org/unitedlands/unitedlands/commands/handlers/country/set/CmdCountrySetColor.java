package org.unitedlands.unitedlands.commands.handlers.country.set;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;

import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.ColorUtils;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdCountrySet.class,
        name = "color",
        description = "Sets the country color",
        usage = "/country set color <#hexcolor>",
        playerOnly = true
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
            United.messenger().send(context.player(), "general-errors.wrong-color-format");
            return;
        }

        context.country().setFillColor(args[0] + "10");
        context.country().setStrokeColor(args[0]);

        UnitedLandsDataManager.instance().updateCountryDbData(context.country(), true);

        United.messenger().send(context.player(), "player.country.setcolor.success", args[0]);
    }

}

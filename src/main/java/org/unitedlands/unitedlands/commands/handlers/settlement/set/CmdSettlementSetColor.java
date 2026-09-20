package org.unitedlands.unitedlands.commands.handlers.settlement.set;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;

import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.ColorUtils;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdSettlementSet.class,
        name = "color",
        description = "Sets the settlement color",
        usage = "/settlement set color <#hexcolor>",
        playerOnly = true
)
public class CmdSettlementSetColor extends SettlementCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "settlement.setcolor");
        if (context == null)
            return;

        if (!(args[0].length() == 7) || !ColorUtils.isValidHexColor(args[0])) {
            United.messenger().send(context.player(), "general-errors.wrong-color-format");
            return;
        }

        context.settlement().setFillColor(args[0] + "10");
        context.settlement().setStrokeColor(args[0]);

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement(), true);

        United.messenger().send(context.player(), "player.settlement.setcolor.success", args[0]);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

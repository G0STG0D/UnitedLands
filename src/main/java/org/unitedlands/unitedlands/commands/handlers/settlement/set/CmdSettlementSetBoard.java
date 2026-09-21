package org.unitedlands.unitedlands.commands.handlers.settlement.set;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;

import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdSettlementSet.class,
        name = "board",
        description = "Sets the settlement board",
        usage = "/settlement set board <Your message here...>",
        playerOnly = true,
        catchAll = true
)
public class CmdSettlementSetBoard extends SettlementCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "settlement.setboard");
        if (context == null)
            return;

        String message = "";
        if (args[0].equalsIgnoreCase("EMPTY")) {
            context.settlement().setTownBoard(null);
            message = "player.settlement.setboard.cleared";
        } else {
            context.settlement().setTownBoard(String.join(" ", args));
            message = "player.settlement.setboard.success";
        }

        context.settlement().saveAndRender();
        
        United.messenger().send(context.player(), message);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

package org.unitedlands.unitedlands.commands.handlers.settlement.set;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

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
            message = MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__SETBOARD__CLEARED.path());
        } else {
            context.settlement().setTownBoard(String.join(" ", args));
            message = MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__SETBOARD__SUCCESS.path());
        }

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement(), true);

        Messenger.sendMessage(context.player(), message,
                null, MessageProvider.instance().get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

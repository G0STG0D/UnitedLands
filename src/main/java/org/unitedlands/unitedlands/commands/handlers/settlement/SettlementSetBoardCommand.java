package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementSetBoardCommand extends SettlementCommandHandler {

    public SettlementSetBoardCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length == 0) {
            Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__SETTLEMENT__SETBOARD__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var context = validate(sender, "settlement.setboard");
        if (context == null)
            return;

        String message = "";
        if (args[0].equalsIgnoreCase("EMPTY")) {
            context.settlement().setTownBoard(null);
            message = messageProvider.get(Message.PLAYER__SETTLEMENT__SETBOARD__CLEARED.path());
        } else {
            context.settlement().setTownBoard(String.join(" ", args));
            message = messageProvider.get(Message.PLAYER__SETTLEMENT__SETBOARD__SUCCESS.path());
        }

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

        Messenger.sendMessage(context.player(), message,
                null, messageProvider.get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

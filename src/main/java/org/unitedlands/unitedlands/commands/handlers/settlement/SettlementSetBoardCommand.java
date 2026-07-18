package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementSetBoardCommand extends SettlementCommandHandler {

    public SettlementSetBoardCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length == 0)
            // TODO: Usage
            return;

        var context = validate(sender, "settlement.setboard");
        if (context == null)
            return;
        
        String message = "";
        if (args[0].equalsIgnoreCase("EMPTY")) {
            context.settlement().setTownBoard(null);
            message = messageProvider.get("settlement.setboard.cleared");
        } else {
            context.settlement().setTownBoard(String.join(" ", args));
            message = messageProvider.get("settlement.setboard.set");
        }

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

        Messenger.sendMessage(context.player(), message,
                null, messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

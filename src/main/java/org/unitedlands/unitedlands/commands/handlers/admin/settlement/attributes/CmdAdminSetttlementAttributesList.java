package org.unitedlands.unitedlands.commands.handlers.admin.settlement.attributes;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminSettlementAttributes.class,
        name = "list",
        description = "Lists all settlement attribues",
        usage = "/ula settlement attributes list"
)

public class CmdAdminSetttlementAttributesList extends SettlementAdminCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var settlement = getSettlement(sender, args[0]);
        if (settlement == null) {
            return;
        }

        var attributes = settlement.getAttributes();
        for (var set : attributes.entrySet()) {
            var v = set.getValue();
            String entry = "<bold>" + set.getKey() + "</bold> - Current value: " + v.getCurrentValue() + " | Min value: " + v.getMinValue() + " | Max value: " + v.getMaxValue() + " | Daily change: "
                    + v.getDailyChange();
            Messenger.sendMessage(sender, entry, null, MessageProvider.instance().get(Message.PREFIX.path()));
        }
    }

}

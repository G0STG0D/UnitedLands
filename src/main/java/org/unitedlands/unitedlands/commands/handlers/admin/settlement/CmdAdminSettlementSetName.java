package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminSettlementSet.class,
        name = "name",
        description = "Changes a settlement's name",
        usage = "/ula settlement set name <settlement_name> <new_name>"
)
public class CmdAdminSettlementSetName extends SettlementAdminCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sendUsage(sender);
            return;
        }
  
        var settlement = getSettlement(sender, args[0]);
        if (settlement == null) {
            return;
        }

        var oldname = settlement.getName();

        settlement.setName(args[1]);

        Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__SETTLEMENT__SETNAME__SUCCESS.path()),
                Map.of("oldname", oldname, "newname", settlement.getName()), MessageProvider.instance().get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return UnitedLandsDataManager.instance().getSettlementNames();
        return null;
    }

}

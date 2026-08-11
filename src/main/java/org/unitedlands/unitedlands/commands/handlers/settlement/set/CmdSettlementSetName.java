package org.unitedlands.unitedlands.commands.handlers.settlement.set;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.commands.handlers.settlement.rank.CmdSettlementRank;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlementRank.class,
        name = "name",
        description = "Sets the settlement name",
        usage = "/settlement set name <new_name>",
        playerOnly = true
)
public class CmdSettlementSetName extends SettlementCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "settlement.setname");
        if (context == null)
            return;

        var oldname = context.settlement().getName();

        context.settlement().setName(args[0]);

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__SETNAME__SUCCESS.path()),
                Map.of("oldname", oldname, "newname", context.settlement().getName()), MessageProvider.instance().get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

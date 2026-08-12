package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlementChunk.class,
        name = "evict",
        description = "Evicts the current owner of a settlement chunk",
        usage = "/settlementchunk evict",
        playerOnly = true
)
public class CmdSettlementChunkEvict extends SettlementChunkCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, "settlement.plot.evict");
        if (context == null)
            return;
        
        if (!context.settlementChunk().hasOwner()) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENTCHUNK__EVICT__NOT_OWNER.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        context.settlementChunk().removeOwner();
        UnitedLandsDataManager.instance().updateSettlementChunkDbData(context.settlementChunk());

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENTCHUNK__EVICT__SUCCESS.path()),
                null, MessageProvider.instance().get(Message.PREFIX.path()));

    }

}

package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementChunkEvictCommand extends SettlementChunkCommandHandler {

    public SettlementChunkEvictCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

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
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENTCHUNK__EVICT__NOT_OWNER.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        context.settlementChunk().removeOwner();
        UnitedLandsDataManager.instance().updateSettlementChunkDbData(context.settlementChunk());

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENTCHUNK__EVICT__SUCCESS.path()),
                null, messageProvider.get(Message.PREFIX.path()));

    }

}

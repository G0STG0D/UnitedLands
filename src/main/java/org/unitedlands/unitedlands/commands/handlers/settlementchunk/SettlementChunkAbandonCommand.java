package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementChunkAbandonCommand extends SettlementChunkCommandHandler {

    public SettlementChunkAbandonCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, null);
        if (context == null)
            return;
        
        if (!context.settlementChunk().hasOwner() || !context.settlementChunk().getOwner().equals(context.citizen())) {
            Messenger.sendMessage(context.player(), messageProvider.get("settlementchunk.abandon.not-owner"),
                    null, messageProvider.get("prefix"));
            return;
        }

        context.settlementChunk().removeOwner();
        UnitedLandsDataManager.instance().updateSettlementChunkDbData(context.settlementChunk());

        Messenger.sendMessage(context.player(), messageProvider.get("settlementchunk.abandon.success"),
                null, messageProvider.get("prefix"));

    }

}

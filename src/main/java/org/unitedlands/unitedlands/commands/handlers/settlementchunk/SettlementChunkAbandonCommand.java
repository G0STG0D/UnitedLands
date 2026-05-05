package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
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

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;
        var settlementChunk = getSettlementChunk(player);
        if (settlementChunk == null)
            return;

        if (!settlementChunk.hasOwner() || !settlementChunk.getOwner().equals(citizen)) {
            Messenger.sendMessage(player, messageProvider.get("settlementchunk.abandon.not-owner"),
                    null, messageProvider.get("prefix"));
            return;
        }

        settlementChunk.removeOwner();
        GlobalDataManager.instance().updateSettlementChunkDbData(settlementChunk);

        Messenger.sendMessage(player, messageProvider.get("settlementchunk.abandon.success"),
                null, messageProvider.get("prefix"));

    }

}

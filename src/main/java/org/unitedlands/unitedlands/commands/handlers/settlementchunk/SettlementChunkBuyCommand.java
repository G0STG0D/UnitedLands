package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementChunkBuyCommand extends SettlementChunkCommandHandler {

    public SettlementChunkBuyCommand(UnitedLands plugin, IMessageProvider messageProvider) {
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

        if (!settlementChunk.isForSale()) {
            Messenger.sendMessage(player, messageProvider.get("settlementchunk.buy.not-for-sale"),
                    null, messageProvider.get("prefix"));
            return;
        }

        // TODO: Check for foreigners
        // TODO: Check and deduct money

        settlementChunk.setSalePrice(null);
        settlementChunk.setOwner(citizen);

        GlobalDataManager.instance().updateSettlementChunkDbData(settlementChunk);

        Messenger.sendMessage(player, messageProvider.get("settlementchunk.buy.success"),
                null, messageProvider.get("prefix"));
    }

}

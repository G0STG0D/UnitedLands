package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.classes.events.settlementChunk.SettlementChunkPrePurchaseEvent;
import org.unitedlands.unitedlands.classes.events.settlementChunk.SettlementChunkPurchaseEvent;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
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

        var context = validate(sender, null);
        if (context == null)
            return;
        
        if (!context.settlementChunk().isForSale()) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENTCHUNK__BUY__NOT_FOR_SALE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        // TODO: Check for foreigners

        if (!UnitedLandsEconomyManager.instance().has(context.citizen().getUuid(),
                new BigDecimal(context.settlementChunk().getSalePrice()))) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.GENERAL_ERRORS__NO_FUNDS.path()),
                    Map.of("amount", UnitedLandsEconomyManager.instance().format(context.settlementChunk().getSalePrice())),
                    messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var preEvent = new SettlementChunkPrePurchaseEvent(context.settlementChunk().getSettlement(), context.settlementChunk(), context.citizen());
        preEvent.callEvent();
        if (preEvent.isCancelled())
            return;

        UnitedLandsEconomyManager.instance().withdraw(context.citizen().getUuid(), context.settlementChunk().getSalePrice());
        UnitedLandsEconomyManager.instance().deposit(context.settlementChunk().getSettlement().getUuid(),
                context.settlementChunk().getSalePrice());

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENTCHUNK__BUY__SUCCESS.path()),
                Map.of("price", UnitedLandsEconomyManager.instance().format(context.settlementChunk().getSalePrice())),
                messageProvider.get(Message.PREFIX.path()));

        context.settlementChunk().setSalePrice(null);
        context.settlementChunk().setOwner(context.citizen());

        (new SettlementChunkPurchaseEvent(context.settlementChunk().getSettlement(), context.settlementChunk(), context.citizen())).callEvent();

        UnitedLandsDataManager.instance().updateSettlementChunkDbData(context.settlementChunk());
    }

}

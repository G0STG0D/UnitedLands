package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.classes.events.settlementChunk.SettlementChunkPrePurchaseEvent;
import org.unitedlands.unitedlands.classes.events.settlementChunk.SettlementChunkPurchaseEvent;
import org.unitedlands.unitedlands.managers.EconomyManager;
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

        if (!EconomyManager.instance().has(citizen.getUuid(), new BigDecimal(settlementChunk.getSalePrice()))) {
            Messenger.sendMessage(player, messageProvider.get("errors.no-funds"),
                    Map.of("amount", EconomyManager.instance().format(settlementChunk.getSalePrice())),
                    messageProvider.get("prefix"));
            return;
        }

        var preEvent = new SettlementChunkPrePurchaseEvent(settlementChunk.getSettlement(), settlementChunk, citizen);
        preEvent.callEvent();
        if (preEvent.isCancelled())
            return;

        EconomyManager.instance().withdraw(citizen.getUuid(), settlementChunk.getSalePrice());
        EconomyManager.instance().deposit(settlementChunk.getSettlement().getUuid(), settlementChunk.getSalePrice());

        Messenger.sendMessage(player, messageProvider.get("settlementchunk.buy.success"),
                Map.of("price", EconomyManager.instance().format(settlementChunk.getSalePrice())),
                messageProvider.get("prefix"));

        settlementChunk.setSalePrice(null);
        settlementChunk.setOwner(citizen);

        (new SettlementChunkPurchaseEvent(settlementChunk.getSettlement(), settlementChunk, citizen)).callEvent();

        GlobalDataManager.instance().updateSettlementChunkDbData(settlementChunk);
    }

}

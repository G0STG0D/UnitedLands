package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementChunkForSaleCommand extends SettlementChunkCommandHandler {

    public SettlementChunkForSaleCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            // TODO: Usage
            return;
        }

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;
        var settlementChunk = getSettlementChunk(player);
        if (settlementChunk == null)
            return;

        if (!hasPermission("settlement.plot.sell", citizen, settlementChunk))
            return;

        if (settlementChunk.hasOwner()) {
            Messenger.sendMessage(player, messageProvider.get("settlementchunk.sell.has-owner"),
                    Map.of("owner", settlementChunk.getOwner().getName()),
                    messageProvider.get("prefix"));
            return;
        }

        Integer price = null;
        if (args[0].equalsIgnoreCase("clear")) {
            settlementChunk.setSalePrice(null);
            GlobalDataManager.instance().updateSettlementChunkDbData(settlementChunk);
            Messenger.sendMessage(player, messageProvider.get("settlementchunk.sell.cleared"),
                    null, messageProvider.get("prefix"));
        } else {
            try {
                price = Integer.parseInt(args[0]);
            } catch (Exception ex) {
                Messenger.sendMessage(player, messageProvider.get("errors.wrong-number-format"),
                        Map.of("input", args[0]), messageProvider.get("prefix"));
                return;
            }
            settlementChunk.setSalePrice(price);
            GlobalDataManager.instance().updateSettlementChunkDbData(settlementChunk);

            Messenger.sendMessage(player, messageProvider.get("settlementchunk.sell.success"),
                    Map.of("price", EconomyManager.instance().format(price)), messageProvider.get("prefix"));
        }
    }

}

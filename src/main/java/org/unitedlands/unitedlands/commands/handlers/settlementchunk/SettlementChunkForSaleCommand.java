package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
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

        var context = validate(sender, "settlement.plot.sell");
        if (context == null)
            return;
        
        if (context.settlementChunk().hasOwner()) {
            Messenger.sendMessage(context.player(), messageProvider.get("settlementchunk.sell.has-owner"),
                    Map.of("owner", context.settlementChunk().getOwner().getName()),
                    messageProvider.get("prefix"));
            return;
        }

        Integer price = null;
        if (args[0].equalsIgnoreCase("clear")) {
            context.settlementChunk().setSalePrice(null);
            UnitedLandsDataManager.instance().updateSettlementChunkDbData(context.settlementChunk());
            Messenger.sendMessage(context.player(), messageProvider.get("settlementchunk.sell.cleared"),
                    null, messageProvider.get("prefix"));
        } else {
            try {
                price = Integer.parseInt(args[0]);
            } catch (Exception ex) {
                Messenger.sendMessage(context.player(), messageProvider.get("errors.wrong-number-format"),
                        Map.of("input", args[0]), messageProvider.get("prefix"));
                return;
            }
            context.settlementChunk().setSalePrice(price);
            UnitedLandsDataManager.instance().updateSettlementChunkDbData(context.settlementChunk());

            Messenger.sendMessage(context.player(), messageProvider.get("settlementchunk.sell.success"),
                    Map.of("price", UnitedLandsEconomyManager.instance().format(price)), messageProvider.get("prefix"));
        }
    }

}

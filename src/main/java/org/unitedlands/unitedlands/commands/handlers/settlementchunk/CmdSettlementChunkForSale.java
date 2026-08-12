package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlementChunk.class,
        name = "forsale",
        description = "Puts a settlement chunk on sale",
        usage = "/settlementchunk forsale <price>",
        playerOnly = true
)
public class CmdSettlementChunkForSale extends SettlementChunkCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "settlement.plot.sell");
        if (context == null)
            return;

        if (context.settlementChunk().hasOwner()) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENTCHUNK__FORSALE__HAS_OWNER.path()),
                    Map.of("owner", context.settlementChunk().getOwner().getName()),
                    MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        Integer price = null;
        if (args[0].equalsIgnoreCase("clear")) {
            context.settlementChunk().setSalePrice(null);
            UnitedLandsDataManager.instance().updateSettlementChunkDbData(context.settlementChunk());
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENTCHUNK__FORSALE__CLEARED.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
        } else {
            try {
                price = Integer.parseInt(args[0]);
            } catch (Exception ex) {
                Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.GENERAL_ERRORS__WRONG_NUMBER_FORMAT.path()),
                        Map.of("input", args[0]), MessageProvider.instance().get(Message.PREFIX.path()));
                return;
            }
            context.settlementChunk().setSalePrice(price);
            UnitedLandsDataManager.instance().updateSettlementChunkDbData(context.settlementChunk());

            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENTCHUNK__FORSALE__SUCCESS.path()),
                    Map.of("price", UnitedLandsEconomyManager.instance().format(price)), MessageProvider.instance().get(Message.PREFIX.path()));
        }
    }

}

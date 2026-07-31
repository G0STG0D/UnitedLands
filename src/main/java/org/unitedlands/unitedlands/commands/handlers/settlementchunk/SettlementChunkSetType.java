package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementChunkSetType extends SettlementChunkCommandHandler {

    public SettlementChunkSetType(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return Settings.settlementChunkTypes.keySet().stream().collect(Collectors.toList());
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__SETTLEMENTCHUNK__SETTYPE__USAGE.path()), null,
                    messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var context = validate(sender, "settlement.plot.settype");
        if (context == null)
            return;

        var type = args[0];
        if (!Settings.settlementChunkTypes.containsKey(type)) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENTCHUNK__SETTYPE__UNKNOWN_TYPE.path()),
                    Map.of("type", type), messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var typeSettings = Settings.settlementChunkTypes.get(type);
        if (typeSettings.maxPerSettlement != -1) {
            var chunksOfType = context.settlementChunk().getSettlement().getChunksOfType(type);
            if (chunksOfType.size() >= typeSettings.maxPerSettlement) {
                Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENTCHUNK__SETTYPE__TOO_MANY_OF_TYPE.path()),
                        Map.of("type", type, "max", String.valueOf(typeSettings.maxPerSettlement)),
                        messageProvider.get(Message.PREFIX.path()));
                return;
            }
        }

        context.settlementChunk().setChunkType(type);
        UnitedLandsDataManager.instance().updateSettlementChunkDbData(context.settlementChunk());

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENTCHUNK__SETTYPE__SUCCESS.path()),
                Map.of("type", type), messageProvider.get(Message.PREFIX.path()));

    }

}

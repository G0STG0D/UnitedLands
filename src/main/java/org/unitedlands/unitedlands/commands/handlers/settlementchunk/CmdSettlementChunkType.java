package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlementChunk.class,
        name = "type",
        description = "Sets the type of settlement chunk",
        usage = "/settlementchunk type <type>",
        playerOnly = true
)
public class CmdSettlementChunkType extends SettlementChunkCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return Settings.settlementChunkTypes.keySet().stream().collect(Collectors.toList());
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "settlement.plot.settype");
        if (context == null)
            return;

        var type = args[0];
        if (!Settings.settlementChunkTypes.containsKey(type)) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENTCHUNK__SETTYPE__UNKNOWN_TYPE.path()),
                    Map.of("type", type), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        var typeSettings = Settings.settlementChunkTypes.get(type);
        if (typeSettings.maxPerSettlement != -1) {
            var chunksOfType = context.settlementChunk().getSettlement().getChunksOfType(type);
            if (chunksOfType.size() >= typeSettings.maxPerSettlement) {
                Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENTCHUNK__SETTYPE__TOO_MANY_OF_TYPE.path()),
                        Map.of("type", type, "max", String.valueOf(typeSettings.maxPerSettlement)),
                        MessageProvider.instance().get(Message.PREFIX.path()));
                return;
            }
        }

        context.settlementChunk().setChunkType(type);
        UnitedLandsDataManager.instance().updateSettlementChunkDbData(context.settlementChunk());

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENTCHUNK__SETTYPE__SUCCESS.path()),
                Map.of("type", type), MessageProvider.instance().get(Message.PREFIX.path()));

    }

}

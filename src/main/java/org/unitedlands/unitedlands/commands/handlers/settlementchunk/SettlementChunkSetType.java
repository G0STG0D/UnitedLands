package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
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
            // TODO: Usage
            return;
        }

        var context = validate(sender, "settlement.plot.settype");
        if (context == null)
            return;
        
        var type = args[0];
        if (!Settings.settlementChunkTypes.containsKey(type)) {
            Messenger.sendMessage(context.player(), messageProvider.get("settlementchunk.settype.unkown-type"),
                    Map.of("type", type), messageProvider.get("prefix"));
            return;
        }

        var typeSettings = Settings.settlementChunkTypes.get(type);
        if (typeSettings.maxPerSettlement != -1) {
            var chunksOfType = context.settlementChunk().getSettlement().getChunksOfType(type);
            if (chunksOfType.size() >= typeSettings.maxPerSettlement) {
                Messenger.sendMessage(context.player(), messageProvider.get("settlementchunk.settype.too-many-of-type"),
                        Map.of("type", type, "max", String.valueOf(typeSettings.maxPerSettlement)),
                        messageProvider.get("prefix"));
                return;
            }
        }

        context.settlementChunk().setChunkType(type);
        UnitedLandsDataManager.instance().updateSettlementChunkDbData(context.settlementChunk());

        Messenger.sendMessage(context.player(), messageProvider.get("settlementchunk.settype.success"),
                Map.of("type", type), messageProvider.get("prefix"));

    }

}

package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.events.settlement.SettlementUnclaimEvent;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.utils.Messenger;

public class SettlementUnclaimCommand extends SettlementCommandHandler {

    public SettlementUnclaimCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, "settlement.unclaim");
        if (context == null)
            return;

        var chunkCoords = CoordinateUtils.locationToChunkCoordinates(context.player().getLocation());

        var existingChunk = UnitedLandsDataManager.instance().getSettlementChunk(chunkCoords);
        if (existingChunk == null) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__UNCLAIM__NOT_CLAIMED.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        if (!existingChunk.getSettlement().equals(context.settlement())) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__UNCLAIM__NOT_IN_SETTLEMENT.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var spawnChunkCoords = CoordinateUtils.locationToChunkCoordinates(context.settlement().getSpawn());
        if (spawnChunkCoords.equals(chunkCoords)) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__UNCLAIM__HAS_SPAWN.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        if (context.settlement().getHomeChunkCoordinates().equals(chunkCoords)) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__UNCLAIM__IS_HOME_CHUNK.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        context.settlement().removeChunk(existingChunk);

        (new SettlementUnclaimEvent(context.settlement(), chunkCoords)).callEvent();

        UnitedLandsDataManager.instance().removeSettlementChunkDbData(existingChunk);
        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__UNCLAIM__SUCCESS.path()),
                Map.of("settlement", context.settlement().getCleanName(),
                        "chunk", chunkCoords.toString()),
                messageProvider.get(Message.PREFIX.path()));
    }

}

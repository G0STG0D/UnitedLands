package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.events.settlement.SettlementUnclaimEvent;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlement.class,
        name = "unclaim",
        description = "Unclaims a settlement chunk",
        usage = "/settlement unclaim",
        playerOnly = true
)
public class CmdSettlementUnclaim extends SettlementCommandHandler {

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
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__UNCLAIM__NOT_CLAIMED.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (!existingChunk.getSettlement().equals(context.settlement())) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__UNCLAIM__NOT_IN_SETTLEMENT.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        var spawnChunkCoords = CoordinateUtils.locationToChunkCoordinates(context.settlement().getSpawn());
        if (spawnChunkCoords.equals(chunkCoords)) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__UNCLAIM__HAS_SPAWN.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (context.settlement().getHomeChunkCoordinates().equals(chunkCoords)) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__UNCLAIM__IS_HOME_CHUNK.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        context.settlement().removeChunk(existingChunk);

        (new SettlementUnclaimEvent(context.settlement(), chunkCoords)).callEvent();

        UnitedLandsDataManager.instance().removeSettlementChunkDbData(existingChunk);
        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement(), true);

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__UNCLAIM__SUCCESS.path()),
                Map.of("settlement", context.settlement().getCleanName(),
                        "chunk", chunkCoords.toString()),
                MessageProvider.instance().get(Message.PREFIX.path()));
    }

}

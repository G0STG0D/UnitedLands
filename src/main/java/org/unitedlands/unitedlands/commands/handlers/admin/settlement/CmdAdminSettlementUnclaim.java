package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.classes.events.settlement.SettlementUnclaimEvent;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminSettlement.class,
        name = "unclaim",
        description = "Unclaims a chunk",
        usage = "/ula settlement unclaim",
        playerOnly = true
)
public class CmdAdminSettlementUnclaim extends SettlementAdminCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 0) {
            sendUsage(sender);
            return;
        }

        var chunkCoords = CoordinateUtils.locationToChunkCoordinates(player.getLocation());

        var existingChunk = UnitedLandsDataManager.instance().getSettlementChunk(chunkCoords);
        if (existingChunk == null) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__SETTLEMENT__UNCLAIM__NOT_CLAIMED.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        var settlement = existingChunk.getSettlement();

        var spawnChunkCoords = CoordinateUtils.locationToChunkCoordinates(settlement.getSpawn());
        if (spawnChunkCoords.equals(chunkCoords)) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__SETTLEMENT__UNCLAIM__HAS_SPAWN.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (settlement.getHomeChunkCoordinates().equals(chunkCoords)) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__SETTLEMENT__UNCLAIM__IS_HOME_CHUNK.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        settlement.removeChunk(existingChunk);

        (new SettlementUnclaimEvent(settlement, chunkCoords)).callEvent();

        UnitedLandsDataManager.instance().removeSettlementChunkDbData(existingChunk);
        UnitedLandsDataManager.instance().updateSettlementDbData(settlement, true);

        Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__SETTLEMENT__UNCLAIM__SUCCESS.path()),
                Map.of("settlement", settlement.getCleanName(),
                        "chunk", chunkCoords.toString()),
                MessageProvider.instance().get(Message.PREFIX.path()));
    }

}

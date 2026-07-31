package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.classes.events.settlement.SettlementUnclaimEvent;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.utils.Messenger;

public class AdminSettlementUnclaimCommand extends SettlementAdminCommandHandler {

    public AdminSettlementUnclaimCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 0) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__UNCLAIM__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        if (!hasPermission(player)) {
            return;
        }

        var chunkCoords = CoordinateUtils.locationToChunkCoordinates(player.getLocation());

        var existingChunk = UnitedLandsDataManager.instance().getSettlementChunk(chunkCoords);
        if (existingChunk == null) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__UNCLAIM__NOT_CLAIMED.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var settlement = existingChunk.getSettlement();

        var spawnChunkCoords = CoordinateUtils.locationToChunkCoordinates(settlement.getSpawn());
        if (spawnChunkCoords.equals(chunkCoords)) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__UNCLAIM__HAS_SPAWN.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        if (settlement.getHomeChunkCoordinates().equals(chunkCoords)) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__UNCLAIM__IS_HOME_CHUNK.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        settlement.removeChunk(existingChunk);

        (new SettlementUnclaimEvent(settlement, chunkCoords)).callEvent();

        UnitedLandsDataManager.instance().removeSettlementChunkDbData(existingChunk);
        UnitedLandsDataManager.instance().updateSettlementDbData(settlement);

        Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__UNCLAIM__SUCCESS.path()),
                Map.of("settlement", settlement.getCleanName(),
                        "chunk", chunkCoords.toString()),
                messageProvider.get(Message.PREFIX.path()));
    }

}

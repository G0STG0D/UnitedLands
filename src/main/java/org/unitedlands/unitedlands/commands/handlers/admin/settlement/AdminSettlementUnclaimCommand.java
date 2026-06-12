package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.classes.events.settlement.SettlementUnclaimEvent;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
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
            Messenger.sendMessage(player, messageProvider.get("admin.usage.settlement.unclaim"),
                    null, messageProvider.get("prefix"));
            return;
        }

        if (!hasPermission(player)) {
            return;
        }

        var chunkCoords = CoordinateUtils.locationToChunkCoordinates(player.getLocation());

        var existingChunk = GlobalDataManager.instance().getSettlementChunk(chunkCoords);
        if (existingChunk == null) {
            Messenger.sendMessage(player, messageProvider.get("settlement.unclaim.not-claimed"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var settlement = existingChunk.getSettlement();

        var spawnChunkCoords = CoordinateUtils.locationToChunkCoordinates(settlement.getSpawn());
        if (spawnChunkCoords.equals(chunkCoords)) {
            Messenger.sendMessage(player, messageProvider.get("settlement.unclaim.has-spawn"),
                    null, messageProvider.get("prefix"));
            return;
        }

        if (settlement.getHomeChunkCoordinates().equals(chunkCoords)) {
            Messenger.sendMessage(player, messageProvider.get("settlement.unclaim.is-home-chunk"),
                    null, messageProvider.get("prefix"));
            return;
        }

        settlement.removeChunk(existingChunk);

        (new SettlementUnclaimEvent(settlement, chunkCoords)).callEvent();

        GlobalDataManager.instance().removeSettlementChunkDbData(existingChunk);
        GlobalDataManager.instance().updateSettlementDbData(settlement);

        Pl3xMapRenderer.instance().renderSettlement(settlement);

        Messenger.sendMessage(player, messageProvider.get("admin.settlement.unclaim"),
                Map.of("settlement", settlement.getCleanName(),
                        "chunk", chunkCoords.toString()),
                messageProvider.get("prefix"));
    }

}

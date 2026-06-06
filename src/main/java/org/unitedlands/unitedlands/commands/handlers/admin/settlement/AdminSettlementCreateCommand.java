package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.utils.Messenger;

public class AdminSettlementCreateCommand extends SettlementAdminCommandHandler {

    public AdminSettlementCreateCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 1) {
            Messenger.sendMessage(player, messageProvider.get("admin.usage.settlement.create"),
                    null, messageProvider.get("prefix"));
            return;
        }
        
        if (!hasPermission(player)) {
            return;
        }

        var chunkCoords = CoordinateUtils.locationToChunkCoordinates(player.getLocation());
        var existingChunk = GlobalDataManager.instance().getSettlementChunk(chunkCoords);
        if (existingChunk != null) {
            Messenger.sendMessage(player, messageProvider.get("settlement.create.claimed"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var world = player.getLocation().getWorld();

        Settlement settlement = new Settlement();
        settlement.setUuid(UUID.randomUUID());
        settlement.setName(args[0]);
        settlement.setFoundingTimestamp(System.currentTimeMillis());
        settlement.setWorld(world);
        settlement.setHomeChunkCoordinates(chunkCoords);
        settlement.setSpawn(player.getLocation());

        var region = GlobalDataManager.instance()
                .getRegion(CoordinateUtils.locationToRegionCoordinates(player.getLocation()));
        if (region != null) {
            settlement.setRegion(region);
        }

        var chunk = new SettlementChunk();
        chunk.setUuid(UUID.randomUUID());
        chunk.setCoordinates(CoordinateUtils.locationToChunkCoordinates(player.getLocation()));
        chunk.setWorld(world);
        chunk.setClaimTimestamp(System.currentTimeMillis());
        chunk.setSettlement(settlement);

        settlement.addChunk(chunk);

        GlobalDataManager.instance().registerSettlement(settlement);
        GlobalDataManager.instance().registerSettlementChunk(chunk);

        GlobalDataManager.instance().createSettlementDbData(settlement);

        EconomyManager.instance().createAccount(settlement.getUuid(), settlement.getName());

        Messenger.sendMessage(player, messageProvider.get("admin.settlement.create"),
                Map.of("settlement", settlement.getCleanName()), messageProvider.get("prefix"));

        Pl3xMapRenderer.instance().renderSettlement(settlement);
    }

}

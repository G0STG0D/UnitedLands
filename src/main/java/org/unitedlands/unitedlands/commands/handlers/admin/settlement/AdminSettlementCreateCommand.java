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
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
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
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__CREATE__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }
        
        if (!hasPermission(player)) {
            return;
        }

        var chunkCoords = CoordinateUtils.locationToChunkCoordinates(player.getLocation());
        var existingChunk = UnitedLandsDataManager.instance().getSettlementChunk(chunkCoords);
        if (existingChunk != null) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__CREATE__ALREADY_CLAIMED.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
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

        var region = UnitedLandsDataManager.instance()
                .getRegion(CoordinateUtils.locationToChunkCenterCoordinates(player.getLocation()));
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

        UnitedLandsDataManager.instance().registerSettlement(settlement);
        UnitedLandsDataManager.instance().registerSettlementChunk(chunk);

        UnitedLandsDataManager.instance().createSettlementDbData(settlement);

        UnitedLandsEconomyManager.instance().createAccount(settlement.getUuid(), settlement.getName());

        Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__CREATE__SUCCESS.path()),
                Map.of("settlement", settlement.getCleanName()), messageProvider.get(Message.PREFIX.path()));

    }

}

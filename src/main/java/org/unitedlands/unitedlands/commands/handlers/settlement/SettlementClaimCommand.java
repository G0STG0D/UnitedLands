package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.unitedlands.utils.CostUtils;
import org.unitedlands.utils.Messenger;

public class SettlementClaimCommand extends SettlementCommandHandler {

    public SettlementClaimCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;
        var settlement = getCitizenSettlement(citizen);
        if (settlement == null)
            return;

        if (!hasPermission("settlement.claim", citizen))
            return;

        var chunkCoords = CoordinateUtils.locationToChunkCoordinates(player.getLocation());

        var existingChunk = GlobalDataManager.instance().getSettlementChunk(chunkCoords);
        if (existingChunk != null) {
            Messenger.sendMessage(player, messageProvider.get("settlement.claim.already-claimed"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var regionCoords = CoordinateUtils.locationToRegionCoordinates(player.getLocation());
        var region = GlobalDataManager.instance().getRegion(regionCoords);
        if (region != null) {
            if (!region.equals(settlement.getRegion()) && !Settings.allowTownClaimsOutsideHomeRegion) {
                Messenger.sendMessage(player, messageProvider.get("settlement.claim.outside-of-region"),
                        null, messageProvider.get("prefix"));
                return;
            }

            if (region.hasCountry()) {
                // TODO Country checks
            }
        }

        var claimCosts = CostUtils.getSettlementClaimCosts(settlement);
        if (!EconomyManager.instance().has(settlement.getUuid(), claimCosts)) {
            Messenger.sendMessage(player, messageProvider.get("settlement.no-funds"),
                    Map.of("amount", EconomyManager.instance().format(claimCosts)), messageProvider.get("prefix"));
            return;
        }

        var chunk = new SettlementChunk();

        chunk.setUuid(UUID.randomUUID());
        chunk.setCoordinates(chunkCoords);
        chunk.setWorld(player.getLocation().getWorld());
        chunk.setClaimTimestamp(System.currentTimeMillis());
        chunk.setSettlement(settlement);

        settlement.addChunk(chunk);

        GlobalDataManager.instance().createSettlementChunkDbData(chunk);

        EconomyManager.instance().withdraw(settlement.getUuid(), claimCosts);

        Pl3xMapRenderer.instance().removeSettlement(settlement);
        Pl3xMapRenderer.instance().renderSettlement(settlement);

        Messenger.sendMessage(player, messageProvider.get("settlement.claim.success"),
                Map.of("settlement", settlement.getCleanName(),
                        "chunk", chunkCoords.toString(),
                        "costs", EconomyManager.instance().format(claimCosts)),
                messageProvider.get("prefix"));
    }

}

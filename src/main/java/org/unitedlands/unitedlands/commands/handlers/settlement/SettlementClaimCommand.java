package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.events.settlement.SettlementPreClaimEvent;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
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

        var context = validate(sender, "settlement.claim");
        if (context == null)
            return;

        var chunkCoords = CoordinateUtils.locationToChunkCoordinates(context.player().getLocation());

        var existingChunk = UnitedLandsDataManager.instance().getSettlementChunk(chunkCoords);
        if (existingChunk != null) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__CLAIM__ALREADY_CLAIMED.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var region = UnitedLandsDataManager.instance()
                .getRegion(CoordinateUtils.locationToChunkCenterCoordinates(context.player().getLocation()));
        if (region != null) {
            if (!region.equals(context.settlement().getRegion()) && !Settings.allowTownClaimsOutsideHomeRegion) {
                Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__CLAIM__OUTSIDE_OF_REGION.path()),
                        null, messageProvider.get(Message.PREFIX.path()));
                return;
            }

            if (region.hasCountry()) {
                // If trying to claim in another country's region, check the claim whitelist
                if (!region.getCountry().equals(context.settlement().getCountry())) {
                    if (!region.getCountry().getSettlementClaimWhitelist().contains(context.settlement())) {
                        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__CLAIM__HAS_COUNTRY.path()),
                                Map.of("country", region.getCountry().getCleanName()), messageProvider.get(Message.PREFIX.path()));
                        return;
                    }
                }
            }
        }

        var claimCosts = CostUtils.getSettlementClaimCosts(context.settlement());
        if (!UnitedLandsEconomyManager.instance().has(context.settlement().getUuid(), claimCosts)) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.GENERAL_ERRORS__NO_FUNDS_SETTLEMENT.path()),
                    Map.of("amount", UnitedLandsEconomyManager.instance().format(claimCosts)),
                    messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var preEvent = new SettlementPreClaimEvent(context.settlement(), chunkCoords);
        preEvent.callEvent();
        if (preEvent.isCancelled())
            return;

        var chunk = new SettlementChunk();

        chunk.setUuid(UUID.randomUUID());
        chunk.setCoordinates(chunkCoords);
        chunk.setWorld(context.player().getLocation().getWorld());
        chunk.setClaimTimestamp(System.currentTimeMillis());
        chunk.setSettlement(context.settlement());

        context.settlement().addChunk(chunk);

        UnitedLandsDataManager.instance().createSettlementChunkDbData(chunk);

        UnitedLandsEconomyManager.instance().withdraw(context.settlement().getUuid(), claimCosts);

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__CLAIM__SUCCESS.path()),
                Map.of("settlement", context.settlement().getCleanName(),
                        "chunk", chunkCoords.toString(),
                        "costs", UnitedLandsEconomyManager.instance().format(claimCosts)),
                messageProvider.get(Message.PREFIX.path()));
    }

}

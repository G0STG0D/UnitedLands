package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.utils.Messenger;

public class SettlementCreateCommand extends SettlementCommandHandler {

    public SettlementCreateCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length < 1)
            return;
        if (args.length == 2 && !player.hasPermission("united.lands.admin"))
            return;

        var citizen = getCitizen(player);
        if (citizen == null)
            return;

        if (citizen.hasSettlement()) {
            Messenger.sendMessage(player, messageProvider.get("errors.already-in-settlement"),
                    Map.of("settlement", citizen.getSettlement().getCleanName()), messageProvider.get("prefix"));
            return;
        }

        var chunkCoords = CoordinateUtils.locationToChunkCoordinates(player.getLocation());
        var existingChunk = GlobalDataManager.instance().getSettlementChunk(chunkCoords);
        if (existingChunk != null) {
            Messenger.sendMessage(player, messageProvider.get("settlement.create.claimed"),
                    null, messageProvider.get("prefix"));
            return;
        }

        if (!EconomyManager.instance().has(citizen.getUuid(), new BigDecimal(Settings.settlementCreateCosts))) {
            Messenger.sendMessage(player, messageProvider.get("errors.no-funds"),
                    Map.of("amount", EconomyManager.instance().format(Settings.settlementCreateCosts)), messageProvider.get("prefix"));
            return;
        }

        var confirmation = new Confirmation("settlement");
        confirmation.setRunnable(() -> {

            var world = player.getLocation().getWorld();

            Settlement settlement = new Settlement();
            settlement.setUuid(UUID.randomUUID());
            settlement.setName(args[0]);
            settlement.setFounder(player);
            settlement.setFoundingTimestamp(System.currentTimeMillis());
            settlement.setWorld(world);
            settlement.setHomeChunkCoordinates(chunkCoords);
            settlement.setSpawn(player.getLocation());

            var region = GlobalDataManager.instance()
                    .getRegion(CoordinateUtils.locationToRegionCoordinates(player.getLocation()));
            var regionInfo = "no region";
            var countryInfo = "no country";
            if (region != null) {
                settlement.setRegion(region);
                regionInfo = region.getCleanName();
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

            settlement.addCitizen(citizen);
            GlobalDataManager.instance().createSettlementDbData(settlement);

            EconomyManager.instance().createAccount(settlement.getUuid(), settlement.getName());
            EconomyManager.instance().withdraw(citizen.getUuid(), Settings.settlementCreateCosts);

            citizen.setSettlement(settlement);
            citizen.addSettlementRank("mayor");
            GlobalDataManager.instance().updateCitizenDbData(citizen);

            Messenger.sendMessage(player, messageProvider.get("settlement.create.player"),
                    Map.of("settlement", settlement.getCleanName()), messageProvider.get("prefix"));
            Messenger.sendMessage(Bukkit.getServer(), messageProvider.get("settlement.create.broadcast"),
                    Map.of("player", player.getName(),
                            "settlement", settlement.getCleanName(),
                            "region", regionInfo,
                            "country", countryInfo),
                    messageProvider.get("prefix"));

            Pl3xMapRenderer.instance().renderSettlement(settlement);

        })
                .setAcceptCommand("/approve settlement")
                .setCancelCommand("/cancel settlement")
                .setSender(player)
                .setReceiver(player)
                .setDiscriminator(args[0])
                .setTimeoutSeconds(30)
                .setTitle("Create settlement with name " + args[0] + " at this location?")
                .send();
    }

}

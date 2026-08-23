package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlement.class,
        name = "merge",
        description = "Merges the target settlement into this settlement",
        usage = "/settlement merge <target_settlement>",
        playerOnly = true
)
public class CmdSettlementMerge extends SettlementCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {

        if (args.length == 1) {
            return UnitedLandsDataManager.instance().getSettlementNames();
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "settlement.merge");
        if (context == null)
            return;

        var targetSettlement = UnitedLandsDataManager.instance().getSettlement(args[0]);
        if (targetSettlement == null) {
            Messenger.sendMessage(sender, MessageProvider.instance().get(Message.GENERAL_ERRORS__SETTLEMENT_NOT_FOUND.path()),
                    Map.of("settlement", args[0]), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        var targetMayor = targetSettlement.getMayor();
        if (!targetMayor.getPlayer().isOnline()) {
            Messenger.sendMessage(sender, MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__MERGE__MAYOR_NOT_ONLINE.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (context.settlement().hasCountry()) {
            if (!context.settlement().getCountry().equals(targetSettlement.getCountry())) {
                Messenger.sendMessage(sender, MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__MERGE__TARGET_NOT_IN_COUNTRY.path()),
                        Map.of("settlement", context.settlement().getCleanName()), MessageProvider.instance().get(Message.PREFIX.path()));
                return;
            }
        } else {
            if (targetSettlement.hasCountry()) {
                Messenger.sendMessage(sender, MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__MERGE__TARGET_HAS_COUNTRY.path()),
                        Map.of("settlement", context.settlement().getCleanName()), MessageProvider.instance().get(Message.PREFIX.path()));
                return;
            }
        }

        Confirmation confirmation = new Confirmation("merge-settlement");
        confirmation.setRunnable(() -> {
            for (var chunk : targetSettlement.getChunks()) {
                context.settlement().addChunk(chunk);
                chunk.setSettlement(context.settlement());
                UnitedLandsDataManager.instance().updateSettlementChunkDbData(chunk);
            }
            for (var citizen : targetSettlement.getCitizens()) {
                citizen.setSettlement(context.settlement());
                citizen.removeSettlementRanks();
                UnitedLandsDataManager.instance().updateCitizenDbData(citizen);
            }

            if (targetSettlement.hasRegion()) {
                var region = targetSettlement.getRegion();
                region.removeSettlement(targetSettlement);
            }
            if (targetSettlement.hasCountry()) {
                var country = targetSettlement.getCountry();
                country.removeSettlement(targetSettlement);
            }

            UnitedLandsEconomyManager.instance().deposit(context.settlement().getUuid(),
                    UnitedLandsEconomyManager.instance().getBalance(targetSettlement.getUuid()),
                    "Merging of settlement bank of " + targetSettlement.getName());
            UnitedLandsEconomyManager.instance().deleteAccount(targetSettlement.getUuid());

            var targetSettlementName = targetSettlement.getCleanName();
            UnitedLandsDataManager.instance().removeSettlementDbData(targetSettlement);

            Pl3xMapRenderer.instance().removeSettlement(targetSettlement);

            Messenger.sendMessage(Bukkit.getServer(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__MERGE__BROADCAST.path()),
                    Map.of(
                            "settlement", context.settlement().getCleanName(),
                            "targetsettlement", targetSettlementName),
                    MessageProvider.instance().get(Message.PREFIX.path()));
        })
                .setTitle(MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__MERGE__CONFIRM.path()))
                .setReplacements(Map.of("settlement", context.settlement().getCleanName()))
                .setSender((Player) sender)
                .setReceiver(targetMayor.getPlayer())
                .send();

    }
}

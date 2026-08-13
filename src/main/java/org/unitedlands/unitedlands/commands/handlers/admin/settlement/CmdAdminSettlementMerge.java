package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminSettlement.class,
        name = "merge",
        description = "Merges the target settlement into the prevailing settlement",
        usage = "/settlement merge <prevailing_settlement> <dissolving_settlement>",
        catchAll = true
)
public class CmdAdminSettlementMerge implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sendUsage(sender);
            return;
        }

        var sourceSettlement = UnitedLandsDataManager.instance().getSettlement(args[0]);
        if (sourceSettlement == null) {
            Messenger.sendMessage(sender, MessageProvider.instance().get(Message.GENERAL_ERRORS__SETTLEMENT_NOT_FOUND.path()),
                    Map.of("settlement", args[0]), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        var targetSettlement = UnitedLandsDataManager.instance().getSettlement(args[1]);
        if (targetSettlement == null) {
            Messenger.sendMessage(sender, MessageProvider.instance().get(Message.GENERAL_ERRORS__SETTLEMENT_NOT_FOUND.path()),
                    Map.of("settlement", args[1]), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        for (var chunk : targetSettlement.getChunks()) {
            sourceSettlement.addChunk(chunk);
            chunk.setSettlement(sourceSettlement);
            UnitedLandsDataManager.instance().updateSettlementChunkDbData(chunk);
        }
        for (var citizen : targetSettlement.getCitizens()) {
            citizen.setSettlement(sourceSettlement);
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

        UnitedLandsEconomyManager.instance().deposit(sourceSettlement.getUuid(),
                UnitedLandsEconomyManager.instance().getBalance(targetSettlement.getUuid()));
        UnitedLandsEconomyManager.instance().deleteAccount(targetSettlement.getUuid());

        var targetSettlementName = targetSettlement.getCleanName();
        UnitedLandsDataManager.instance().removeSettlementDbData(targetSettlement);

        Pl3xMapRenderer.instance().removeSettlement(targetSettlement);

        Messenger.sendMessage(Bukkit.getServer(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__MERGE__BROADCAST.path()),
                Map.of(
                        "settlement", sourceSettlement.getCleanName(),
                        "targetsettlement", targetSettlementName),
                MessageProvider.instance().get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length <= 2) {
            return UnitedLandsDataManager.instance().getSettlementNames();
        }
        return null;
    }
}
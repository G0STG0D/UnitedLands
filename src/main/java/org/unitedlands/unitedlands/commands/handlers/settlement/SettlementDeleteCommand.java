package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementDeleteCommand extends SettlementCommandHandler {

    public SettlementDeleteCommand(UnitedLands plugin, IMessageProvider messageProvider) {
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

        if (!hasPermission("settlement.delete", citizen))
            return;

        if (settlement.hasCountry() && settlement.getCountry().getCapital().equals(settlement)) {
            Messenger.sendMessage(player, messageProvider.get("settlement.delete.is-capital"), null,
                    messageProvider.get("prefix"));
            return;
        }

        Confirmation leave = new Confirmation("settlement-delete");
        leave.setRunnable(() -> {

            if (settlement.hasRegion()) {
                var region = settlement.getRegion();
                region.removeSettlement(settlement);
            }

            if (settlement.hasCountry()) {
                var country = settlement.getCountry();
                country.removeSettlement(settlement);
            }

            for (var settlementCitizen : settlement.getCitizens()) {
                settlementCitizen.removeSettlement();
                settlementCitizen.removeSettlementRanks();
                settlementCitizen.removeCountryRanks();
                GlobalDataManager.instance().updateCitizenDbData(settlementCitizen);
            }

            EconomyManager.instance().deleteAccount(settlement.getUuid());

            GlobalDataManager.instance().removeSettlementDbData(settlement);

            Pl3xMapRenderer.instance().removeSettlement(settlement);

            Messenger.sendMessage(Bukkit.getServer(), messageProvider.get("settlement.delete.deleted-broadcast"),
                    Map.of("settlement", settlement.getCleanName()),
                    messageProvider.get("prefix"));
        })
                .setTitle("<red>Are you sure you want to delete <blue>" + settlement.getCleanName()
                        + "</blue>? <bold>This cannot be undone!</bold></red>")
                .setSender(player)
                .setReceiver(player)
                .setDiscriminator(settlement.getName())
                .setAcceptCommand("/approve settlement-delete")
                .setCancelCommand("/cancel settlement-delete")
                .setTimeoutSeconds(60)
                .send();
    }

}

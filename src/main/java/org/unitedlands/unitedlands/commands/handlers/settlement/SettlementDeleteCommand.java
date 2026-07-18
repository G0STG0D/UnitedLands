package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
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

        var context = validate(sender, "settlement.delete");
        if (context == null)
            return;
        
        if (context.settlement().hasCountry() && context.settlement().getCountry().getCapital().equals(context.settlement())) {
            Messenger.sendMessage(context.player(), messageProvider.get("settlement.delete.is-capital"), null,
                    messageProvider.get("prefix"));
            return;
        }

        Confirmation leave = new Confirmation("settlement-delete");
        leave.setRunnable(() -> {

            if (context.settlement().hasRegion()) {
                var region = context.settlement().getRegion();
                region.removeSettlement(context.settlement());
            }

            if (context.settlement().hasCountry()) {
                var country = context.settlement().getCountry();
                country.removeSettlement(context.settlement());
            }

            for (var settlementCitizen : context.settlement().getCitizens()) {
                settlementCitizen.removeSettlement();
                settlementCitizen.removeSettlementRanks();
                settlementCitizen.removeCountryRanks();
                UnitedLandsDataManager.instance().updateCitizenDbData(settlementCitizen);
            }

            UnitedLandsEconomyManager.instance().deleteAccount(context.settlement().getUuid());

            UnitedLandsDataManager.instance().removeSettlementDbData(context.settlement());

            Pl3xMapRenderer.instance().removeSettlement(context.settlement());

            Messenger.sendMessage(Bukkit.getServer(), messageProvider.get("settlement.delete.deleted-broadcast"),
                    Map.of("settlement", context.settlement().getCleanName()),
                    messageProvider.get("prefix"));
        })
                .setTitle(messageProvider.get("settlement.delete.confirm"))
                .setReplacements(Map.of("settlement", context.settlement().getCleanName()))
                .setSender(context.player())
                .setReceiver(context.player())
                .setDiscriminator(context.settlement().getName())
                .setAcceptCommand("/approve settlement-delete")
                .setCancelCommand("/cancel settlement-delete")
                .setTimeoutSeconds(60)
                .send();
    }

}

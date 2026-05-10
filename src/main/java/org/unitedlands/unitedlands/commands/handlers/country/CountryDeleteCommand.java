package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class CountryDeleteCommand extends CountryCommandHandler {

    public CountryDeleteCommand(UnitedLands plugin, IMessageProvider messageProvider) {
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
        var country = getCitizenCountry(citizen);
        if (country == null)
            return;

        if (!hasPermission("country.delete", citizen))
            return;

        Confirmation leave = new Confirmation("country-delete");
        leave.setRunnable(() -> {

            for (var region : country.getRegions()) {
                for (var settlement : region.getSettlements()) {
                    if (!settlement.hasCountry())
                        continue;
                    for (var settlementCitizen : settlement.getCitizens()) {
                        settlementCitizen.removeCountryRanks();
                        GlobalDataManager.instance().updateCitizenDbData(settlementCitizen);
                    }
                    settlement.removeCountry();
                    GlobalDataManager.instance().updateSettlementDbData(settlement);
                }

                region.removeCountry();
                GlobalDataManager.instance().updateRegionDbData(region);
            }

            GlobalDataManager.instance().removeCountryDbData(country);

            for (var region : country.getRegions()) {
                Pl3xMapRenderer.instance().renderRegion(region);
                for (var settlement : region.getSettlements()) {
                    Pl3xMapRenderer.instance().renderSettlement(settlement);
                }
            }
            Pl3xMapRenderer.instance().removeCountry(country);

            Messenger.sendMessage(Bukkit.getServer(), messageProvider.get("country.delete.deleted-broadcast"),
                    Map.of("country", country.getCleanName()),
                    messageProvider.get("prefix"));

        })
                .setTitle("<red>Are you sure you want to delete <green>" + country.getCleanName()
                        + "</green>? <bold>This cannot be undone!</bold></red>")
                .setSender(player)
                .setReceiver(player)
                .setDiscriminator(country.getName())
                .setAcceptCommand("/approve country-delete")
                .setCancelCommand("/cancel country-delete")
                .setTimeoutSeconds(60)
                .send();
    }

}

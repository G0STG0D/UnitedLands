package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;

import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdCountry.class,
        name = "delete",
        description = "Deletes a country",
        usage = "/country delete",
        playerOnly = true
)
public class CmdCountryDelete extends CountryCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, "country.delete");
        if (context == null)
            return;

        Confirmation leave = new Confirmation("country-delete");
        leave.setRunnable(() -> {

            for (var region : context.country().getRegions()) {
                for (var settlement : region.getSettlements()) {
                    if (!settlement.hasCountry())
                        continue;
                    for (var settlementCitizen : settlement.getCitizens()) {
                        settlementCitizen.removeCountryRanks();
                        settlementCitizen.save();
                    }
                    settlement.removeCountry();
                    settlement.saveAndRender();
                }
                region.removeCountry();
                region.saveAndRender();
            }

            UnitedLandsEconomyManager.instance().deleteAccount(context.country().getUuid());

            UnitedLandsDataManager.instance().removeCountryDbData(context.country());

            Pl3xMapRenderer.instance().removeCountry(context.country());

            United.messenger().send(Bukkit.getServer(), "player.country.delete.broadcast-message", context.country().getCleanName());

        })
                .setTitle(United.messenger().get("player.country.delete.delete-confirm", context.country().getCleanName()))
                .setSender(context.player())
                .setReceiver(context.player())
                .send();
    }

}

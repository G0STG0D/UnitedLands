package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
    parent          = CmdCountry.class,
    name            = "delete",
    description     = "Deletes a country",
    usage           = "/country delete",
    playerOnly      = true
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

        // TODO: move string to config
        Confirmation leave = new Confirmation("country-delete");
        leave.setRunnable(() -> {

            for (var region : context.country().getRegions()) {
                for (var settlement : region.getSettlements()) {
                    if (!settlement.hasCountry())
                        continue;
                    for (var settlementCitizen : settlement.getCitizens()) {
                        settlementCitizen.removeCountryRanks();
                        UnitedLandsDataManager.instance().updateCitizenDbData(settlementCitizen);
                    }
                    settlement.removeCountry();
                    UnitedLandsDataManager.instance().updateSettlementDbData(settlement);
                }
                region.removeCountry();
                UnitedLandsDataManager.instance().updateRegionDbData(region);
            }

            UnitedLandsEconomyManager.instance().deleteAccount(context.country().getUuid());

            UnitedLandsDataManager.instance().removeCountryDbData(context.country());

            Pl3xMapRenderer.instance().removeCountry(context.country());

            Messenger.sendMessage(Bukkit.getServer(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__DELETE__BROADCAST_MESSAGE.path()),
                    Map.of("country", context.country().getCleanName()),
                    MessageProvider.instance().get(Message.PREFIX.path()));

        })
                .setTitle("<red>Are you sure you want to delete <green>" + context.country().getCleanName()
                        + "</green>? <bold>This cannot be undone!</bold></red>")
                .setSender(context.player())
                .setReceiver(context.player())
                .send();
    }

}

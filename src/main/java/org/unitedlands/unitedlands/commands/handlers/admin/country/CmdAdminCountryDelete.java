package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminCountry.class,
        name = "delete",
        description = "Deletes a country",
        usage = "/ula country delete <country_name>"
)
public class CmdAdminCountryDelete extends CountryAdminCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var country = getCountry(sender, args[0]);
        if (country == null) {
            return;
        }

        for (var region : country.getRegions()) {
            for (var settlement : region.getSettlements()) {
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

        UnitedLandsEconomyManager.instance().deleteAccount(country.getUuid());

        UnitedLandsDataManager.instance().removeCountryDbData(country);

        Pl3xMapRenderer.instance().removeCountry(country);

        Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__COUNTRY__DELETE__SUCCESS.path()),
                Map.of("country", country.getName()), MessageProvider.instance().get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        if (args.length == 1) {
            return UnitedLandsDataManager.instance().getCountryNames();
        }
        return null;
    }

}

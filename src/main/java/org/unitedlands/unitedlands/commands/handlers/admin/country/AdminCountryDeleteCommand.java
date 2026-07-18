package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class AdminCountryDeleteCommand extends CountryAdminCommandHandler {

    public AdminCountryDeleteCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 1) {
            Messenger.sendMessage(player, messageProvider.get("admin.usage.country.delete"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var country = getCountry(player, args[0]);
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
                Pl3xMapRenderer.instance().renderSettlement(settlement);
            }
            region.removeCountry();
            UnitedLandsDataManager.instance().updateRegionDbData(region);
            
            Pl3xMapRenderer.instance().renderPolyRegion(region);
        }

        UnitedLandsEconomyManager.instance().deleteAccount(country.getUuid());

        UnitedLandsDataManager.instance().removeCountryDbData(country);

        Pl3xMapRenderer.instance().removeCountry(country);

        Messenger.sendMessage(player, messageProvider.get("admin.country.delete"),
                Map.of("country", country.getName()), messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        if (args.length == 1) {
            return UnitedLandsDataManager.instance().getCountryNames();
        }
        return null;
    }

}

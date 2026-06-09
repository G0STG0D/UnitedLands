package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
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

        EconomyManager.instance().deleteAccount(country.getUuid());

        GlobalDataManager.instance().unregisterCountry(country);
        GlobalDataManager.instance().removeCountryDbData(country);

        for (var region : country.getRegions()) {
            Pl3xMapRenderer.instance().renderRegion(region);
            for (var settlement : region.getSettlements()) {
                Pl3xMapRenderer.instance().renderSettlement(settlement);
            }
        }
        Pl3xMapRenderer.instance().removeCountry(country);

        Messenger.sendMessage(player, messageProvider.get("admin.country.delete"),
                Map.of("country", country.getName()), messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        if (args.length == 1) {
            return GlobalDataManager.instance().getCountryNames();
        }
        return null;
    }

}

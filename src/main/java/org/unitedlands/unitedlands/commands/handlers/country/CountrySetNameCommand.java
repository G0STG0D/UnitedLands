package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class CountrySetNameCommand extends CountryCommandHandler {

    public CountrySetNameCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1)
            return;

        var player = (Player) sender;
        var citizen = UnitedLandsDataManager.instance().getCitizen(player);
        if (citizen == null || citizen.getCountry() == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.not-in-country"),
                    null, messageProvider.get("prefix"));
            return;
        }

        if (!hasPermission("country.setname", citizen))
            return;

        var country = citizen.getCountry();
        country.setName(args[0]);

        UnitedLandsDataManager.instance().updateCountryDbData(country);

        for (var region : country.getRegions()) {
            Pl3xMapRenderer.instance().renderPolyRegion(region);
        }
        Pl3xMapRenderer.instance().renderCountry(country);
        for (var region : country.getRegions()) {
            for (var settlement : region.getSettlements()) {
                Pl3xMapRenderer.instance().renderSettlement(settlement);
            }
        }

        Messenger.sendMessage(player, messageProvider.get("country.setname.set"),
                Map.of("country", country.getCleanName()), messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        return null;
    }

}

package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.ColorUtils;
import org.unitedlands.utils.Messenger;

public class CountrySetColorCommand extends CountryCommandHandler {

    public CountrySetColorCommand(UnitedLands plugin, IMessageProvider messageProvider) {
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

        if (!hasPermission("country.setcolor", citizen))
            return;

        if (!(args[0].length() == 7) || !ColorUtils.isValidHexColor(args[0])) {
            Messenger.sendMessage(player, messageProvider.get("country.setcolor.wrong-format"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var country = citizen.getCountry();

        country.setFillColor(args[0] + "10");
        country.setStrokeColor(args[0]);

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

        Messenger.sendMessage(player, messageProvider.get("country.setcolor.set"),
                null, messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        return null;
    }

}

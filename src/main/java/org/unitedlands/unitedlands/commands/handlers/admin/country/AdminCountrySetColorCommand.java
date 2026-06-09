package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.ColorUtils;
import org.unitedlands.utils.Messenger;

public class AdminCountrySetColorCommand extends CountryAdminCommandHandler {

    public AdminCountrySetColorCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 2) {
            Messenger.sendMessage(player, messageProvider.get("admin.usage.country.setcolor"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var country = getCountry(player, args[0]);
        if (country == null) {
            return;
        }

        if (!(args[1].length() == 7) || !ColorUtils.isValidHexColor(args[1])) {
            Messenger.sendMessage(player, messageProvider.get("country.setcolor.wrong-format"),
                    null, messageProvider.get("prefix"));
            return;
        }

        country.setFillColor(args[1] + "10");
        country.setStrokeColor(args[1]);

        GlobalDataManager.instance().updateCountryDbData(country);

        for (var region : country.getRegions()) {
            Pl3xMapRenderer.instance().renderRegion(region);
        }
        Pl3xMapRenderer.instance().renderCountry(country);
        for (var region : country.getRegions()) {
            for (var settlement : region.getSettlements()) {
                Pl3xMapRenderer.instance().renderSettlement(settlement);
            }
        }

        Messenger.sendMessage(player, messageProvider.get("admin.country.setcolor"),
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

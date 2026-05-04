package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.ColorUtils;
import org.unitedlands.utils.Messenger;

public class CountrySetColorCommand extends BaseCommandHandler<UnitedLands> {

    public CountrySetColorCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1)
            return;

        var player = (Player) sender;
        var citizen = GlobalDataManager.instance().getCitizen(player);
        if (citizen == null || citizen.getCountry() == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.not-in-country"),
                    null, messageProvider.get("prefix"));
            return;
        }

        if (!(args[0].length() == 7) || !ColorUtils.isValidHexColor(args[0])) {
            Messenger.sendMessage(player, messageProvider.get("country.setcolor.wrong-format"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var country = citizen.getCountry();
        country.setFillColor(args[0] + "10");
        country.setStrokeColor(args[0]);

        GlobalDataManager.instance().updateCountryDbData(country);

        for (var region : country.getRegions()) {
            plugin.getMapRenderer().renderRegion(region);
        }
        plugin.getMapRenderer().renderCountry(country);

        Messenger.sendMessage(player, messageProvider.get("country.setcolor.success"),
                null, messageProvider.get("prefix"));
        return;
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        return null;
    }

}

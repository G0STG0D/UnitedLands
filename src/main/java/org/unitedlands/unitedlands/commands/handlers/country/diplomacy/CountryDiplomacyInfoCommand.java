package org.unitedlands.unitedlands.commands.handlers.country.diplomacy;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.classes.infoscreen.DiplomacyInfoScreen;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;

public class CountryDiplomacyInfoCommand extends CountryCommandHandler {

    public CountryDiplomacyInfoCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;

        Country country = null;
        if (args.length == 0) {
            country = getCitizenCountry(citizen);
            if (country == null)
                return;
        } else if (args.length >= 1) {
            country = UnitedLandsDataManager.instance().getCountry(args[0]);
            if (country == null) {
                return;
            }
        }

        var screen = new DiplomacyInfoScreen(plugin, messageProvider, country);
        screen.send(player);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

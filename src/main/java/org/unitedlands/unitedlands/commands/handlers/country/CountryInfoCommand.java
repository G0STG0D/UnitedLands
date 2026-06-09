package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.classes.infoscreen.CountryInfoScreen;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class CountryInfoCommand extends CountryCommandHandler {

    public CountryInfoCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return GlobalDataManager.instance().getCountryNames();
        return null;
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
            country = GlobalDataManager.instance().getCountry(args[0]);
            if (country == null) {
                return;
            }
        } 
        
        var screen = new CountryInfoScreen(plugin, messageProvider, country);
        if (screen.getComponents().size() > 0) {
            for (var component : screen.getComponents()) {
                Messenger.send(player, component.getContent());
            }
        }
    }
}

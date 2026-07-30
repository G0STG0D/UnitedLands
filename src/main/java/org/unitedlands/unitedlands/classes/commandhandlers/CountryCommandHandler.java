package org.unitedlands.unitedlands.classes.commandhandlers;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class CountryCommandHandler extends BaseCommandHandler<UnitedLands> {

    public record CountryCommandHandlerContext(Player player, Citizen citizen,
            Country country) {
    }

    public CountryCommandHandler(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender arg0, String[] arg1) {

    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] arg1) {
        return null;
    }

    protected CountryCommandHandlerContext validate(CommandSender sender, String permission) {

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return null;
        var country = getCitizenCountry(citizen);
        if (country == null)
            return null;

        if (permission != null)
            if (!hasPermission(permission, citizen))
                return null;

        return new CountryCommandHandlerContext(player, citizen, country);
    }

    protected Citizen getCitizen(Player player) {
        var citizen = UnitedLandsDataManager.instance().getCitizen(player);
        if (citizen == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.no-citizen-data"),
                    null, messageProvider.get("prefix"));
            return null;
        }
        return citizen;
    }

    protected Country getCitizenCountry(Citizen citizen) {
        if (citizen.getCountry() == null) {
            Messenger.sendMessage((Player) citizen.getPlayer(), messageProvider.get("errors.not-in-country"),
                    null, messageProvider.get("prefix"));
            return null;
        }
        return citizen.getCountry();
    }

    protected boolean hasPermission(String permission, Citizen citizen) {
        if (!plugin.getPermissionManager().hasRankPermission(permission, citizen)) {
            Messenger.sendMessage((Player) citizen.getPlayer(), messageProvider.get("errors.no-country-permission"),
                    Map.of("perm", permission), messageProvider.get("prefix"));
            return false;
        }
        return true;
    }

}

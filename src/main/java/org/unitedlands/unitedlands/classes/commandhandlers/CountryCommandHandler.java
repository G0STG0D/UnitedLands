package org.unitedlands.unitedlands.classes.commandhandlers;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

public class CountryCommandHandler implements UnitedCommandExecutor {

    public record CountryCommandHandlerContext(Player player, Citizen citizen,
            Country country) {
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
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__NO_CITIZEN_DATA.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return null;
        }
        return citizen;
    }

    protected Country getCitizenCountry(Citizen citizen) {
        if (citizen.getCountry() == null) {
            Messenger.sendMessage((Player) citizen.getPlayer(), MessageProvider.instance().get(Message.GENERAL_ERRORS__PLAYER_NOT_IN_COUNTRY.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return null;
        }
        return citizen.getCountry();
    }

    protected boolean hasPermission(String permission, Citizen citizen) {
        if (!UnitedLands.instance().getPermissionManager().hasRankPermission(permission, citizen)) {
            Messenger.sendMessage((Player) citizen.getPlayer(), MessageProvider.instance().get(Message.GENERAL_ERRORS__NO_COUNTRY_PERMISSION.path()),
                    Map.of("perm", permission), MessageProvider.instance().get(Message.PREFIX.path()));
            return false;
        }
        return true;
    }

}

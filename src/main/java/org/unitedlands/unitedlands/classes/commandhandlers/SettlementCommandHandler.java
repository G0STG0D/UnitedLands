package org.unitedlands.unitedlands.classes.commandhandlers;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

public class SettlementCommandHandler implements UnitedCommandExecutor {

    public record SettlementCommandHandlerContext(Player player, Citizen citizen,
            Settlement settlement) {
    }

    @Override
    public void handleCommand(CommandSender arg0, String[] arg1) {

    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] arg1) {
        return null;
    }

    protected SettlementCommandHandlerContext validate(CommandSender sender, String permission) {

        var player = (Player) sender;
        
        if (!Settings.worlds.contains(player.getLocation().getWorld().getName())) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__WRONG_WORLD.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return null;
        }
        
        var citizen = getCitizen(player);
        if (citizen == null)
            return null;
        var settlement = getCitizenSettlement(citizen);
        if (settlement == null)
            return null;

        if (permission != null)
            if (!hasPermission(permission, citizen))
                return null;

        return new SettlementCommandHandlerContext(player, citizen, settlement);
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

    protected Settlement getCitizenSettlement(Citizen citizen) {
        if (citizen.getSettlement() == null) {
            Messenger.sendMessage((Player) citizen.getPlayer(), MessageProvider.instance().get(Message.GENERAL_ERRORS__NOT_IN_SETTLEMENT.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return null;
        }
        return citizen.getSettlement();
    }

    protected boolean hasPermission(String permission, Citizen citizen) {
        if (!UnitedLands.instance().getPermissionManager().hasRankPermission(permission, citizen)) {
            Messenger.sendMessage((Player) citizen.getPlayer(), MessageProvider.instance().get(Message.GENERAL_ERRORS__NO_SETTLEMENT_PERMISSION.path()),
                    Map.of("perm", permission), MessageProvider.instance().get(Message.PREFIX.path()));
            return false;
        }
        return true;
    }

}

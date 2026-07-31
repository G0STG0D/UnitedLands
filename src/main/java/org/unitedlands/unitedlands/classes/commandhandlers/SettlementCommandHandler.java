package org.unitedlands.unitedlands.classes.commandhandlers;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementCommandHandler extends BaseCommandHandler<UnitedLands> {

    public record SettlementCommandHandlerContext(Player player, Citizen citizen,
            Settlement settlement) {
    }

    public SettlementCommandHandler(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
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
            Messenger.sendMessage(player, messageProvider.get(Message.GENERAL_ERRORS__NO_CITIZEN_DATA.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return null;
        }
        return citizen;
    }

    protected Settlement getCitizenSettlement(Citizen citizen) {
        if (citizen.getSettlement() == null) {
            Messenger.sendMessage((Player) citizen.getPlayer(), messageProvider.get(Message.GENERAL_ERRORS__NOT_IN_SETTLEMENT.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return null;
        }
        return citizen.getSettlement();
    }

    protected boolean hasPermission(String permission, Citizen citizen) {
        if (!plugin.getPermissionManager().hasRankPermission(permission, citizen)) {
            Messenger.sendMessage((Player) citizen.getPlayer(), messageProvider.get(Message.GENERAL_ERRORS__NO_SETTLEMENT_PERMISSION.path()),
                    Map.of("perm", permission), messageProvider.get(Message.PREFIX.path()));
            return false;
        }
        return true;
    }

}

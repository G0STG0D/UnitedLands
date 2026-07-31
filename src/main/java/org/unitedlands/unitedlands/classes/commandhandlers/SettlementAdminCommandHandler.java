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
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.Messenger;

public class SettlementAdminCommandHandler extends BaseCommandHandler<UnitedLands> {

    public SettlementAdminCommandHandler(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender arg0, String[] arg1) {

    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] arg1) {
        return null;
    }

    protected Settlement getSettlement(CommandSender player, String name) {
        var settlement = UnitedLandsDataManager.instance().getSettlement(name);
        if (settlement == null) {
            Messenger.sendMessage(player, messageProvider.get(Message.GENERAL_ERRORS__SETTLEMENT_NOT_FOUND.path()),
                    Map.of("settlement", name), messageProvider.get(Message.PREFIX.path()));
            return null;
        }
        return settlement;
    }

    protected Citizen getCitizen(CommandSender player, String name) {
        var citizen = UnitedLandsDataManager.instance().getCitizen(name);
        if (citizen == null) {
            Messenger.sendMessage(player, messageProvider.get(Message.GENERAL_ERRORS__CITIZEN_NOT_FOUND.path()),
                    Map.of("name", name), messageProvider.get(Message.PREFIX.path()));
            return null;
        }
        return citizen;
    }

    protected boolean hasPermission(Player player) {
        if (!PermissionManager.instance().hasGlobalOverrides(player)) {
            Messenger.sendMessage((Player) player, messageProvider.get(Message.GENERAL_ERRORS__NO_PERMISSION.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return false;
        }
        return true;
    }

}

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

    protected Settlement getSettlement(Player player, String name) {
        var settlement = UnitedLandsDataManager.instance().getSettlement(name);
        if (settlement == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.settlement-not-found"),
                    Map.of("settlement", name), messageProvider.get("prefix"));
            return null;
        }
        return settlement;
    }

    protected Citizen getCitizen(Player player, String name) {
        var citizen = UnitedLandsDataManager.instance().getCitizen(name);
        if (citizen == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.citizen-not-found"),
                    Map.of("name", name), messageProvider.get("prefix"));
            return null;
        }
        return citizen;
    }

    protected boolean hasPermission(Player player) {
        if (!PermissionManager.instance().hasGlobalOverrides(player)) {
            Messenger.sendMessage((Player) player, messageProvider.get("errors.no-permission"),
                    null, messageProvider.get("prefix"));
            return false;
        }
        return true;
    }

}

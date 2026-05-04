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
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementCommandHandler extends BaseCommandHandler<UnitedLands> {

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

    protected Citizen getCitizen(Player player) {
        var citizen = GlobalDataManager.instance().getCitizen(player);
        if (citizen == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.no-citizen-data"),
                    null, messageProvider.get("prefix"));
            return null;
        }
        return citizen;
    }

    protected Settlement getCitizenSettlement(Citizen citizen) {
        if (citizen.getSettlement() == null) {
            Messenger.sendMessage((Player) citizen.getPlayer(), messageProvider.get("errors.not-in-settlement"),
                    null, messageProvider.get("prefix"));
            return null;
        }
        return citizen.getSettlement();
    }

    protected boolean hasPermission(String permission, Citizen citizen) {
        if (!plugin.getPermissionManager().hasRankPermission(permission, citizen)) {
            Messenger.sendMessage((Player) citizen.getPlayer(), messageProvider.get("errors.no-settlement-permission"),
                    Map.of("perm", permission), messageProvider.get("prefix"));
            return false;
        }
        return true;
    }

}

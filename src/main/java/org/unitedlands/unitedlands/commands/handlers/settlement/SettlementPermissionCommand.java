package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementPermissionCommand extends SettlementCommandHandler {


    public SettlementPermissionCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    List<String> permissions = List.of("break", "place", "containers", "switch", "block_use", "interact");
    List<String> memberships = List.of("town_residents", "region_residents", "country_residents", "foreigners");
    List<String> switches = List.of("on", "off");

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return permissions;
            case 2:
                return memberships;
            case 3:
                return switches;
            default:
                return null;
        }
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 3)
            // TODO: Usage info
            return;

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;
        var settlement = getCitizenSettlement(citizen);
        if (settlement == null)
            return;

        if (!hasPermission("settlement.manage.perms", citizen))
            return;

        int membership = 0;
        switch (args[1]) {
            case "town_residents":
                membership = LocationMembership.SETTLEMENT_RESIDENT;
                break;
            case "region_residents":
                membership = LocationMembership.REGION_RESIDENT;
                break;
            case "country_residents":
                membership = LocationMembership.NATION_RESIDENT;
                break;
            case "foreigners":
                membership = LocationMembership.FOREIGNER;
                break;
            default:
                Messenger.sendMessage(player, messageProvider.get("settlement.permission.unknown-membership"),
                        Map.of("membership", args[1]), messageProvider.get("prefix"));
                return;
        }

        boolean add = args[2].equalsIgnoreCase("on");

        int p = 0;
        switch (args[0]) {
            case "break":
                p = settlement.getBreakPermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                settlement.setBreakPermissions(p);
                break;
            case "place":
                p = settlement.getPlacePermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                settlement.setPlacePermissions(p);
                break;
            case "containers":
                p = settlement.getContainerPermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                settlement.setContainerPermissions(p);
                break;
            case "switch":
                p = settlement.getSwitchPermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                settlement.setSwitchPermissions(p);
                break;
            case "block_use":
                p = settlement.getBlockUsePermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                settlement.setBlockUsePermissions(p);
                break;
            case "interact":
                p = settlement.getInteractPermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                settlement.setInteractPermissions(p);
                break;
            default:
                Messenger.sendMessage(player, messageProvider.get("settlement.permission.unknown-permission"),
                        Map.of("permission", args[0]), messageProvider.get("prefix"));
                return;
        }

        Messenger.sendMessage(player, messageProvider.get("settlement.permission.set"), Map.of(
                "permission", args[0],
                "member", args[0],
                "state", add ? "<green>on</green>" : "<red>off</red>"),
                messageProvider.get("prefix"));

        GlobalDataManager.instance().updateSettlementDbData(settlement);
    }

}

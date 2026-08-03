package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementPermissionCommand extends SettlementCommandHandler {


    public SettlementPermissionCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    List<String> permissions = List.of("break", "place", "containers", "switch", "block_use", "interact");
    List<String> memberships = List.of("settlement_residents", "region_residents", "country_residents", "foreigners");
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

        if (args.length != 3) {
            Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__SETTLEMENT__PERMISSION__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }
        var context = validate(sender, "settlement.manage.perms");
        if (context == null)
            return;
        
        int membership = 0;
        switch (args[1]) {
            case "settlement_residents":
                membership = LocationMembership.SETTLEMENT_RESIDENT;
                break;
            case "region_residents":
                membership = LocationMembership.REGION_RESIDENT;
                break;
            case "country_residents":
                membership = LocationMembership.COUNTRY_RESIDENT;
                break;
            case "foreigners":
                membership = LocationMembership.FOREIGNER;
                break;
            default:
                Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__PERMISSION__UNKNOWN_MEMBERSHIP.path()),
                        Map.of("membership", args[1]), messageProvider.get(Message.PREFIX.path()));
                return;
        }

        boolean add = args[2].equalsIgnoreCase("on");

        int p = 0;
        switch (args[0]) {
            case "break":
                p = context.settlement().getBreakPermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                context.settlement().setBreakPermissions(p);
                break;
            case "place":
                p = context.settlement().getPlacePermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                context.settlement().setPlacePermissions(p);
                break;
            case "containers":
                p = context.settlement().getContainerPermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                context.settlement().setContainerPermissions(p);
                break;
            case "switch":
                p = context.settlement().getSwitchPermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                context.settlement().setSwitchPermissions(p);
                break;
            case "block_use":
                p = context.settlement().getBlockUsePermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                context.settlement().setBlockUsePermissions(p);
                break;
            case "interact":
                p = context.settlement().getInteractPermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                context.settlement().setInteractPermissions(p);
                break;
            default:
                Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__PERMISSION__UNKNOWN_PERMISSION.path()),
                        Map.of("permission", args[0]), messageProvider.get(Message.PREFIX.path()));
                return;
        }

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__PERMISSION__SUCCESS.path()), Map.of(
                "permission", args[0],
                "membership", args[0],
                "state", add ? "<green>on</green>" : "<red>off</red>"),
                messageProvider.get(Message.PREFIX.path()));

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());
    }

}

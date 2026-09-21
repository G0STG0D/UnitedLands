package org.unitedlands.unitedlands.commands.handlers.region;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.commandhandlers.RegionCommandHandler;

import org.unitedlands.utils.United;

@UnitedSubCommand(
    parent          = CmdRegion.class,
    name            = "permission",
    usage           = "/region permission <permission> <group> <on|off>",
    playerOnly      = true,
    catchAll        = true
)
public class CmdRegionPermission extends RegionCommandHandler {

    List<String> permissions = List.of("break", "place", "containers", "switch", "block_use", "interact");
    List<String> memberships = List.of("region_residents", "country_residents", "foreigners");
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
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "region.manage.perms");
        if (context == null)
            return;
        
        int membership = 0;
        switch (args[1]) {
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
                United.messenger().send(context.player(), "player.region.permission.unknown-membership", args[1]);
                return;
        }

        boolean add = args[2].equalsIgnoreCase("on");

        int p = 0;
        switch (args[0]) {
            case "break":
                p = context.region().getBreakPermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                context.region().setBreakPermissions(p);
                break;
            case "place":
                p = context.region().getPlacePermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                context.region().setPlacePermissions(p);
                break;
            case "containers":
                p = context.region().getContainerPermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                context.region().setContainerPermissions(p);
                break;
            case "switch":
                p = context.region().getSwitchPermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                context.region().setSwitchPermissions(p);
                break;
            case "block_use":
                p = context.region().getBlockUsePermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                context.region().setBlockUsePermissions(p);
                break;
            case "interact":
                p = context.region().getInteractPermissions();
                if (add)
                    p |= membership;
                else
                    p &= ~membership;
                context.region().setInteractPermissions(p);
                break;
            default:
                United.messenger().send(context.player(), "player.region.permission.unknown-permission", args[0]);
                return;
        }

        context.region().saveAndRender();

        United.messenger().send(context.player(), "player.region.permission.success", args[0], args[1], add ? "<green>on</green>" : "<red>off</red>");
    }

}

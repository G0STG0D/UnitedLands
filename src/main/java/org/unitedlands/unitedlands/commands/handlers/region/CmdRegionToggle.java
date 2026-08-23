package org.unitedlands.unitedlands.commands.handlers.region;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.RegionCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
    parent          = CmdRegion.class,
    name            = "toggle",
    usage           = "/region toggle <toggle> <on|off>",
    playerOnly      = true,
    catchAll        = true
)
public class CmdRegionToggle extends RegionCommandHandler {

    List<String> fields = List.of("pvp", "monsters", "animals", "fire", "explosions");
    List<String> switches = List.of("on", "off");

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        switch (args.length) {
        case 1:
            return fields;
        case 2:
            return switches;
        default:
            return null;
        }
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "region.manage.toggle");
        if (context == null)
            return;

        Boolean enable = null;
        if (args[1].equalsIgnoreCase("on")) {
            enable = true;
        } else {
            enable = false;
        }

        switch (args[0]) {
        case "pvp":
            context.region().setAllowPvp(enable);
            break;
        case "monsters":
            context.region().setAllowMonsters(enable);
            break;
        case "animals":
            context.region().setAllowAnimals(enable);
            break;
        case "fire":
            context.region().setAllowFire(enable);
            break;
        case "explosions":
            context.region().setAllowExplosions(enable);
            break;
        default:
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__REGION__TOGGLE__UNKNOWN_TOGGLE.path()),
                    Map.of("toggle", args[0]), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        UnitedLandsDataManager.instance().updateRegionDbData(context.region(), false);

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__REGION__TOGGLE__SUCCESS.path()), Map.of(
                "field", args[0],
                "state",
                enable != null ? (enable == true ? "<green>on</green>" : "<red>off</red>") : "<yellow>unset</yellow>"),
                MessageProvider.instance().get(Message.PREFIX.path()));
    }

}

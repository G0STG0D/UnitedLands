package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlement.class,
        name = "toggle",
        description = "Sets settlement toggles",
        usage = "/settlement toggle <toggle> <on|off>",
        catchAll = true,
        playerOnly = true
)
public class CmdSettlementToggle extends SettlementCommandHandler {

    List<String> fields = List.of("pvp", "monsters", "animals", "fire", "explosions", "public");
    List<String> switches = List.of("on", "off", "unset");

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

        var context = validate(sender, "settlement.manage.toggle");
        if (context == null)
            return;

        Boolean enable = null;
        if (args[1].equalsIgnoreCase("on")) {
            enable = true;
        } else if (args[1].equalsIgnoreCase("off")) {
            enable = false;
        }

        switch (args[0]) {
            case "public":
                // public can't be inherited, enforce value
                if (enable == null)
                    enable = false;
                context.settlement().setPublic(enable);
                break;
            case "pvp":
                context.settlement().setAllowPvp(enable);
                break;
            case "monsters":
                context.settlement().setAllowMonsters(enable);
                break;
            case "animals":
                context.settlement().setAllowAnimals(enable);
                break;
            case "fire":
                context.settlement().setAllowFire(enable);
                break;
            case "explosions":
                context.settlement().setAllowExplosions(enable);
                break;
            default:
                Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__TOGGLE__UNKNOWN_TOGGLE.path()),
                        Map.of("toggle", args[0]), MessageProvider.instance().get(Message.PREFIX.path()));
                return;
        }

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement(), true);

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__TOGGLE__SUCCESS.path()), Map.of(
                "field", args[0],
                "state",
                enable != null ? (enable == true ? "<green>on</green>" : "<red>off</red>") : "<yellow>unset</yellow>"),
                MessageProvider.instance().get(Message.PREFIX.path()));
    }

}

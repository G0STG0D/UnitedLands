package org.unitedlands.unitedlands.commands.handlers.settlementchunk;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementChunkCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementChunkToggleCommand extends SettlementChunkCommandHandler {

    public SettlementChunkToggleCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    List<String> fields = List.of("pvp", "monsters", "animals", "fire", "explosions");
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

        if (args.length != 2)
            return;

        var context = validate(sender, "settlement.plot.toggle");
        if (context == null)
            return;
        
        @Nullable
        Boolean enable = null;
        if (args[1].equalsIgnoreCase("on")) {
            enable = true;
        } else if (args[1].equalsIgnoreCase("off")) {
            enable = false;
        }

        switch (args[0]) {
            case "pvp":
                context.settlementChunk().setAllowPvp(enable);
                break;
            case "monsters":
                context.settlementChunk().setAllowMonsters(enable);
                break;
            case "animals":
                context.settlementChunk().setAllowAnimals(enable);
                break;
            case "fire":
                context.settlementChunk().setAllowFire(enable);
                break;
            case "explosions":
                context.settlementChunk().setAllowExplosions(enable);
                break;
            default:
                Messenger.sendMessage(context.player(), messageProvider.get("settlementchunk.toggle.unknown-toggle"),
                        Map.of("toggle", args[0]), messageProvider.get("prefix"));
                return;
        }

        UnitedLandsDataManager.instance().updateSettlementChunkDbData(context.settlementChunk());

        Messenger.sendMessage(context.player(), messageProvider.get("settlementchunk.toggle.set"), Map.of(
                "field", args[0],
                "state",
                enable != null ? (enable == true ? "<green>on</green>" : "<red>off</red>") : "<yellow>unset</yellow>"),
                messageProvider.get("prefix"));
    }

}

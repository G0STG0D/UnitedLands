package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementToggleCommand extends SettlementCommandHandler {

    public SettlementToggleCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

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

        if (args.length != 2)
            return;

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;
        var settlement = getCitizenSettlement(citizen);
        if (settlement == null)
            return;

        if (!hasPermission("settlement.manage.toggle", citizen))
            return;
        
        @Nullable
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
                settlement.setPublic(enable);
                break;
            case "pvp":
                settlement.setAllowPvp(enable);
                break;
            case "monsters":
                settlement.setAllowMonsters(enable);
                break;
            case "animals":
                settlement.setAllowAnimals(enable);
                break;
            case "fire":
                settlement.setAllowFire(enable);
                break;
            case "explosions":
                settlement.setAllowExplosions(enable);
                break;
            default:
                Messenger.sendMessage(player, messageProvider.get("settlement.toggle.unknown-toggle"),
                        Map.of("toggle", args[0]), messageProvider.get("prefix"));
                return;
        }

        GlobalDataManager.instance().updateSettlementDbData(settlement);

        Messenger.sendMessage(player, messageProvider.get("settlement.toggle.set"), Map.of(
                "field", args[0],
                "state",
                enable != null ? (enable == true ? "<green>on</green>" : "<red>off</red>") : "<yellow>unset</yellow>"),
                messageProvider.get("prefix"));
    }

}

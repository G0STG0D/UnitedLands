package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.DisplayManager;

@UnitedSubCommand(
        parent = CmdSettlement.class,
        name = "map",
        description = "Shows the settlement claims map",
        usage = "/settlement map",
        playerOnly = true
)
public class CmdSettlementMap extends SettlementCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {
        var player = (Player) sender;
        if (DisplayManager.instance().isPlayerViewingMap(player)) {
            DisplayManager.instance().hideMap(player);
        } else {
            DisplayManager.instance().showMap(player, player.getLocation());
        }
    }
}

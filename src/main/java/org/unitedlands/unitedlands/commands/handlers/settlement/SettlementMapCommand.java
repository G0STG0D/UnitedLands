package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.DisplayManager;

public class SettlementMapCommand extends SettlementCommandHandler {

    public SettlementMapCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

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

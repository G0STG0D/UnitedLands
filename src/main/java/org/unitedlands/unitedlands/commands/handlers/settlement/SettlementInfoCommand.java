package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.infoscreen.SettlementInfoScreen;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementInfoCommand extends SettlementCommandHandler {

    public SettlementInfoCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 2)
            return GlobalDataManager.instance().getSettlementNames();
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;

        Settlement settlement = null;
        if (args.length == 0) {
            settlement = getCitizenSettlement(citizen);
            if (settlement == null)
                return;
        } else if (args.length >= 1) {
            settlement = GlobalDataManager.instance().getSettlement(args[0]);
            if (settlement == null) {
                return;
            }
        }

        var screen = new SettlementInfoScreen(plugin, messageProvider, settlement);
        if (screen.getComponents().size() > 0) {
            for (var component : screen.getComponents()) {
                Messenger.send(player, component.getContent());
            }
        }
    }
}

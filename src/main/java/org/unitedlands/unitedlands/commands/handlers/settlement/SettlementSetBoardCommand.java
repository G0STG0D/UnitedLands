package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementSetBoardCommand extends SettlementCommandHandler {

    public SettlementSetBoardCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length == 0)
            // TODO: Usage
            return;

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;
        var settlement = getCitizenSettlement(citizen);
        if (settlement == null)
            return;

        if (!hasPermission("settlement.setboard", citizen))
            return;

        String message = "";
        if (args[0].equalsIgnoreCase("EMPTY")) {
            settlement.setTownBoard(null);
            message = messageProvider.get("settlement.setboard.cleared");
        } else {
            settlement.setTownBoard(String.join(" ", args));
            message = messageProvider.get("settlement.setboard.set");
        }

        GlobalDataManager.instance().updateSettlementDbData(settlement);

        Messenger.sendMessage(player, message,
                null, messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.utils.Messenger;

public class SettlementWithdrawCommand extends SettlementCommandHandler {

    public SettlementWithdrawCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1)
            // TODO: Usage
            return;

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;
        var settlement = getCitizenSettlement(citizen);
        if (settlement == null)
            return;

        if (!hasPermission("settlement.withdraw", citizen))
            return;

        BigDecimal amount = new BigDecimal(0);
        try {
            amount = new BigDecimal(args[0]);
        } catch (Exception ex) {
            Messenger.sendMessage(player, messageProvider.get("errors.wrong-number-format"),
                    Map.of("input", args[1]), messageProvider.get("prefix"));
            return;
        }

        if (!EconomyManager.instance().has(settlement.getUuid(), amount)) {
            Messenger.sendMessage(player, messageProvider.get("settlement.no-funds"),
                    Map.of("amount", EconomyManager.instance().format(amount)), messageProvider.get("prefix"));
            return;
        }

        EconomyManager.instance().withdraw(settlement.getUuid(), amount);
        EconomyManager.instance().deposit(player.getUniqueId(), amount);

        Messenger.sendMessage(player, messageProvider.get("settlement.withdraw"),
                Map.of("amount", EconomyManager.instance().format(amount)), messageProvider.get("prefix"));
    }

}

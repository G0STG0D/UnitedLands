package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.utils.Messenger;

public class SettlementDepositCommand extends SettlementCommandHandler {

    public SettlementDepositCommand(UnitedLands plugin, IMessageProvider messageProvider) {
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

        var context = validate(sender, null);
        if (context == null)
            return;
        
        BigDecimal amount = new BigDecimal(0);
        try {
            amount = new BigDecimal(args[0]);
        } catch (Exception ex) {
            Messenger.sendMessage(context.player(), messageProvider.get("errors.wrong-number-format"),
                    Map.of("input", args[1]), messageProvider.get("prefix"));
            return;
        }

        if (!UnitedLandsEconomyManager.instance().has(context.player().getUniqueId(), amount)) {
            Messenger.sendMessage(context.player(), messageProvider.get("errors.no-funds"),
                    Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)), messageProvider.get("prefix"));
            return;
        }

        UnitedLandsEconomyManager.instance().withdraw(context.player().getUniqueId(), amount);
        UnitedLandsEconomyManager.instance().deposit(context.settlement().getUuid(), amount);

        Messenger.sendMessage(context.player(), messageProvider.get("settlement.deposit"),
                Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)), messageProvider.get("prefix"));
    }

}

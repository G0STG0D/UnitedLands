package org.unitedlands.unitedlands.commands.handlers.country;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.utils.Messenger;

public class CountryWithdrawCommand extends CountryCommandHandler {

    public CountryWithdrawCommand(UnitedLands plugin, IMessageProvider messageProvider) {
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

        var context = validate(sender, "country.withdraw");
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

        if (!UnitedLandsEconomyManager.instance().has(context.country().getUuid(), amount)) {
            Messenger.sendMessage(context.player(), messageProvider.get("country.no-funds"),
                    Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)), messageProvider.get("prefix"));
            return;
        }

        UnitedLandsEconomyManager.instance().withdraw(context.country().getUuid(), amount);
        UnitedLandsEconomyManager.instance().deposit(context.player().getUniqueId(), amount);

        Messenger.sendMessage(context.player(), messageProvider.get("country.withdraw"),
                Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)), messageProvider.get("prefix"));
    }

}

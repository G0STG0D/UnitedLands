package org.unitedlands.unitedlands.commands.handlers.country;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
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

        var player = (Player) sender;
        var citizen = GlobalDataManager.instance().getCitizen(player);
        if (citizen == null || citizen.getCountry() == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.not-in-country"),
                    null, messageProvider.get("prefix"));
            return;
        }
        
        if (!hasPermission("country.withdraw", citizen))
            return;

        var country = citizen.getCountry();

        BigDecimal amount = new BigDecimal(0);
        try {
            amount = new BigDecimal(args[0]);
        } catch (Exception ex) {
            Messenger.sendMessage(player, messageProvider.get("errors.wrong-number-format"),
                    Map.of("input", args[1]), messageProvider.get("prefix"));
            return;
        }

        if (!EconomyManager.instance().has(country.getUuid(), amount)) {
            Messenger.sendMessage(player, messageProvider.get("country.no-funds"),
                    Map.of("amount", EconomyManager.instance().format(amount)), messageProvider.get("prefix"));
            return;
        }

        EconomyManager.instance().withdraw(country.getUuid(), amount);
        EconomyManager.instance().deposit(player.getUniqueId(), amount);

        Messenger.sendMessage(player, messageProvider.get("country.withdraw"),
                Map.of("amount", EconomyManager.instance().format(amount)), messageProvider.get("prefix"));
    }

}

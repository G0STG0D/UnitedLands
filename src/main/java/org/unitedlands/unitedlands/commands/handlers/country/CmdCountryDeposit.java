package org.unitedlands.unitedlands.commands.handlers.country;

import java.math.BigDecimal;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;

import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdCountry.class,
        name = "deposit",
        description = "Deposits money in the country bank account",
        usage = "/country deposit <amount>",
        playerOnly = true
)
public class CmdCountryDeposit extends CountryCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var player = (Player) sender;
        var citizen = UnitedLandsDataManager.instance().getCitizen(player);
        if (citizen == null || citizen.getCountry() == null) {
            United.messenger().send(player, "general-errors.not-in-country");
            return;
        }

        var country = citizen.getCountry();

        BigDecimal amount = new BigDecimal(0);
        try {
            amount = new BigDecimal(args[0]);
        } catch (Exception ex) {
            United.messenger().send(player, "general-errors.wrong-number-format", args[1]);
            return;
        }

        if (!UnitedLandsEconomyManager.instance().has(player.getUniqueId(), amount)) {
            United.messenger().send(player, "general-errors.no-funds-player", UnitedLandsEconomyManager.instance().format(amount));
            return;
        }

        UnitedLandsEconomyManager.instance().withdraw(player.getUniqueId(), amount, "Deposit to " + country.getName());
        UnitedLandsEconomyManager.instance().deposit(country.getUuid(), amount, "Deposit by " + player.getName());

        United.messenger().send(player, "player.country.deposit.success", UnitedLandsEconomyManager.instance().format(amount));
    }

}

package org.unitedlands.unitedlands.commands.handlers.country;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
    parent          = CmdCountry.class,
    name            = "deposit",
    description     = "Deposits money in the country bank account",
    usage           = "/country deposit <amount>",
    playerOnly      = true
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
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__NOT_IN_COUNTRY.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        var country = citizen.getCountry();

        BigDecimal amount = new BigDecimal(0);
        try {
            amount = new BigDecimal(args[0]);
        } catch (Exception ex) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__WRONG_NUMBER_FORMAT.path()),
                    Map.of("input", args[1]), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (!UnitedLandsEconomyManager.instance().has(player.getUniqueId(), amount)) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__NO_FUNDS_PLAYER.path()),
                    Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        UnitedLandsEconomyManager.instance().withdraw(player.getUniqueId(), amount, "Deposit to " + country.getName());
        UnitedLandsEconomyManager.instance().deposit(country.getUuid(), amount, "Deposit by " + player.getName());

        Messenger.sendMessage(player, MessageProvider.instance().get(Message.PLAYER__COUNTRY__DEPOSIT__SUCCESS.path()),
                Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)),
                MessageProvider.instance().get(Message.PREFIX.path()));
    }

}

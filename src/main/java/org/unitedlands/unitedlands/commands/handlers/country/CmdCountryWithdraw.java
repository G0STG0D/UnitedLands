package org.unitedlands.unitedlands.commands.handlers.country;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
    parent          = CmdCountry.class,
    name            = "withdraw",
    description     = "Withdraws money from the country bank account",
    usage           = "/country withdraw <amount>",
    playerOnly      = true
)
public class CmdCountryWithdraw extends CountryCommandHandler {

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

        var context = validate(sender, "country.withdraw");
        if (context == null)
            return;

        BigDecimal amount = new BigDecimal(0);
        try {
            amount = new BigDecimal(args[0]);
        } catch (Exception ex) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.GENERAL_ERRORS__WRONG_NUMBER_FORMAT.path()),
                    Map.of("input", args[1]), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (!UnitedLandsEconomyManager.instance().has(context.country().getUuid(), amount)) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.GENERAL_ERRORS__NO_FUNDS_COUNTRY.path()),
                    Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        UnitedLandsEconomyManager.instance().withdraw(context.country().getUuid(), amount);
        UnitedLandsEconomyManager.instance().deposit(context.player().getUniqueId(), amount);

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__WITHDRAW__SUCCESS.path()),
                Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)), MessageProvider.instance().get(Message.PREFIX.path()));
    }

}

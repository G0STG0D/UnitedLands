package org.unitedlands.unitedlands.commands.handlers.country;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class CountryDepositCommand extends CountryCommandHandler {

    public CountryDepositCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__COUNTRY__DEPOSIT__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var player = (Player) sender;
        var citizen = UnitedLandsDataManager.instance().getCitizen(player);
        if (citizen == null || citizen.getCountry() == null) {
            Messenger.sendMessage(player, messageProvider.get(Message.GENERAL_ERRORS__NOT_IN_COUNTRY.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var country = citizen.getCountry();

        BigDecimal amount = new BigDecimal(0);
        try {
            amount = new BigDecimal(args[0]);
        } catch (Exception ex) {
            Messenger.sendMessage(player, messageProvider.get(Message.GENERAL_ERRORS__WRONG_NUMBER_FORMAT.path()),
                    Map.of("input", args[1]), messageProvider.get(Message.PREFIX.path()));
            return;
        }

        if (!UnitedLandsEconomyManager.instance().has(player.getUniqueId(), amount)) {
            Messenger.sendMessage(player, messageProvider.get(Message.GENERAL_ERRORS__NO_FUNDS.path()),
                    Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)), messageProvider.get(Message.PREFIX.path()));
            return;
        }

        UnitedLandsEconomyManager.instance().withdraw(player.getUniqueId(), amount);
        UnitedLandsEconomyManager.instance().deposit(country.getUuid(), amount);

        Messenger.sendMessage(player, messageProvider.get(Message.PLAYER__COUNTRY__DEPOSIT__SUCCESS.path()),
                Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)),
                messageProvider.get(Message.PREFIX.path()));
    }

}

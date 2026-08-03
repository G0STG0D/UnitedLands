package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
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

        if (args.length != 1) {
            Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__SETTLEMENT__WITHDRAW__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var context = validate(sender, "settlement.withdraw");
        if (context == null)
            return;

        BigDecimal amount = new BigDecimal(0);
        try {
            amount = new BigDecimal(args[0]);
        } catch (Exception ex) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.GENERAL_ERRORS__WRONG_NUMBER_FORMAT.path()),
                    Map.of("input", args[1]), messageProvider.get(Message.PREFIX.path()));
            return;
        }

        if (!UnitedLandsEconomyManager.instance().has(context.settlement().getUuid(), amount)) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.GENERAL_ERRORS__NO_FUNDS_SETTLEMENT.path()),
                    Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)), messageProvider.get(Message.PREFIX.path()));
            return;
        }

        UnitedLandsEconomyManager.instance().withdraw(context.settlement().getUuid(), amount);
        UnitedLandsEconomyManager.instance().deposit(context.player().getUniqueId(), amount);

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__WITHDRAW__SUCCESS.path()),
                Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)), messageProvider.get(Message.PREFIX.path()));
    }

}

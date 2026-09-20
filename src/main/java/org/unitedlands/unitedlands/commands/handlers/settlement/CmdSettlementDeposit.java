package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.math.BigDecimal;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;

import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdSettlement.class,
        name = "deposit",
        description = "Deposits money in a settlement's bank account",
        usage = "/settlement deposit <amount>",
        playerOnly = true
)
public class CmdSettlementDeposit extends SettlementCommandHandler {

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

        var context = validate(sender, null);
        if (context == null)
            return;

        BigDecimal amount = new BigDecimal(0);
        try {
            amount = new BigDecimal(args[0]);
        } catch (Exception ex) {
            United.messenger().send(context.player(), "general-errors.wrong-number-format", args[1]);
            return;
        }

        if (!UnitedLandsEconomyManager.instance().has(context.player().getUniqueId(), amount)) {
            United.messenger().send(context.player(), "general-errors.no-funds-player", UnitedLandsEconomyManager.instance().format(amount));
            return;
        }

        UnitedLandsEconomyManager.instance().withdraw(context.player().getUniqueId(), amount, "Deposit to " + context.settlement().getName());
        UnitedLandsEconomyManager.instance().deposit(context.settlement().getUuid(), amount, "Deposited by " + context.player().getName());

        United.messenger().send(context.player(), "player.settlement.deposit.success", UnitedLandsEconomyManager.instance().format(amount));
    }

}

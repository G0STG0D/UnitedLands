package org.unitedlands.unitedlands.commands.handlers.settlement.set;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;

import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdSettlementSet.class,
        name = "taxes",
        description = "Sets the settlement taxes",
        usage = "/settlement set taxes <amount>",
        playerOnly = true
)
public class CmdSettlementSetTaxes extends SettlementCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "settlement.settaxes");
        if (context == null)
            return;

        double value = 0.0f;
        try {
            value = Float.parseFloat(args[0]);
        } catch (NumberFormatException ex) {
            United.messenger().send(context.player(), "general-errors.wrong-number-format", args[0]);
            return;
        }

        if (context.settlement().useTaxPercent()) {
            if (value < Settings.settlementMinTaxPercent) {
                United.messenger().send(context.player(), "player.settlement.settax.below-min", String.format("%.2f%%", Settings.settlementMinTaxPercent * 100));
                value = Settings.settlementMinTaxPercent;
            } else if (value > Settings.settlementMaxTaxPercent) {
                United.messenger().send(context.player(), "player.settlement.settax.above-max", String.format("%.2f%%", Settings.settlementMaxTaxPercent * 100));
                value = Settings.settlementMaxTaxPercent;
            }
        } else {
            if (value < Settings.settlementMinTaxAmount) {
                United.messenger().send(context.player(), "player.settlement.settax.below-min", UnitedLandsEconomyManager.instance().format(Settings.settlementMinTaxAmount));
                value = Settings.settlementMinTaxAmount;
            } else if (value > Settings.settlementMaxTaxAmount) {
                United.messenger().send(context.player(), "player.settlement.settax.above-max", UnitedLandsEconomyManager.instance().format(Settings.settlementMaxTaxAmount));
                value = Settings.settlementMaxTaxAmount;
            }
        }

        context.settlement().setTax((float) value);

        var valueString = context.settlement().useTaxPercent() ? String.format("%.2f%%", value * 100)
                : UnitedLandsEconomyManager.instance().format((double) value);

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement(), false);

        United.messenger().send(context.player(), "player.settlement.settax.success", context.settlement().getCleanName(), valueString);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

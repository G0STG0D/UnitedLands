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
        name = "taxpercent",
        description = "Toggles the settlement tax percent use",
        usage = "/settlement set taxpercent <true|false>",
        playerOnly = true
)
public class CmdSettlementSetTaxPercent extends SettlementCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "settlement.usetaxpercent");
        if (context == null)
            return;

        boolean value = Boolean.parseBoolean(args[0]);

        context.settlement().setUseTaxPercent(value);

        var currTax = context.settlement().getTax();
        if (context.settlement().useTaxPercent()) {
            if (currTax < Settings.settlementMinTaxPercent) {
                United.messenger().send(context.player(), "player.settlement.settax.below-min", String.format("%.2f%%", Settings.settlementMinTaxPercent * 100));
                context.settlement().setTax(Settings.settlementMinTaxPercent);
            } else if (currTax > Settings.settlementMaxTaxPercent) {
                United.messenger().send(context.player(), "player.settlement.settax.above-max", String.format("%.2f%%", Settings.settlementMaxTaxPercent * 100));
                context.settlement().setTax(Settings.settlementMaxTaxPercent);

            }
        } else {
            if (currTax < Settings.settlementMinTaxAmount) {
                United.messenger().send(context.player(), "player.settlement.settax.below-min", UnitedLandsEconomyManager.instance().format(Settings.settlementMinTaxAmount));
                context.settlement().setTax((float) Settings.settlementMinTaxAmount);

            } else if (currTax > Settings.settlementMaxTaxAmount) {
                United.messenger().send(context.player(), "player.settlement.settax.above-max", UnitedLandsEconomyManager.instance().format(Settings.settlementMaxTaxAmount));
                context.settlement().setTax((float) Settings.settlementMaxTaxAmount);
            }
        }

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement(), false);

        United.messenger().send(context.player(), "player.settlement.usetaxpercent.success", context.settlement().getCleanName(), value == true ? "<green>on</green>" : "<red>off</red>");
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return List.of("true", "false");
        return null;
    }

}

package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementUseTaxPercentCommand extends SettlementCommandHandler {

    public SettlementUseTaxPercentCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1)
            // TODO: Usage
            return;

        var context = validate(sender, "settlement.usetaxpercent");
        if (context == null)
            return;

        boolean value = Boolean.parseBoolean(args[0]);

        context.settlement().setUseTaxPercent(value);

        var currTax = context.settlement().getTax();
        if (context.settlement().isUseTaxPercent()) {
            if (currTax < Settings.settlementMinTaxPercent) {
                Messenger.sendMessage(context.player(), messageProvider.get("settlement.settaxes.below-min"),
                        Map.of("min", String.format("%.2f%%", Settings.settlementMinTaxPercent * 100)),
                        messageProvider.get("prefix"));
                context.settlement().setTax(Settings.settlementMinTaxPercent);
            } else if (currTax > Settings.settlementMaxTaxPercent) {
                Messenger.sendMessage(context.player(), messageProvider.get("settlement.settaxes.above-max"),
                        Map.of("max", String.format("%.2f%%", Settings.settlementMaxTaxPercent * 100)),
                        messageProvider.get("prefix"));
                context.settlement().setTax(Settings.settlementMaxTaxPercent);

            }
        } else {
            if (currTax < Settings.settlementMinTaxAmount) {
                Messenger.sendMessage(context.player(), messageProvider.get("settlement.settaxes.below-min"),
                        Map.of("min", UnitedLandsEconomyManager.instance().format(Settings.settlementMinTaxAmount)),
                        messageProvider.get("prefix"));
                context.settlement().setTax((float) Settings.settlementMinTaxAmount);

            } else if (currTax > Settings.settlementMaxTaxAmount) {
                Messenger.sendMessage(context.player(), messageProvider.get("settlement.settaxes.above-max"),
                        Map.of("max", UnitedLandsEconomyManager.instance().format(Settings.settlementMaxTaxAmount)),
                        messageProvider.get("prefix"));
                context.settlement().setTax((float) Settings.settlementMaxTaxAmount);
            }
        }

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

        Messenger.sendMessage(context.player(), messageProvider.get("settlement.usetaxpercent-" + value),
                Map.of("settlement", context.settlement().getCleanName()), messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return List.of("true", "false");
        return null;
    }

}

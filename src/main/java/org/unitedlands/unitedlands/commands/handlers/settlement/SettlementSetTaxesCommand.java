package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementSetTaxesCommand extends SettlementCommandHandler {

    public SettlementSetTaxesCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1)
            // TODO: Usage
            return;

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;
        var settlement = getCitizenSettlement(citizen);
        if (settlement == null)
            return;

        if (!hasPermission("settlement.settaxes", citizen))
            return;

        double value = 0.0f;
        try {
            value = Float.parseFloat(args[0]);
        } catch (NumberFormatException ex) {
            Messenger.sendMessage(player, messageProvider.get("errors.wrong-number-format"),
                    Map.of("input", args[0]), messageProvider.get("prefix"));
            return;
        }

        if (settlement.isUseTaxPercent()) {
            if (value < Settings.settlementMinTaxPercent) {
                Messenger.sendMessage(player, messageProvider.get("settlement.settaxes.below-min"),
                        Map.of("min", String.format("%.2f%%", Settings.settlementMinTaxPercent * 100)),
                        messageProvider.get("prefix"));
                value = Settings.settlementMinTaxPercent;
            } else if (value > Settings.settlementMaxTaxPercent) {
                Messenger.sendMessage(player, messageProvider.get("settlement.settaxes.above-max"),
                        Map.of("max", String.format("%.2f%%", Settings.settlementMaxTaxPercent * 100)),
                        messageProvider.get("prefix"));
                value = Settings.settlementMaxTaxPercent;
            }
        } else {
            if (value < Settings.settlementMinTaxAmount) {
                Messenger.sendMessage(player, messageProvider.get("settlement.settaxes.below-min"),
                        Map.of("min", EconomyManager.instance().format(Settings.settlementMinTaxAmount)),
                        messageProvider.get("prefix"));
                value = Settings.settlementMinTaxAmount;
            } else if (value > Settings.settlementMaxTaxAmount) {
                Messenger.sendMessage(player, messageProvider.get("settlement.settaxes.above-max"),
                        Map.of("max", EconomyManager.instance().format(Settings.settlementMaxTaxAmount)),
                        messageProvider.get("prefix"));
                value = Settings.settlementMaxTaxAmount;
            }
        }

        settlement.setTax((float)value);

        var valueString = settlement.isUseTaxPercent() ? String.format("%.2f%%", value * 100)
                : EconomyManager.instance().format((double) value);

        GlobalDataManager.instance().updateSettlementDbData(settlement);

        Messenger.sendMessage(player, messageProvider.get("settlement.settaxes.set"),
                Map.of("settlement", settlement.getCleanName(), "value", valueString), messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

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

public class SettlementUseTaxPercentCommand extends SettlementCommandHandler {

    public SettlementUseTaxPercentCommand(UnitedLands plugin, IMessageProvider messageProvider) {
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

        if (!hasPermission("settlement.usetaxpercent", citizen))
            return;

        boolean value = Boolean.parseBoolean(args[0]);

        settlement.setUseTaxPercent(value);

        var currTax = settlement.getTax();
        if (settlement.isUseTaxPercent()) {
            if (currTax < Settings.settlementMinTaxPercent) {
                Messenger.sendMessage(player, messageProvider.get("settlement.settaxes.below-min"),
                        Map.of("min", String.format("%.2f%%", Settings.settlementMinTaxPercent * 100)),
                        messageProvider.get("prefix"));
                settlement.setTax(Settings.settlementMinTaxPercent);
            } else if (currTax > Settings.settlementMaxTaxPercent) {
                Messenger.sendMessage(player, messageProvider.get("settlement.settaxes.above-max"),
                        Map.of("max", String.format("%.2f%%", Settings.settlementMaxTaxPercent * 100)),
                        messageProvider.get("prefix"));
                settlement.setTax(Settings.settlementMaxTaxPercent);

            }
        } else {
            if (currTax < Settings.settlementMinTaxAmount) {
                Messenger.sendMessage(player, messageProvider.get("settlement.settaxes.below-min"),
                        Map.of("min", EconomyManager.instance().format(Settings.settlementMinTaxAmount)),
                        messageProvider.get("prefix"));
                settlement.setTax((float) Settings.settlementMinTaxAmount);

            } else if (currTax > Settings.settlementMaxTaxAmount) {
                Messenger.sendMessage(player, messageProvider.get("settlement.settaxes.above-max"),
                        Map.of("max", EconomyManager.instance().format(Settings.settlementMaxTaxAmount)),
                        messageProvider.get("prefix"));
                settlement.setTax((float) Settings.settlementMaxTaxAmount);
            }
        }

        GlobalDataManager.instance().updateSettlementDbData(settlement);

        Messenger.sendMessage(player, messageProvider.get("settlement.usetaxpercent-" + value),
                Map.of("settlement", settlement.getCleanName()), messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return List.of("true", "false");
        return null;
    }

}

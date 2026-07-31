package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementSetTaxesCommand extends SettlementCommandHandler {

    public SettlementSetTaxesCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__SETTLEMENT__SETTAX__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var context = validate(sender, "settlement.settaxes");
        if (context == null)
            return;

        double value = 0.0f;
        try {
            value = Float.parseFloat(args[0]);
        } catch (NumberFormatException ex) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.GENERAL_ERRORS__WRONG_NUMBER_FORMAT.path()),
                    Map.of("input", args[0]), messageProvider.get(Message.PREFIX.path()));
            return;
        }

        if (context.settlement().useTaxPercent()) {
            if (value < Settings.settlementMinTaxPercent) {
                Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__SETTAX__BELOW_MIN.path()),
                        Map.of("min", String.format("%.2f%%", Settings.settlementMinTaxPercent * 100)),
                        messageProvider.get(Message.PREFIX.path()));
                value = Settings.settlementMinTaxPercent;
            } else if (value > Settings.settlementMaxTaxPercent) {
                Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__SETTAX__ABOVE_MAX.path()),
                        Map.of("max", String.format("%.2f%%", Settings.settlementMaxTaxPercent * 100)),
                        messageProvider.get(Message.PREFIX.path()));
                value = Settings.settlementMaxTaxPercent;
            }
        } else {
            if (value < Settings.settlementMinTaxAmount) {
                Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__SETTAX__BELOW_MIN.path()),
                        Map.of("min", UnitedLandsEconomyManager.instance().format(Settings.settlementMinTaxAmount)),
                        messageProvider.get(Message.PREFIX.path()));
                value = Settings.settlementMinTaxAmount;
            } else if (value > Settings.settlementMaxTaxAmount) {
                Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__SETTAX__ABOVE_MAX.path()),
                        Map.of("max", UnitedLandsEconomyManager.instance().format(Settings.settlementMaxTaxAmount)),
                        messageProvider.get(Message.PREFIX.path()));
                value = Settings.settlementMaxTaxAmount;
            }
        }

        context.settlement().setTax((float) value);

        var valueString = context.settlement().useTaxPercent() ? String.format("%.2f%%", value * 100)
                : UnitedLandsEconomyManager.instance().format((double) value);

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__SETTAX__SUCCESS.path()),
                Map.of("settlement", context.settlement().getCleanName(), "value", valueString), messageProvider.get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

package org.unitedlands.unitedlands.commands.handlers.settlement.set;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.commands.handlers.settlement.rank.CmdSettlementRank;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlementRank.class,
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
                Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__SETTAX__BELOW_MIN.path()),
                        Map.of("min", String.format("%.2f%%", Settings.settlementMinTaxPercent * 100)),
                        MessageProvider.instance().get(Message.PREFIX.path()));
                context.settlement().setTax(Settings.settlementMinTaxPercent);
            } else if (currTax > Settings.settlementMaxTaxPercent) {
                Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__SETTAX__ABOVE_MAX.path()),
                        Map.of("max", String.format("%.2f%%", Settings.settlementMaxTaxPercent * 100)),
                        MessageProvider.instance().get(Message.PREFIX.path()));
                context.settlement().setTax(Settings.settlementMaxTaxPercent);

            }
        } else {
            if (currTax < Settings.settlementMinTaxAmount) {
                Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__SETTAX__BELOW_MIN.path()),
                        Map.of("min", UnitedLandsEconomyManager.instance().format(Settings.settlementMinTaxAmount)),
                        MessageProvider.instance().get(Message.PREFIX.path()));
                context.settlement().setTax((float) Settings.settlementMinTaxAmount);

            } else if (currTax > Settings.settlementMaxTaxAmount) {
                Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__SETTAX__ABOVE_MAX.path()),
                        Map.of("max", UnitedLandsEconomyManager.instance().format(Settings.settlementMaxTaxAmount)),
                        MessageProvider.instance().get(Message.PREFIX.path()));
                context.settlement().setTax((float) Settings.settlementMaxTaxAmount);
            }
        }

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__USETAXPERCENT__SUCCESS.path()),
                Map.of("settlement", context.settlement().getCleanName(),
                        "state", value == true ? "<green>on</green>" : "<red>off</red>"),
                MessageProvider.instance().get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return List.of("true", "false");
        return null;
    }

}

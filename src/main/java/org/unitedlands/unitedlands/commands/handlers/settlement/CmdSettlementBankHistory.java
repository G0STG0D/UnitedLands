package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlement.class,
        name = "bankhistory",
        description = "Shows the settlement's bank history",
        usage = "/settlement bankhistory [page]",
        playerOnly = true
)
public class CmdSettlementBankHistory extends SettlementCommandHandler {

    private final int PAGE_SIZE = 5;

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, "settlement.bankhistory");
        if (context == null)
            return;

        var startIndex = 0;
        if (args.length == 1) {
            try {
                startIndex = (Integer.parseInt(args[0]) - 1);
            } catch (Exception ex) {
                Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.GENERAL_ERRORS__WRONG_NUMBER_FORMAT.path()),
                        Map.of("input", args[0]), MessageProvider.instance().get(Message.PREFIX.path()));
                return;
            }
        }

        var records = UnitedLandsEconomyManager.instance().getBankRecords(context.settlement().getUuid(), startIndex, PAGE_SIZE);
        Messenger.sendMessage(sender, "<bold>" + context.settlement().getCleanName() + " Bank History:</bold>", null, MessageProvider.instance().get(Message.PREFIX.path()));
        for (var record : records) {

            var timeStamp = "<gray>[" + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(record.getTimestamp()) + "]</gray>";
            var amount = record.getAmount() > 0 ?          
                         "<green>+"  + UnitedLandsEconomyManager.instance().format(record.getAmount()) + "<green>" :
                         "<red>"  + UnitedLandsEconomyManager.instance().format(record.getAmount()) + "<red>";

            Messenger.sendMessage(sender, 
                timeStamp + " " + amount + " <gray>(" + record.getDetails() + ")</gray>", 
                null, 
                MessageProvider.instance().get(Message.PREFIX.path()));
        }
    }

}

package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.ColorUtils;
import org.unitedlands.utils.Messenger;

public class SettlementSetColorCommand extends SettlementCommandHandler {

    public SettlementSetColorCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__SETTLEMENT__SETCOLOR__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }
        
        var context = validate(sender, "settlement.setcolor");
        if (context == null)
            return;

        if (!(args[0].length() == 7) || !ColorUtils.isValidHexColor(args[0])) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.GENERAL_ERRORS__WRONG_COLOR_FORMAT.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        context.settlement().setFillColor(args[0] + "10");
        context.settlement().setStrokeColor(args[0]);

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__SETCOLOR__SUCCESS.path()),
                Map.of("color", args[1]), messageProvider.get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

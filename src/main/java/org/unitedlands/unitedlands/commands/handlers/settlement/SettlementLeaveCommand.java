package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.events.settlement.SettlementPlayerLeaveEvent;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementLeaveCommand extends SettlementCommandHandler {

    public SettlementLeaveCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, null);
        if (context == null)
            return;
        
        if (context.citizen().hasSettlementRank("mayor")) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__LEAVE__IS_MAYOR.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        if (context.citizen().hasCountryRank("leader")) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__LEAVE__IS_LEADER.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        Confirmation leave = new Confirmation("settlement-leave");
        leave.setRunnable(() -> {

            context.settlement().removeCitizen(context.citizen());
            context.citizen().removeSettlementRanks();
            context.citizen().removeSettlement();

            (new SettlementPlayerLeaveEvent(context.settlement(), context.player())).callEvent();

            UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());
            UnitedLandsDataManager.instance().updateCitizenDbData(context.citizen());

            Messenger.sendMessage(context.settlement().getOnlinePlayers(), messageProvider.get(Message.PLAYER__SETTLEMENT__PLAYER_LEFT.path()),
                    Map.of("name", context.player().getName()), messageProvider.get(Message.PREFIX.path()));
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__LEAVE__SETTLEMENT_LEFT.path()),
                    Map.of("settlement", context.settlement().getCleanName()), messageProvider.get(Message.PREFIX.path()));

        })
                .setTitle(messageProvider.get(Message.PLAYER__SETTLEMENT__LEAVE__CONFIRM.path()))
                .setReplacements(Map.of("settlement", context.settlement().getCleanName()))
                .setSender(context.player())
                .setReceiver(context.player())
                .send();
    }

}

package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.events.settlement.SettlementPlayerLeaveEvent;
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
            Messenger.sendMessage(context.player(), messageProvider.get("settlement.leave.is-mayor"),
                    null, messageProvider.get("prefix"));
            return;
        }

        if (context.citizen().hasCountryRank("leader")) {
            Messenger.sendMessage(context.player(), messageProvider.get("settlement.leave.is-leader"),
                    null, messageProvider.get("prefix"));
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

            Messenger.sendMessage(context.settlement().getOnlinePlayers(), messageProvider.get("settlement.leave.player-left"),
                    Map.of("name", context.player().getName()), messageProvider.get("prefix"));
            Messenger.sendMessage(context.player(), messageProvider.get("settlement.leave.settlement-left"),
                    Map.of("settlement", context.settlement().getCleanName()), messageProvider.get("prefix"));

        })
                .setTitle(messageProvider.get("settlement.leave.confirm"))
                .setReplacements(Map.of("settlement", context.settlement().getCleanName()))
                .setSender(context.player())
                .setReceiver(context.player())
                .setDiscriminator(context.settlement().getName())
                .setAcceptCommand("/approve settlement-leave")
                .setCancelCommand("/cancel settlement-leave")
                .setTimeoutSeconds(60)
                .send();
    }

}

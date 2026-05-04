package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
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

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;
        var settlement = getCitizenSettlement(citizen);
        if (settlement == null)
            return;

        if (citizen.hasSettlementRank("mayor")) {
            Messenger.sendMessage(player, messageProvider.get("settlement.leave.is-mayor"),
                    null, messageProvider.get("prefix"));
            return;
        }

        Confirmation leave = new Confirmation("settlement-leave");
        leave.setRunnable(() -> {

            settlement.removeCitizen(citizen);
            citizen.removeSettlementRanks();
            citizen.removeSettlement();

            GlobalDataManager.instance().updateSettlementDbData(settlement);
            GlobalDataManager.instance().updateCitizenDbData(citizen);

            Messenger.sendMessage(settlement.getOnlinePlayers(), messageProvider.get("settlement.leave.player-left"),
                    Map.of("name", player.getName()), messageProvider.get("prefix"));
            Messenger.sendMessage(player, messageProvider.get("settlement.leave.settlement-left"),
                    Map.of("settlement", settlement.getCleanName()), messageProvider.get("prefix"));

        })
                .setTitle("<aqua>Are you sure you want to leave <blue>" + settlement.getCleanName() + "</blue>?")
                .setSender(player)
                .setReceiver(player)
                .setDiscriminator(settlement.getName())
                .setAcceptCommand("/approve settlement-leave")
                .setCancelCommand("/cancel settlement-leave")
                .setTimeoutSeconds(60)
                .send();
    }

}

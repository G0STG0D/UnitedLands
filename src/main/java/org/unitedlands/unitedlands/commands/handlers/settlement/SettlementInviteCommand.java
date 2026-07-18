package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementInviteCommand extends SettlementCommandHandler {

    public SettlementInviteCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length == 0) {
            // TODO: Usage
            return;
        }

        var context = validate(sender, "settlement.invite");
        if (context == null)
            return;
        
        var targetPlayer = Bukkit.getPlayerExact(args[0]);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            Messenger.sendMessage(context.player(), messageProvider.get("errors.player-not-found"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var targetCitizen = getCitizen(targetPlayer);
        if (targetCitizen == null)
            return;

        Confirmation invite = new Confirmation("settlement-invite");
        invite.setRunnable(() -> {

            context.settlement().addCitizen(targetCitizen);
            targetCitizen.setSettlement(context.settlement());

            UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());
            UnitedLandsDataManager.instance().updateCitizenDbData(targetCitizen);

            Messenger.sendMessage(context.settlement().getOnlinePlayers(), messageProvider.get("settlement.invite.player-joined"),
                    Map.of("name", targetPlayer.getName()), messageProvider.get("prefix"));
            Messenger.sendMessage(targetPlayer, messageProvider.get("settlement.invite.settlement-joined"),
                    Map.of("settlement", context.settlement().getCleanName()), messageProvider.get("prefix"));

        })
                .setTitle(messageProvider.get("settlement.invite.player-message"))
                .setReplacements(Map.of("settlement", context.settlement().getCleanName()))
                .setSender(context.player())
                .setReceiver(targetPlayer)
                .setDiscriminator(context.settlement().getName())
                .setAcceptCommand("/approve invite [" + context.settlement().getName() + "]")
                .setCancelCommand("/reject invite [" + context.settlement().getName() + "]")
                .setTimeoutSeconds(60)
                .send();
    }

}

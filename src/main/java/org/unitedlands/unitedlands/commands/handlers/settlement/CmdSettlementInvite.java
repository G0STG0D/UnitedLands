package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlement.class,
        name = "invite",
        description = "Invites a player to the settlement",
        usage = "/settlement invite <player>",
        playerOnly = true
)
public class CmdSettlementInvite extends SettlementCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "settlement.invite");
        if (context == null)
            return;

        var targetPlayer = Bukkit.getPlayerExact(args[0]);
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.GENERAL_ERRORS__PLAYER_NOT_FOUND.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        var targetCitizen = getCitizen(targetPlayer);
        if (targetCitizen == null)
            return;

        if (targetCitizen.hasSettlement())
        {
                        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__INVITE__ALREADY_IN_SETTLEMENT.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }


        Confirmation invite = new Confirmation("settlement-invite");
        invite.setRunnable(() -> {

            context.settlement().addCitizen(targetCitizen);
            targetCitizen.setSettlement(context.settlement());

            UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement(), true);
            UnitedLandsDataManager.instance().updateCitizenDbData(targetCitizen);

            Messenger.sendMessage(context.settlement().getOnlinePlayers(),
                    MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__INVITE__PLAYER_JOINED.path()),
                    Map.of("name", targetPlayer.getName()), MessageProvider.instance().get(Message.PREFIX.path()));
            Messenger.sendMessage(targetPlayer, MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__INVITE__PLAYER_MESSAGE.path()),
                    Map.of("settlement", context.settlement().getCleanName()), MessageProvider.instance().get(Message.PREFIX.path()));

        })
                .setTitle(MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__INVITE__INVITE_MESSAGE.path()))
                .setReplacements(Map.of("settlement", context.settlement().getCleanName()))
                .setSender(context.player())
                .setReceiver(targetPlayer)
                .send();
    }

}

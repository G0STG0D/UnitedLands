package org.unitedlands.unitedlands.commands.handlers.settlement.trust;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlementTrust.class,
        name = "add",
        description = "Removes settlement trust from a player",
        usage = "/settlement trust remove <player>",
        playerOnly = true
)
public class CmdSettlementTrustRemove extends SettlementCommandHandler {

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

        var context = validate(sender, "settlement.addtrust");
        if (context == null)
            return;

        var targetPlayer = Bukkit.getPlayerExact(args[0]);
        if (targetPlayer == null) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.GENERAL_ERRORS__PLAYER_NOT_FOUND.path()),
                    Map.of("name", args[0]), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }
        var targetCitizen = getCitizen(targetPlayer);
        if (targetCitizen == null)
            return;

        if (!context.settlement().getTrustList().contains(targetCitizen)) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__REMOVETRUST__NOT_TRUSTED.path()),
                    Map.of("citizen", args[0]), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        context.settlement().removeTrusted(targetCitizen);
        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

        if (targetPlayer.isOnline()) {
            Messenger.sendMessage(targetPlayer, MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__REMOVETRUST__UNTRUSTED.path()),
                    Map.of("settlement", context.settlement().getCleanName()), MessageProvider.instance().get(Message.PREFIX.path()));
        }

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__REMOVETRUST__SUCCESS.path()),
                Map.of("citizen", targetPlayer.getName()), MessageProvider.instance().get(Message.PREFIX.path()));
    }

}

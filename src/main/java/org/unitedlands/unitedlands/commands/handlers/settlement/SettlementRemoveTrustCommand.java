package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementRemoveTrustCommand extends SettlementCommandHandler {

    public SettlementRemoveTrustCommand(UnitedLands plugin, IMessageProvider messageProvider) {
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

        if (args.length != 1) {
            Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__SETTLEMENT__REMOVETRUST__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var context = validate(sender, "settlement.addtrust");
        if (context == null)
            return;

        var targetPlayer = Bukkit.getPlayerExact(args[0]);
        if (targetPlayer == null) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.GENERAL_ERRORS__PLAYER_NOT_FOUND.path()),
                    Map.of("name", args[0]), messageProvider.get(Message.PREFIX.path()));
            return;
        }
        var targetCitizen = getCitizen(targetPlayer);
        if (targetCitizen == null)
            return;

        if (!context.settlement().getTrustList().contains(targetCitizen)) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__REMOVETRUST__NOT_TRUSTED.path()),
                    Map.of("citizen", args[0]), messageProvider.get(Message.PREFIX.path()));
            return;
        }

        context.settlement().removeTrusted(targetCitizen);
        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

        if (targetPlayer.isOnline()) {
            Messenger.sendMessage(targetPlayer, messageProvider.get(Message.PLAYER__SETTLEMENT__REMOVETRUST__UNTRUSTED.path()),
                    Map.of("settlement", context.settlement().getCleanName()), messageProvider.get(Message.PREFIX.path()));
        }

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__REMOVETRUST__SUCCESS.path()),
                Map.of("citizen", targetPlayer.getName()), messageProvider.get(Message.PREFIX.path()));
    }

}

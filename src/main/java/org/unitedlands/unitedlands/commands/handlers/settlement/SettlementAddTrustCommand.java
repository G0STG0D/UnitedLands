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

public class SettlementAddTrustCommand extends SettlementCommandHandler {

    public SettlementAddTrustCommand(UnitedLands plugin, IMessageProvider messageProvider) {
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
            Messenger.sendMessage(sender, messageProvider.get(Message.PLAYER__SETTLEMENT__ADDTRUST__USAGE.path()),
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

        if (context.player().equals(targetPlayer)) {
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__ADDTRUST__CANNOT_TRUST_YOURSELF.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var targetCitizen = getCitizen(targetPlayer);
        if (targetCitizen == null)
            return;

        context.settlement().addTrusted(targetCitizen);
        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

        if (targetPlayer.isOnline()) {
            Messenger.sendMessage(targetPlayer, messageProvider.get(Message.PLAYER__SETTLEMENT__ADDTRUST__TRUSTED.path()),
                    Map.of("settlement", context.settlement().getCleanName()), messageProvider.get(Message.PREFIX.path()));
        }

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__ADDTRUST__SUCCESS.path()),
                Map.of("citizen", targetPlayer.getName()), messageProvider.get(Message.PREFIX.path()));
    }

}

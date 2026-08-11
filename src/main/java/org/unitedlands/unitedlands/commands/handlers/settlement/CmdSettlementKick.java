package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.events.settlement.SettlementPlayerLeaveEvent;
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
public class CmdSettlementKick extends SettlementCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1) {
            var player = (Player) sender;
            var citizen = getCitizen(player);
            if (citizen == null)
                return null;
            var settlement = citizen.getSettlement();
            if (settlement == null)
                return null;
            return settlement.getCitizens().stream().map(Citizen::getName).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var context = validate(sender, "settlement.kick");
        if (context == null)
            return;

        var targetPlayer = Bukkit.getPlayerExact(args[0]);
        if (targetPlayer == null) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.GENERAL_ERRORS__PLAYER_NOT_FOUND.path()),
                    Map.of("citizen", args[0]), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (context.player().equals(targetPlayer)) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__KICK__CANNOT_KICK_YOURSELF.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        var targetCitizen = getCitizen(targetPlayer);
        if (targetCitizen == null)
            return;

        if (!context.settlement().getCitizens().contains(targetCitizen)) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__KICK__NOT_IN_SETTLEMENT.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (targetCitizen.hasCountryRank("leader")) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__KICK__IS_LEADER.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        context.settlement().removeCitizen(targetCitizen);
        targetCitizen.removeSettlementRanks();
        targetCitizen.removeSettlement();

        (new SettlementPlayerLeaveEvent(context.settlement(), targetPlayer)).callEvent();

        UnitedLandsDataManager.instance().updateCitizenDbData(targetCitizen);

        if (targetPlayer.isOnline()) {
            Messenger.sendMessage(targetPlayer, MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__KICK__KICKED.path()),
                    Map.of("settlement", context.settlement().getCleanName()), MessageProvider.instance().get(Message.PREFIX.path()));
        }

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__KICK__SUCCESS.path()),
                Map.of("citizen", targetPlayer.getName()), MessageProvider.instance().get(Message.PREFIX.path()));
    }

}

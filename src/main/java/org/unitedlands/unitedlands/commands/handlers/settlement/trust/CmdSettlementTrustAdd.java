package org.unitedlands.unitedlands.commands.handlers.settlement.trust;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;

import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdSettlementTrust.class,
        name = "add",
        description = "Adds settlement trust to a player",
        usage = "/settlement trust add <player>",
        playerOnly = true
)
public class CmdSettlementTrustAdd extends SettlementCommandHandler {

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
            United.messenger().send(context.player(), "general-errors.player-not-found", args[0]);
            return;
        }

        if (context.player().equals(targetPlayer)) {
            United.messenger().send(context.player(), "player.settlement.addtrust.cannot-trust-yourself");
            return;
        }

        var targetCitizen = getCitizen(targetPlayer);
        if (targetCitizen == null)
            return;

        context.settlement().addTrusted(targetCitizen);
        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement(), false);

        if (targetPlayer.isOnline()) {
            United.messenger().send(targetPlayer, "player.settlement.addtrust.trusted", context.settlement().getCleanName());
        }

        United.messenger().send(context.player(), "player.settlement.addtrust.success", targetPlayer.getName());
    }

}

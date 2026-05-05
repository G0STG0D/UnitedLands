package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.utils.Messenger;

public class SettlementSetSpawnCommand extends SettlementCommandHandler {

    public SettlementSetSpawnCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
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

        if (!hasPermission("settlement.setspawn", citizen))
            return;

        var chunkCoordinates = CoordinateUtils.locationToChunkCoordinates(player.getLocation());
        if (!settlement.hasChunkAtCoordinates(chunkCoordinates)) {
            Messenger.sendMessage(player, messageProvider.get("settlement.setspawn.not-in-claims"),
                    null, messageProvider.get("prefix"));
            return;
        }

        settlement.setSpawn(player.getLocation());

        Messenger.sendMessage(player, messageProvider.get("settlement.setspawn.set"),
                null, messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

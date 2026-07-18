package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;

import org.bukkit.command.CommandSender;
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

        var context = validate(sender, "settlement.setspawn");
        if (context == null)
            return;

        var chunkCoordinates = CoordinateUtils.locationToChunkCoordinates(context.player().getLocation());
        if (!context.settlement().hasChunkAtCoordinates(chunkCoordinates)) {
            Messenger.sendMessage(context.player(), messageProvider.get("settlement.setspawn.not-in-claims"),
                    null, messageProvider.get("prefix"));
            return;
        }

        context.settlement().setSpawn(context.player().getLocation());

        Messenger.sendMessage(context.player(), messageProvider.get("settlement.setspawn.set"),
                null, messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

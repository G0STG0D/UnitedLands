package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
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
            Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__SETSPAWN__NOT_IN_CLAIMS.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        context.settlement().setSpawn(context.player().getLocation());

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

        Messenger.sendMessage(context.player(), messageProvider.get(Message.PLAYER__SETTLEMENT__SETSPAWN__SUCCESS.path()),
                null, messageProvider.get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

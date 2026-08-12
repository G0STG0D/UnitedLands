package org.unitedlands.unitedlands.commands.handlers.settlement.set;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlementSet.class,
        name = "spawn",
        description = "Sets the settlement spawn",
        usage = "/settlement set spawn",
        playerOnly = true
)
public class CmdSettlementSetSpawn extends SettlementCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, "settlement.setspawn");
        if (context == null)
            return;

        var chunkCoordinates = CoordinateUtils.locationToChunkCoordinates(context.player().getLocation());
        if (!context.settlement().hasChunkAtCoordinates(chunkCoordinates)) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__SETSPAWN__NOT_IN_CLAIMS.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        context.settlement().setSpawn(context.player().getLocation());

        UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement(), false);

        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__SETSPAWN__SUCCESS.path()),
                null, MessageProvider.instance().get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

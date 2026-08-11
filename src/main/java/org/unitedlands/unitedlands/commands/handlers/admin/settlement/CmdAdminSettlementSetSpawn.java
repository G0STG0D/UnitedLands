package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminSettlementSet.class,
        name = "spawn",
        description = "Changes a settlement's spawn",
        usage = "/ula settlement set spawn <settlement_name>",
        playerOnly = true
)
public class CmdAdminSettlementSetSpawn extends SettlementAdminCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var settlement = getSettlement(player, args[0]);
        if (settlement == null) {
            return;
        }

        var chunkCoordinates = CoordinateUtils.locationToChunkCoordinates(player.getLocation());
        if (!settlement.hasChunkAtCoordinates(chunkCoordinates)) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__SETTLEMENT__SETSPAWN__NOT_IN_CLAIMS.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        settlement.setSpawn(player.getLocation());

        Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__SETTLEMENT__SETSPAWN__SUCCESS.path()),
                Map.of("settlement", settlement.getName()), MessageProvider.instance().get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return UnitedLandsDataManager.instance().getSettlementNames();
        return null;
    }

}

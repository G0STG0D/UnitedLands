package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.utils.Messenger;

public class AdminSettlementSetSpawnCommand extends SettlementAdminCommandHandler {

    public AdminSettlementSetSpawnCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 1) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__SETSPAWN__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }
        if (!hasPermission(player)) {
            return;
        }

        var settlement = getSettlement(player, args[0]);
        if (settlement == null) {
            return;
        }

        var chunkCoordinates = CoordinateUtils.locationToChunkCoordinates(player.getLocation());
        if (!settlement.hasChunkAtCoordinates(chunkCoordinates)) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__SETSPAWN__NOT_IN_CLAIMS.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        settlement.setSpawn(player.getLocation());

        Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__SETSPAWN__SUCCESS.path()),
                Map.of("settlement", settlement.getName()), messageProvider.get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return UnitedLandsDataManager.instance().getSettlementNames();
        return null;
    }

}

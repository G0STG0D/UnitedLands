package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
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
            Messenger.sendMessage(player, messageProvider.get("admin.usage.settlement.setspawn"),
                    null, messageProvider.get("prefix"));
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
            Messenger.sendMessage(player, messageProvider.get("settlement.setspawn.not-in-claims"),
                    null, messageProvider.get("prefix"));
            return;
        }

        settlement.setSpawn(player.getLocation());

        Messenger.sendMessage(player, messageProvider.get("admin.settlement.setspawn"),
                Map.of("settlement", args[0]), messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return GlobalDataManager.instance().getSettlementNames();
        return null;
    }

}

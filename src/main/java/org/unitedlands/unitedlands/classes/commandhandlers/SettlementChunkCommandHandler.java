package org.unitedlands.unitedlands.classes.commandhandlers;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.managers.PlayerCacheManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.utils.Messenger;

public class SettlementChunkCommandHandler extends BaseCommandHandler<UnitedLands> {

    public SettlementChunkCommandHandler(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender arg0, String[] arg1) {

    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] arg1) {
        return null;
    }

    protected Citizen getCitizen(Player player) {
        var citizen = GlobalDataManager.instance().getCitizen(player);
        if (citizen == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.no-citizen-data"),
                    null, messageProvider.get("prefix"));
            return null;
        }
        return citizen;
    }

    protected SettlementChunk getSettlementChunk(Player player) {
        var chunkCoordinates = CoordinateUtils.locationToChunkCoordinates(player.getLocation());
        var settlementChunk = GlobalDataManager.instance().getSettlementChunk(chunkCoordinates);
        if (settlementChunk == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.not-in-claim"),
                    null, messageProvider.get("prefix"));
            return null;
        }
        return settlementChunk;
    }

    protected boolean hasPermission(String permission, Citizen citizen, SettlementChunk settlementChunk) {
        if (!settlementChunk.getSettlement().equals(citizen.getSettlement())) {
            Messenger.sendMessage((Player) citizen.getPlayer(), messageProvider.get("errors.no-claim-permission"),
                    null, messageProvider.get("prefix"));
            return false;            
        }
        if (!plugin.getPermissionManager().hasRankPermission(permission, citizen)) {
            Messenger.sendMessage((Player) citizen.getPlayer(), messageProvider.get("errors.no-settlement-permission"),
                    Map.of("perm", permission), messageProvider.get("prefix"));
            return false;
        }
        return true;
    }

    protected boolean hasChunkPermission(SettlementChunk chunk, Player player) {
        var playerCache = PlayerCacheManager.instance().getPlayerCache(player);
        if (!chunk.getCoordinates().equals(playerCache.getCachedChunkCoordinates()))
            playerCache.calculateMemberships();
        var membership = playerCache.getChunkMembership();
        if (membership == LocationMembership.OWNER || membership == LocationMembership.TRUSTED) {
            return true;
        } else {
            Messenger.sendMessage(player, messageProvider.get("errors.no-claim-permission"),
                    null, messageProvider.get("prefix"));
            return false;
        }
    }

}

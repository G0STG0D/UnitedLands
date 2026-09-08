package org.unitedlands.unitedlands.classes.commandhandlers;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.unitedlands.managers.PlayerCacheManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

public class SettlementChunkCommandHandler implements UnitedCommandExecutor {

    public record SettlementChunkCommandHandlerContext(Player player, Citizen citizen,
            SettlementChunk settlementChunk) {
    }

    @Override
    public void handleCommand(CommandSender arg0, String[] arg1) {

    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] arg1) {
        return null;
    }

    protected SettlementChunkCommandHandlerContext validate(CommandSender sender, String permission) {

        var player = (Player) sender;
        
        if (!Settings.worlds.contains(player.getLocation().getWorld().getName())) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__WRONG_WORLD.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return null;
        }
        
        var citizen = getCitizen(player);
        if (citizen == null)
            return null;
        var settlementChunk = getSettlementChunk(player);
        if (settlementChunk == null)
            return null;

        if (permission != null)
            if (!hasPermission(permission, citizen, settlementChunk))
                return null;

        return new SettlementChunkCommandHandlerContext(player, citizen, settlementChunk);
    }

    protected Citizen getCitizen(Player player) {
        var citizen = UnitedLandsDataManager.instance().getCitizen(player);
        if (citizen == null) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__NO_CITIZEN_DATA.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return null;
        }
        return citizen;
    }

    protected SettlementChunk getSettlementChunk(Player player) {
        var chunkCoordinates = CoordinateUtils.locationToChunkCoordinates(player.getLocation());
        var settlementChunk = UnitedLandsDataManager.instance().getSettlementChunk(chunkCoordinates);
        if (settlementChunk == null) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__NOT_IN_CLAIM.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return null;
        }
        return settlementChunk;
    }

    protected boolean hasPermission(String permission, Citizen citizen, SettlementChunk settlementChunk) {
        if (!settlementChunk.getSettlement().equals(citizen.getSettlement())) {
            Messenger.sendMessage((Player) citizen.getPlayer(), MessageProvider.instance().get(Message.GENERAL_ERRORS__NO_CLAIM_PERMISSION.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return false;
        }
        if (!PermissionManager.instance().hasRankPermission(permission, citizen)) {
            Messenger.sendMessage((Player) citizen.getPlayer(), MessageProvider.instance().get(Message.GENERAL_ERRORS__NO_SETTLEMENT_PERMISSION.path()),
                    Map.of("perm", permission), MessageProvider.instance().get(Message.PREFIX.path()));
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
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__NO_CLAIM_PERMISSION.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return false;
        }
    }

}

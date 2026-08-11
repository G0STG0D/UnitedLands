package org.unitedlands.unitedlands.classes.commandhandlers;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.unitedlands.managers.PlayerCacheManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

public class RegionCommandHandler implements UnitedCommandExecutor{

    public record RegionCommandHandlerContext(Player player, Citizen citizen,
            Region region) {
    }

    @Override
    public void handleCommand(CommandSender arg0, String[] arg1) {

    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] arg1) {
        return null;
    }

    protected RegionCommandHandlerContext validate(CommandSender sender, String permission) {

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return null;

        var playerCache = PlayerCacheManager.instance().getPlayerCache(player);
        if (playerCache.getCachedRegion() == null)
            return null;

        var region = playerCache.getCachedRegion();
        if (!region.hasCountry() || !region.getCountry().equals(citizen.getCountry())) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__NO_REGION_PERMISSION.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return null;
        }

        if (permission != null) {
            if (!hasPermission(permission, citizen)) {
                // Special region check: Region administrators have access to all region commands
                if (!citizen.equals(region.getAdministrator())) {
                    return null;
                }
            }
        }

        return new RegionCommandHandlerContext(player, citizen, playerCache.getCachedRegion());
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

    protected boolean hasPermission(String permission, Citizen citizen) {
        if (!PermissionManager.instance().hasRankPermission(permission, citizen)) {
            Messenger.sendMessage((Player) citizen.getPlayer(), MessageProvider.instance().get(Message.GENERAL_ERRORS__NO_COUNTRY_PERMISSION.path()),
                    Map.of("perm", permission), MessageProvider.instance().get(Message.PREFIX.path()));
            return false;
        }
        return true;
    }

}

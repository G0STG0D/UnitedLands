package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.utils.Messenger;

public class AdminCountryClaimCommand extends CountryAdminCommandHandler {

    public AdminCountryClaimCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);

    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length < 1) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__COUNTRY__CLAIM__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var country = getCountry(player, args[0]);
        if (country == null) {
            return;
        }

        var region = UnitedLandsDataManager.instance()
                .getRegion(CoordinateUtils.locationToChunkCenterCoordinates(player.getLocation()));
        if (region == null) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__COUNTRY__CLAIM__NO_REGION.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        } else {
            if (region.getCountry() != null) {
                Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__COUNTRY__CLAIM__ALREADY_CLAIMED.path()),
                        null, messageProvider.get(Message.PREFIX.path()));
                return;
            }
        }

        long claimDuration = 0;
        if (args.length > 1) {
            try {
                claimDuration = Long.parseLong(args[1]);
            } catch (Exception ex) {
                Messenger.sendMessage(player, messageProvider.get(Message.GENERAL_ERRORS__WRONG_NUMBER_FORMAT.path()),
                        null, messageProvider.get(Message.PREFIX.path()));
            }
        }

        region.setClaimantCountry(country);
        region.setClaimStartTime(System.currentTimeMillis());
        region.setClaimEndTime(System.currentTimeMillis() + (claimDuration * 1000));
        region.startClaimTask();

        UnitedLandsDataManager.instance().updateRegionDbData(region);

        Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__COUNTRY__CLAIM__SUCCESS.path()),
                Map.of("country", country.getCleanName(),
                        "region", region.getCleanName()),
                messageProvider.get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        if (args.length == 1) {
            return UnitedLandsDataManager.instance().getCountryNames();
        }
        return null;
    }

}

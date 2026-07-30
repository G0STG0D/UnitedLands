package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
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
            Messenger.sendMessage(player, messageProvider.get("admin.usage.country.claim"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var country = getCountry(player, args[0]);
        if (country == null) {
            return;
        }

        var region = UnitedLandsDataManager.instance()
                .getRegion(CoordinateUtils.locationToChunkCenterCoordinates(player.getLocation()));
        if (region == null) {
            Messenger.sendMessage(player, messageProvider.get("country.claim.no-region"),
                    null, messageProvider.get("prefix"));
            return;
        } else {
            if (region.getCountry() != null) {
                Messenger.sendMessage(player, messageProvider.get("country.claim.already-claimed"),
                        null, messageProvider.get("prefix"));
                return;
            }
        }

        long claimDuration = 0;
        if (args.length > 1) {
            try {
                claimDuration = Long.parseLong(args[1]);
            } catch (Exception ex) {
                Messenger.sendMessage(player, messageProvider.get("errors.wrong-number-format"),
                        null, messageProvider.get("prefix"));
            }
        }

        region.setClaimantCountry(country);
        region.setClaimStartTime(System.currentTimeMillis());
        region.setClaimEndTime(System.currentTimeMillis() + (claimDuration * 1000));
        region.startClaimTask();

        UnitedLandsDataManager.instance().updateRegionDbData(region);

        Messenger.sendMessage(player, messageProvider.get("admin.country.claim"),
                Map.of("country", country.getCleanName(),
                        "region", region.getCleanName()),
                messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        if (args.length == 1) {
            return UnitedLandsDataManager.instance().getCountryNames();
        }
        return null;
    }

}

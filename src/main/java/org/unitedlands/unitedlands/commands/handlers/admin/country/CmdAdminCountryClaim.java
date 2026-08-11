package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminCountry.class,
        name = "claim",
        description = "Claims a region for a country ",
        usage = "/ula country claim <country_name> <region_name> [claim_seconds]",
        catchAll = true
)
public class CmdAdminCountryClaim extends CountryAdminCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length < 2) {
            sendUsage(sender);
            return;
        }

        var country = getCountry(player, args[0]);
        if (country == null) {
            return;
        }

        var region = UnitedLandsDataManager.instance().getRegion(args[1]);
        if (region == null) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__COUNTRY__CLAIM__NO_REGION.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        } else {
            if (region.getCountry() != null) {
                Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__COUNTRY__CLAIM__ALREADY_CLAIMED.path()),
                        null, MessageProvider.instance().get(Message.PREFIX.path()));
                return;
            }
        }

        long claimDuration = 0;
        if (args.length > 1) {
            try {
                claimDuration = Long.parseLong(args[1]);
            } catch (Exception ex) {
                Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__WRONG_NUMBER_FORMAT.path()),
                        null, MessageProvider.instance().get(Message.PREFIX.path()));
            }
        }

        region.setClaimantCountry(country);
        region.setClaimStartTime(System.currentTimeMillis());
        region.setClaimEndTime(System.currentTimeMillis() + (claimDuration * 1000));
        region.startClaimTask();

        UnitedLandsDataManager.instance().updateRegionDbData(region);

        Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__COUNTRY__CLAIM__SUCCESS.path()),
                Map.of("country", country.getCleanName(),
                        "region", region.getCleanName()),
                MessageProvider.instance().get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        return switch (args.length) {
            case 1 -> UnitedLandsDataManager.instance().getCountryNames();
            case 2 -> UnitedLandsDataManager.instance().getRegionNames();
            default -> null;
        };
    }

}

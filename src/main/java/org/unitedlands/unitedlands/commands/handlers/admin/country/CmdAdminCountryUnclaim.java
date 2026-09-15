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
        name = "unclaim",
        description = "Unclaims a region",
        usage = "/ula country unclaim <region_name>",
        catchAll = true
)
public class CmdAdminCountryUnclaim extends CountryAdminCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var region = UnitedLandsDataManager.instance().getRegion(args[0]);
        if (region == null) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__COUNTRY__CLAIM__NO_REGION.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        } else {
            if (region.getCountry() == null) {
                Messenger.sendMessage(player,
                        MessageProvider.instance().get(Message.ADMIN__COUNTRY__UNCLAIM__NOT_CLAIMED.path()),
                        null, MessageProvider.instance().get(Message.PREFIX.path()));
                return;
            }
        }

        var country = region.getCountry();

        for (var settlement : region.getSettlements()) {
            for (var citizen : settlement.getCitizens()) {
                citizen.removeCountryRanks();
                UnitedLandsDataManager.instance().updateCitizenDbData(citizen);
            }
            settlement.removeCountry();
            UnitedLandsDataManager.instance().updateSettlementDbData(settlement, true);
        }
        region.removeCountry();
        UnitedLandsDataManager.instance().updateRegionDbData(region, true);

        country.removeRegion(region);
        UnitedLandsDataManager.instance().updateCountryDbData(country, true);

        Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__COUNTRY__UNCLAIM__SUCCESS.path()),
                Map.of("country", country.getCleanName(),
                        "region", region.getCleanName()),
                MessageProvider.instance().get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        return switch (args.length) {
            case 1 -> UnitedLandsDataManager.instance().getRegionNames();
            default -> null;
        };
    }

}

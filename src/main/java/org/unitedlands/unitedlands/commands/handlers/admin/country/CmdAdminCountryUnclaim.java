package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;

import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

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
            United.messenger().send(player, "admin.country.claim.no-region");
            return;
        } else {
            if (region.getCountry() == null) {
                United.messenger().send(player,
                        "admin.country.unclaim.not-claimed");
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

        United.messenger().send(player, "admin.country.unclaim.success", country.getCleanName(), region.getCleanName());
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        return switch (args.length) {
            case 1 -> UnitedLandsDataManager.instance().getRegionNames();
            default -> null;
        };
    }

}

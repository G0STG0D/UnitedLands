package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;

import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdAdminCountrySettlement.class,
        name = "remove",
        description = "Removes a settlement from a country",
        usage = "/ula country settlement remove <settlement_name>",
        catchAll = true
)
public class CmdAdminCountrySettlementRemove extends CountryAdminCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sendUsage(sender);
            return;
        }

        var country = getCountry(sender, args[0]);
        if (country == null) {
            return;
        }

        var settlement = getSettlement(sender, args[1]);
        if (settlement == null) {
            return;
        }

        if (!settlement.hasCountry() || !country.equals(settlement.getCountry())) {
            United.messenger().send(sender,
                    "admin.country.removesettlement.not-in-country", settlement.getName());
            return;
        }

        settlement.removeCountry();
        ;
        country.removeSettlement(settlement);

        UnitedLandsDataManager.instance().updateSettlementDbData(settlement, true);

        United.messenger().send(sender, "admin.country.removesettlement.success", country.getName(), settlement.getName());
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        switch (args.length) {
            case 1:
                return UnitedLandsDataManager.instance().getCountryNames();
            case 2:
                var country = UnitedLandsDataManager.instance().getCountry(args[0]);
                if (country != null)
                    return country.getSettlements().stream().map(Settlement::getName).collect(Collectors.toList());
        }
        return null;
    }

}

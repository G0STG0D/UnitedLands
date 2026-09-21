package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;

import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdAdminCountrySettlement.class,
        name = "add",
        description = "Adds a settlement to a player",
        usage = "/ula country settlement add <settlement_name>",
        catchAll = true
)
public class CmdAdminCountrySettlementAdd extends CountryAdminCommandHandler {

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

        if (settlement.hasCountry()) {
            United.messenger().send(sender, "admin.country.addsettlement.settlement-has-country",
                    Map.of("settlement", settlement.getName()));
            return;
        }

        settlement.setCountry(country);
        settlement.saveAndRender();

        country.addSettlement(settlement);

        United.messenger().send(sender, "admin.country.addsettlement.success", country.getName(), settlement.getName());
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        switch (args.length) {
            case 1:
                return UnitedLandsDataManager.instance().getCountryNames();
            case 2:
                return UnitedLandsDataManager.instance().getSettlementNames();
        }
        return null;
    }

}

package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;

import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdAdminSettlement.class,
        name = "delete",
        description = "Deletes a settlement",
        usage = "/ula settlement delete <settlement_name>"
)
public class CmdAdminSettlementDelete extends SettlementAdminCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 1) {
            sendUsage(sender);
            return;
        }

        var settlement = getSettlement(sender, args[0]);
        if (settlement == null) {
            return;
        }

        if (settlement.hasRegion()) {
            var region = settlement.getRegion();
            region.removeSettlement(settlement);
        }

        if (settlement.hasCountry()) {

            var country = settlement.getCountry();

            if (country.getCapital().equals(settlement)) {
                United.messenger().send(Bukkit.getServer(), "admin.settlement.delete.is-capital");
                return;
            }

            country.removeSettlement(settlement);
        }

        for (var settlementCitizen : settlement.getCitizens()) {
            settlementCitizen.removeSettlement();
            settlementCitizen.removeSettlementRanks();
            settlementCitizen.removeCountryRanks();
            settlementCitizen.save();
        }

        UnitedLandsEconomyManager.instance().deleteAccount(settlement.getUuid());

        UnitedLandsDataManager.instance().removeSettlementDbData(settlement);

        Pl3xMapRenderer.instance().removeSettlement(settlement);

        United.messenger().send(Bukkit.getServer(), "admin.settlement.delete.success", settlement.getCleanName());
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return UnitedLandsDataManager.instance().getSettlementNames();
        return null;
    }

}

package org.unitedlands.unitedlands.tasks;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.schedulers.NewDayScheduler;
import org.unitedlands.unitedlands.utils.CostUtils;
import org.unitedlands.utils.United;

import net.kyori.adventure.audience.Audience;

public class NewDayTask implements Runnable {


    @Override
    public void run() {

        United.messenger().send(Bukkit.getServer(), "new-day.start");

        var settlements = UnitedLandsDataManager.instance().getSettlements();

        //Map<Country, Double> countryTaxTotals = new HashMap<>();

        for (Settlement settlement : settlements) {

            // var totalTax = 0;
            // for (Citizen citizen : settlement.getCitizens()) {
            //     var citizenBalance = UnitedLandsEconomyManager.instance().getBalance(citizen.getUuid()).doubleValue();
            //     if (settlement.useTaxPercent()) {
            //         // Percentage tax
            //         var tax = citizenBalance * settlement.getTax();
            //         UnitedLandsEconomyManager.instance().withdraw(citizen.getUuid(), tax, "Daily taxes");
            //         totalTax += tax;
            //         notifyPlayer(citizen.getOfflinePlayer().getPlayer(), "new-day.citizen-tax-notice", tax);
            //     } else {
            //         // Flat tax
            //         var tax = settlement.getTax();
            //         if (citizenBalance >= tax) {
            //             UnitedLandsEconomyManager.instance().withdraw(citizen.getUuid(), tax, "Daily taxes");
            //             totalTax += tax;
            //             notifyPlayer(citizen.getOfflinePlayer().getPlayer(), "new-day.citizen-tax-notice", tax);
            //         } else {
            //             // TODO: kick citizen?
            //             notifyPlayer(settlement.getMayor().getOfflinePlayer().getPlayer(), "new-day.citizen-BANKRUPT", tax);
            //         }
            //     }
            // }

            // United.logger().info("Settlement " + settlement.getName() + " collected " + UnitedLandsEconomyManager.instance().format(totalTax) + " taxes.", "UnitedLands");
            // UnitedLandsEconomyManager.instance().deposit(settlement.getUuid(), totalTax, "Citizen taxes");
            // notifyPlayers(settlement.getOnlinePlayers(), "new-day.settlement-tax-notice", totalTax);

            var balance = UnitedLandsEconomyManager.instance().getBalance(settlement.getUuid()).doubleValue();
            var upkeep = CostUtils.getSettlementUpkeep(settlement);

            if (balance >= upkeep) {
                UnitedLandsEconomyManager.instance().withdraw(settlement.getUuid(), upkeep, "Daily upkeep");
                United.logger().info("Settlement " + settlement.getName() + " paid " + UnitedLandsEconomyManager.instance().format(upkeep) + " upkeep.", "UnitedLands");
                notifyPlayers(settlement.getOnlinePlayers(), "new-day.settlement-upkeep-notice", upkeep);
            } else {
                // Do town fall
            }

            if (settlement.hasCountry()) {

                // TODO: nation taxes
            }


        }

        for (Country country : UnitedLandsDataManager.instance().getCountries()) {
            for (Settlement settlement : country.getSettlements()) {
                
            }
        }

        NewDayScheduler.instance().finishNewDay();
    }

    private void notifyPlayer(Player player, String message, double amount) {
        if (player == null || !player.isOnline())
            return;
        United.messenger().send(player, message, UnitedLandsEconomyManager.instance().format(amount));
    }

    private void notifyPlayers(Set<Player> players, String message, double amount) {
        United.messenger().send(Audience.audience(players), message, UnitedLandsEconomyManager.instance().format(amount));
    }

}

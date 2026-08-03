package org.unitedlands.unitedlands.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.schedulers.NewDayScheduler;
import org.unitedlands.unitedlands.utils.CostUtils;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Logger;
import org.unitedlands.utils.Messenger;

import net.kyori.adventure.audience.Audience;

public class NewDayTask implements Runnable {

    private MessageProvider messageProvider = UnitedLands.instance().getMessageProvider();

    @Override
    public void run() {

        Messenger.sendMessage(Bukkit.getServer(), messageProvider.get(Message.NEW_DAY__START.path()));

        var settlements = UnitedLandsDataManager.instance().getSettlements();

        Map<Country, Double> countryTaxTotals = new HashMap<>();

        for (Settlement settlement : settlements) {

            var totalTax = 0;
            for (Citizen citizen : settlement.getCitizens()) {
                var citizenBalance = UnitedLandsEconomyManager.instance().getBalance(citizen.getUuid()).doubleValue();
                if (settlement.useTaxPercent()) {
                    // Percentage tax
                    var tax = citizenBalance * settlement.getTax();
                    UnitedLandsEconomyManager.instance().withdraw(citizen.getUuid(), tax);
                    totalTax += tax;
                    notifyPlayer(citizen.getPlayer().getPlayer(), Message.NEW_DAY__CITIZEN_TAX_NOTICE.path(), tax);
                } else {
                    // Flat tax
                    var tax = settlement.getTax();
                    if (citizenBalance >= tax) {
                        UnitedLandsEconomyManager.instance().withdraw(citizen.getUuid(), tax);
                        totalTax += tax;
                        notifyPlayer(citizen.getPlayer().getPlayer(), Message.NEW_DAY__CITIZEN_TAX_NOTICE.path(), tax);
                    } else {
                        // TODO: kick citizen?
                        notifyPlayer(settlement.getMayor().getPlayer().getPlayer(), Message.NEW_DAY__CITIZEN_BANKRUPT.path(), tax);
                    }
                }
            }

            Logger.log("Settlement " + settlement.getName() + " collected " + UnitedLandsEconomyManager.instance().format(totalTax) + " taxes.", "UnitedLands");
            UnitedLandsEconomyManager.instance().deposit(settlement.getUuid(), totalTax);
            notifyPlayers(settlement.getOnlinePlayers(), Message.NEW_DAY__SETTLEMENT_TAX_NOTICE.path(), totalTax);

            var balance = UnitedLandsEconomyManager.instance().getBalance(settlement.getUuid()).doubleValue();
            var upkeep = CostUtils.getSettlementUpkeep(settlement);

            if (balance >= upkeep) {
                UnitedLandsEconomyManager.instance().withdraw(settlement.getUuid(), upkeep);
                Logger.log("Settlement " + settlement.getName() + " paid " + UnitedLandsEconomyManager.instance().format(upkeep) + " upkeep.", "UnitedLands");
                notifyPlayers(settlement.getOnlinePlayers(), Message.NEW_DAY__SETTLEMENT_UPKEEP_NOTICE.path(), upkeep);
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
        Messenger.sendMessage(player, messageProvider.get(message), Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)), null);
    }

    private void notifyPlayers(Set<Player> players, String message, double amount) {
        Messenger.sendMessage(Audience.audience(players), messageProvider.get(message), Map.of("amount", UnitedLandsEconomyManager.instance().format(amount)),
                null);
    }

}

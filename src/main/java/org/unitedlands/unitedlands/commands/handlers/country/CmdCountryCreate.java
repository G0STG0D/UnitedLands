package org.unitedlands.unitedlands.commands.handlers.country;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Settings;

import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.United;

@UnitedSubCommand(
        parent = CmdCountry.class,
        name = "create",
        description = "Creates a new country",
        usage = "/country create <country_name>",
        playerOnly = true
)
public class CmdCountryCreate implements UnitedCommandExecutor {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length == 0) {
            sendUsage(sender);
            return;
        }

        var player = (Player) sender;
        var citizen = UnitedLandsDataManager.instance().getCitizen(player);
        if (citizen == null || citizen.getSettlement() == null) {
            United.messenger().send(player, "general-errors.not-in-settlement");
            return;
        }

        var settlement = citizen.getSettlement();
        var region = settlement.getRegion();

        if (region == null) {
            United.messenger().send(player, "general-errors.region-not-found");
            return;
        }

        if (region.getCountry() != null) {
            United.messenger().send(player, "player.country.create.region-occupied", region.getCountry().getCleanName());
            return;
        }

        if (!UnitedLandsEconomyManager.instance().has(citizen.getUuid(), new BigDecimal(Settings.countryCreateCosts))) {
            United.messenger().send(player, "general-errors.no-funds-player", UnitedLandsEconomyManager.instance().format(Settings.countryCreateCosts));
            return;
        }

        var confirmation = new Confirmation("country");
        confirmation.setRunnable(() -> {

            Country country = new Country();
            country.setUuid(UUID.randomUUID());
            country.setName(args[0]);
            country.setWorld(region.getWorld());
            country.setCapital(settlement);
            country.setFoundingTimestamp(System.currentTimeMillis());
            country.setFounder(player);
            country.setSpawn(settlement.getSpawn());
            country.setStrokeColor(Settings.defaultCountryStrokeColour);
            country.setFillColor(Settings.defaultCountryFillColour);

            country.addSettlement(settlement);
            country.addRegion(region);

            region.setCountry(country);
            region.setAdministrator(citizen);
            region.saveAndRender();

            settlement.setCountry(country);
            settlement.saveAndRender();

            citizen.addCountryRank("leader");
            citizen.save();

            UnitedLandsDataManager.instance().createCountryDbData(country);

            UnitedLandsEconomyManager.instance().createAccount(country.getUuid(), country.getName());
            UnitedLandsEconomyManager.instance().withdraw(citizen.getUuid(), Settings.countryCreateCosts, "Country creation costs");

            United.messenger().send(player, "player.country.create.player-message", country.getCleanName());
            United.messenger().send(Bukkit.getServer(), "player.country.create.broadcast-message", player.getName(), country.getCleanName(),
                    region.getCleanName());
        })
                .setTitle(United.messenger().get("player.country.create.create-confirm", args[0], UnitedLandsEconomyManager.instance().format(Settings.countryCreateCosts)))
                .setSender(player)
                .setReceiver(player)
                .send();
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

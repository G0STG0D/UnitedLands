package org.unitedlands.unitedlands.commands.handlers.country;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.registrars.command.UnitedCommandExecutor;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;


@UnitedSubCommand(
    parent          = CmdCountry.class,
    name            = "create",
    description     = "Creates a new country",
    usage           = "/country create <country_name>",
    playerOnly      = true
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
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__NOT_IN_SETTLEMENT.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        var settlement = citizen.getSettlement();
        var region = settlement.getRegion();

        if (region == null) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__REGION_NOT_FOUND.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (region.getCountry() != null) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.PLAYER__COUNTRY__CREATE__REGION_OCCUPIED.path()),
                    Map.of("country", region.getCountry().getCleanName()), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (!UnitedLandsEconomyManager.instance().has(citizen.getUuid(), new BigDecimal(Settings.countryCreateCosts))) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.GENERAL_ERRORS__NO_FUNDS_PLAYER.path()),
                    Map.of("amount", UnitedLandsEconomyManager.instance().format(Settings.countryCreateCosts)),
                    MessageProvider.instance().get(Message.PREFIX.path()));
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
            settlement.setCountry(country);

            citizen.addCountryRank("leader");

            UnitedLandsDataManager.instance().createCountryDbData(country);
            UnitedLandsDataManager.instance().updateRegionDbData(region);
            UnitedLandsDataManager.instance().updateSettlementDbData(settlement);
            UnitedLandsDataManager.instance().updateCitizenDbData(citizen);

            UnitedLandsEconomyManager.instance().createAccount(country.getUuid(), country.getName());
            UnitedLandsEconomyManager.instance().withdraw(citizen.getUuid(), Settings.countryCreateCosts);

            // TODO: Move string to config
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.PLAYER__COUNTRY__CREATE__PLAYER_MESSAGE.path()),
                    Map.of("country", country.getCleanName()), MessageProvider.instance().get(Message.PREFIX.path()));
            Messenger.sendMessage(Bukkit.getServer(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__CREATE__BROADCAST_MESSAGE.path()),
                    Map.of("player", player.getName(),
                            "country", country.getCleanName(),
                            "region", region.getCleanName()),
                    MessageProvider.instance().get(Message.PREFIX.path()));
        })
                .setTitle("Create country with name " + args[0] + "?")
                .setSender(player)
                .setReceiver(player)
                .send();
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

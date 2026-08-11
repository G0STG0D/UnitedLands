package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminCountry.class,
        name = "create",
        description = "Created a country with a capital",
        usage = "/ula country create <country_name> <capital>",
        playerOnly = true
)
public class CmdAdminCountryCreate extends CountryAdminCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sendUsage(sender);
            return;
        }

        var player = (Player) sender;

        if (!hasPermission(player)) {
            return;
        }

        var settlement = getSettlement(player, args[1]);
        if (settlement == null) {
            return;
        }

        var region = settlement.getRegion();

        if (region == null) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__COUNTRY__CREATE__NO_REGION.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (region.getCountry() != null) {
            Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__COUNTRY__CREATE__REGION_OCCUPIED.path()),
                    Map.of("country", region.getCountry().getCleanName()), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        Country country = new Country();
        country.setUuid(UUID.randomUUID());
        country.setName(args[0]);
        country.setWorld(region.getWorld());
        country.setCapital(settlement);
        country.setFoundingTimestamp(System.currentTimeMillis());
        country.setSpawn(settlement.getSpawn());
        country.setStrokeColor(Settings.defaultCountryStrokeColour);
        country.setFillColor(Settings.defaultCountryFillColour);

        country.addSettlement(settlement);
        country.addRegion(region);

        region.setCountry(country);
        settlement.setCountry(country);

        UnitedLandsDataManager.instance().createCountryDbData(country);
        UnitedLandsDataManager.instance().updateRegionDbData(region);
        UnitedLandsDataManager.instance().updateSettlementDbData(settlement);

        Messenger.sendMessage(player, MessageProvider.instance().get(Message.ADMIN__COUNTRY__CREATE__SUCCESS.path()),
                Map.of("country", country.getCleanName(), "settlement", settlement.getName(), "region", region.getName()),
                MessageProvider.instance().get(Message.PREFIX.path()));

    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return UnitedLandsDataManager.instance().getSettlementNames();
        }
        return null;
    }

}

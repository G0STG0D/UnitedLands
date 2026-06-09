package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class AdminCountryCreateCommand extends CountryAdminCommandHandler {

    public AdminCountryCreateCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length == 0)
            return;

        var player = (Player) sender;

        if (args.length != 2) {
            Messenger.sendMessage(player, messageProvider.get("admin.usage.country.create"),
                    null, messageProvider.get("prefix"));
            return;
        }

        if (!hasPermission(player)) {
            return;
        }

        var settlement = getSettlement(player, args[1]);
        if (settlement == null) {
            return;
        }

        var region = settlement.getRegion();

        if (region == null) {
            Messenger.sendMessage(player, messageProvider.get("country.create.no-region"),
                    null, messageProvider.get("prefix"));
            return;
        }

        if (region.getCountry() != null) {
            Messenger.sendMessage(player, messageProvider.get("country.create.region-occupied"),
                    Map.of("country", region.getCountry().getCleanName()), messageProvider.get("prefix"));
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

        GlobalDataManager.instance().createCountryDbData(country);
        GlobalDataManager.instance().updateRegionDbData(region);
        GlobalDataManager.instance().updateSettlementDbData(settlement);

        Pl3xMapRenderer.instance().renderRegion(region);
        Pl3xMapRenderer.instance().renderSettlement(settlement);
        Pl3xMapRenderer.instance().renderCountry(country);

        Messenger.sendMessage(player, messageProvider.get("admin.country.create"),
                Map.of("country", country.getCleanName()), messageProvider.get("prefix"));

    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return GlobalDataManager.instance().getSettlementNames();
        }
        return null;
    }

}

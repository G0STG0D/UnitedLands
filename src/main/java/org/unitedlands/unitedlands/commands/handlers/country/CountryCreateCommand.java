package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class CountryCreateCommand extends BaseCommandHandler<UnitedLands> {

    public CountryCreateCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length == 0)
            return;

        var player = (Player) sender;
        var citizen = GlobalDataManager.instance().getCitizen(player);
        if (citizen == null || citizen.getSettlement() == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.not-in-settlement"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var settlement = citizen.getSettlement();
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
        country.addRegion(region);
        country.setSpawn(settlement.getSpawn());
        country.setStrokeColor(Settings.getDefaultCountryStrokeColour());
        country.setFillColor(Settings.getDefaultCountryFillColour());
        country.addRegion(region);

        region.setCountry(country);
        settlement.setCountry(country);
        citizen.addCountryRank("country-leader");

        GlobalDataManager.instance().createCountryDbData(country);
        GlobalDataManager.instance().updateRegionDbData(region);
        GlobalDataManager.instance().updateSettlementDbData(settlement);
        GlobalDataManager.instance().updateCitizenDbData(citizen);

        plugin.getMapRenderer().renderRegion(region);
        plugin.getMapRenderer().renderSettlement(settlement);
        plugin.getMapRenderer().renderCountry(country);

        Messenger.sendMessage(player, messageProvider.get("country.create.player"),
                Map.of("country", country.getCleanName()), messageProvider.get("prefix"));
        Messenger.sendMessage(Bukkit.getServer(), messageProvider.get("country.create.broadcast"),
                Map.of("player", player.getName(),
                        "country", country.getCleanName(),
                        "region", region.getCleanName()),
                messageProvider.get("prefix"));

    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.unitedlands.utils.CostUtils;
import org.unitedlands.utils.Messenger;

public class CountryClaimCommand extends CountryCommandHandler {

    public CountryClaimCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;
        var citizen = UnitedLandsDataManager.instance().getCitizen(player);
        if (citizen == null || citizen.getCountry() == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.not-in-country"),
                    null, messageProvider.get("prefix"));
            return;
        }

        if (!hasPermission("country.setcolor", citizen))
            return;
        
        var country = citizen.getCountry();

        var region = UnitedLandsDataManager.instance()
                .getRegion(CoordinateUtils.locationToChunkCenterCoordinates(player.getLocation()));
        if (region == null) {
            Messenger.sendMessage(player, messageProvider.get("country.claim.no-region"),
                    null, messageProvider.get("prefix"));
            return;
        } else {
            if (region.getCountry() != null) {
                Messenger.sendMessage(player, messageProvider.get("country.claim.already-claimed"),
                        null, messageProvider.get("prefix"));
                return;
            }
        }

        var claimCost = CostUtils.getRegionClaimCosts(country, region);

        Confirmation confirmation = new Confirmation("region-claim");
        confirmation.setRunnable(() -> {

            region.setCountry(country);
            country.addRegion(region);
            UnitedLandsDataManager.instance().updateRegionDbData(region);

            Pl3xMapRenderer.instance().removeRegion(region);
            Pl3xMapRenderer.instance().renderPolyRegion(region);
            Pl3xMapRenderer.instance().renderCountry(country);

            Messenger.sendMessage(player, messageProvider.get("country.claim.success"),
                    Map.of("country", country.getCleanName(),
                            "region", region.getCleanName()),
                    messageProvider.get("prefix"));
            Messenger.sendMessage(Bukkit.getServer(), messageProvider.get("country.claim.broadcast"),
                    Map.of("player", player.getName(),
                            "country", country.getCleanName(),
                            "region", region.getCleanName()),
                    messageProvider.get("prefix"));

        })
                .setTitle(
                        "Claiming this region will cost " + UnitedLandsEconomyManager.instance().format(claimCost) + ". Continue?")
                .setSender(player)
                .setReceiver(player)
                .setAcceptCommand("/approve region-claim")
                .setDiscriminator(region.getName())
                .setTimeoutSeconds(30)
                .send();

    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

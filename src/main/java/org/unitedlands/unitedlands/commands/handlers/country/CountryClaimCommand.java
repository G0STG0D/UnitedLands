package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.classes.BaseCommandHandler;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.utils.Messenger;

public class CountryClaimCommand extends BaseCommandHandler<UnitedLands> {

    public CountryClaimCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;
        var citizen = GlobalDataManager.instance().getCitizen(player);
        if (citizen == null || citizen.getCountry() == null) {
            Messenger.sendMessage(player, messageProvider.get("errors.not-in-country"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var country = citizen.getCountry();

        var region = GlobalDataManager.instance().getRegion(CoordinateUtils.locationToRegionCoordinates(player.getLocation()));
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

        region.setCountry(country);
        country.addRegion(region);
        GlobalDataManager.instance().updateRegionDbData(region);

        Pl3xMapRenderer.instance().renderRegion(region);
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

    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

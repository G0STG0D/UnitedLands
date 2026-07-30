package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.utils.Messenger;

public class CountryUnclaimCommand extends CountryCommandHandler {

    public CountryUnclaimCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, "country.unclaim");
        if (context == null)
            return;

        var region = UnitedLandsDataManager.instance().getRegion(CoordinateUtils.locationToChunkCenterCoordinates(context.player().getLocation()));
        if (region == null) {
            Messenger.sendMessage(context.player(), messageProvider.get("country.unclaim.no-region"), null, messageProvider.get("prefix"));
            return;
        } else {

            if (context.country().getRegionCount() == 1) {
                Messenger.sendMessage(context.player(), messageProvider.get("country.unclaim.last-region"), null, messageProvider.get("prefix"));
                return;
            }

            // Not claied and not being claimed
            if (region.getCountry() == null && region.getClaimantCountry() == null) {
                Messenger.sendMessage(context.player(), messageProvider.get("country.unclaim.region-not-claimed"), null, messageProvider.get("prefix"));
                return;
            }

            if (region.getCountry() != null) {
                if (!context.country().equals(region.getCountry())) {
                    // Claimed, but by someone else
                    Messenger.sendMessage(context.player(), messageProvider.get("country.unclaim.region-not-in-country"), null, messageProvider.get("prefix"));
                    return;
                } else {
                    // Claimed by country

                    Player player = (Player) sender;

                    var confirmTitle = messageProvider.get("country.unclaim.confirm-title");
                    var countrySettlements = region.getSettlements(context.country());
                    if (countrySettlements.size() > 0) {
                        confirmTitle += " " + messageProvider.get("country.unclaim.confirm-warn-settlements");
                        var citizen = UnitedLandsDataManager.instance().getCitizen(player);
                        if (citizen != null && countrySettlements.stream().anyMatch(s -> s.getCitizens().contains(citizen))) {
                            confirmTitle += " " + messageProvider.get("country.unclaim.confirm-warn-leader");
                        }
                    }

                    Confirmation confirmation = new Confirmation("region-unclaim");
                    confirmation.setRunnable(() -> {

                        for (var countrySettlement : countrySettlements) {
                            for (var citizen : countrySettlement.getCitizens()) {
                                citizen.removeCountryRanks();
                                UnitedLandsDataManager.instance().updateCitizenDbData(citizen);
                            }
                            countrySettlement.removeCountry();
                            UnitedLandsDataManager.instance().updateSettlementDbData(countrySettlement);
                        }

                        region.removeCountry();
                        context.country().removeRegion(region);

                        UnitedLandsDataManager.instance().updateRegionDbData(region);
                        UnitedLandsDataManager.instance().updateCountryDbData(context.country());

                        Messenger.sendMessage(context.player(), messageProvider.get("country.unclaim.success"), null, messageProvider.get("prefix"));
                    }).setTitle(confirmTitle).setAcceptCommand("/approve region-unclaim").setTimeoutSeconds(20).setSender(player).setReceiver(player).send();

                }
            } else {
                if (region.getClaimantCountry() != null) {
                    if (!context.country().equals(region.getClaimantCountry())) {
                        // Being claimed, but by someone else
                        Messenger.sendMessage(context.player(), messageProvider.get("country.unclaim.other-country-claiming"), null,
                                messageProvider.get("prefix"));
                        return;
                    } else {
                        // Being claimed by country
                        region.removeClaimantCountry();
                        region.setClaimStartTime(null);
                        region.setClaimEndTime(null);
                        UnitedLandsDataManager.instance().updateRegionDbData(region);
                        Messenger.sendMessage(context.player(), messageProvider.get("country.unclaim.success"), null, messageProvider.get("prefix"));
                    }
                }
            }
        }
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

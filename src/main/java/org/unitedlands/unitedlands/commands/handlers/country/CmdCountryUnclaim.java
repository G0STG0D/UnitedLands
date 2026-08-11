package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
    parent          = CmdCountry.class,
    name            = "unclaim",
    description     = "Unclaims a region",
    usage           = "/country unclaim",
    playerOnly      = true
)
public class CmdCountryUnclaim extends CountryCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, "country.unclaim");
        if (context == null)
            return;

        var region = UnitedLandsDataManager.instance().getRegion(CoordinateUtils.locationToChunkCenterCoordinates(context.player().getLocation()));
        if (region == null) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__UNCLAIM__NO_REGION.path()), null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        } else {

            if (context.country().getRegionCount() == 1) {
                Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__UNCLAIM__LAST_REGION.path()), null, MessageProvider.instance().get(Message.PREFIX.path()));
                return;
            }

            // Not claied and not being claimed
            if (region.getCountry() == null && region.getClaimantCountry() == null) {
                Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__UNCLAIM__REGION_NOT_CLAIMED.path()), null,
                        MessageProvider.instance().get(Message.PREFIX.path()));
                return;
            }

            if (region.getCountry() != null) {
                if (!context.country().equals(region.getCountry())) {
                    // Claimed, but by someone else
                    Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__UNCLAIM__REGION_NOT_OWNED.path()), null,
                            MessageProvider.instance().get(Message.PREFIX.path()));
                    return;
                } else {
                    // Claimed by country

                    Player player = (Player) sender;

                    var confirmTitle = MessageProvider.instance().get(Message.PLAYER__COUNTRY__UNCLAIM__CONFIRM.path());
                    var countrySettlements = region.getSettlements(context.country());
                    if (countrySettlements.size() > 0) {
                        confirmTitle += " " + MessageProvider.instance().get(Message.PLAYER__COUNTRY__UNCLAIM__CONFIRM_WARN_SETTLEMENTS.path());
                        var citizen = UnitedLandsDataManager.instance().getCitizen(player);
                        if (citizen != null && countrySettlements.stream().anyMatch(s -> s.getCitizens().contains(citizen))) {
                            confirmTitle += " " + MessageProvider.instance().get(Message.PLAYER__COUNTRY__UNCLAIM__CONFIRM_WARN_LEADER.path());
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

                        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__UNCLAIM__SUCCESS.path()), null,
                                MessageProvider.instance().get(Message.PREFIX.path()));
                    })
                            .setTitle(confirmTitle)
                            .setSender(player)
                            .setReceiver(player)
                            .send();

                }
            } else {
                if (region.getClaimantCountry() != null) {
                    if (!context.country().equals(region.getClaimantCountry())) {
                        // Being claimed, but by someone else
                        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__UNCLAIM__OTHER_COUNTRY_CLAIMING.path()), null,
                                MessageProvider.instance().get(Message.PREFIX.path()));
                        return;
                    } else {
                        // Being claimed by country
                        region.removeClaimantCountry();
                        region.setClaimStartTime(null);
                        region.setClaimEndTime(null);
                        UnitedLandsDataManager.instance().updateRegionDbData(region);
                        Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__UNCLAIM__SUCCESS.path()), null,
                                MessageProvider.instance().get(Message.PREFIX.path()));
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

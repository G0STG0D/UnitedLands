package org.unitedlands.unitedlands.commands.handlers.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryCommandHandler;
import org.unitedlands.unitedlands.classes.events.region.RegionClaimStartEvent;
import org.unitedlands.unitedlands.classes.events.region.RegionDoubleClaimEvent;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.CoordinateUtils;
import org.unitedlands.unitedlands.utils.CostUtils;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Messenger;


@UnitedSubCommand(
    parent          = CmdCountry.class,
    name            = "claim",
    description     = "Starts a region claim for a country",
    usage           = "/country claim",
    playerOnly      = true
)
public class CmdCountryClaim extends CountryCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, "country.claim");
        if (context == null)
            return;

        var region = UnitedLandsDataManager.instance()
                .getRegion(CoordinateUtils.locationToChunkCenterCoordinates(context.player().getLocation()));
        if (region == null) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__CLAIM__NO_REGION.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        } else {
            if (region.getCountry() != null) {
                Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__CLAIM__ALREADY_CLAIMED.path()),
                        Map.of("country", region.getCountry().getName()), MessageProvider.instance().get(Message.PREFIX.path()));
                return;
            }
        }

        var currentClaims = UnitedLandsDataManager.instance().getRegionClaimsOngoing(context.country());
        if (currentClaims.size() >= Settings.regionParallelClaimsMax) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__CLAIM__TOO_MANY_CLAIMING.path()),
                    Map.of("max", String.valueOf(Settings.regionParallelClaimsMax)), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        var claimCost = CostUtils.getRegionClaimCosts(context.country(), region);
        
        // TODO: Check claim costs

        // RegionPreClaimStartEvent preStartClaimEvent = new
        // RegionPreClaimStartEvent(region, context.country());
        // preStartClaimEvent.callEvent();
        // if (preStartClaimEvent.isCancelled())
        // return;

        // TODO: Move to config
        var confirmationMessage = "Claiming this region will take " + Formatter.formatDuration(Settings.regionClaimTime * 1000)
                                + " and cost " + UnitedLandsEconomyManager.instance().format(claimCost) + ". Continue?";
        var doubleClaim = false;

        if (region.getClaimantCountry() != null) {

            // Notify other plugins (e.g. UnitedWars) of the double claim attempt. They can
            // decide to uncancel the double claim event to execute other logic on the
            // actual claim event. If they don't, prevent the double claiming by default.
            RegionDoubleClaimEvent doubleClaimEvent = new RegionDoubleClaimEvent(
                    (Player)sender,
                    region,
                    context.country(),
                    region.getClaimantCountry());

            doubleClaimEvent.setCancelled(true);
            doubleClaimEvent.callEvent();

            if (doubleClaimEvent.isCancelled()) {
                Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__COUNTRY__CLAIM__ALREADY_BEING_CLAIMED.path()),
                        Map.of("country", region.getClaimantCountry().getCleanName()), MessageProvider.instance().get(Message.PREFIX.path()));
                return;
            }

            // Get the other plugin's alternate confirmation message
            if (doubleClaimEvent.getConfirmationMessage() != null)
                confirmationMessage = doubleClaimEvent.getConfirmationMessage();
            doubleClaim = true;

        }

        var finalDoubleClaim = doubleClaim;
        Confirmation confirmation = new Confirmation("region-claim");
        confirmation.setRunnable(() -> {

            // Allow other plugins (like UnitedWars) cancel the event and execute other code
            // instead.
            RegionClaimStartEvent startClaimEvent = new RegionClaimStartEvent((Player)sender, region, context.country(), finalDoubleClaim);
            startClaimEvent.callEvent();
            if (startClaimEvent.isCancelled())
                return;

            region.setClaimantCountry(context.country());
            region.setClaimStartTime(System.currentTimeMillis());
            region.setClaimEndTime(System.currentTimeMillis() + (Settings.regionClaimTime * 1000));
            region.startClaimTask();

            UnitedLandsDataManager.instance().updateRegionDbData(region);

            // Pl3xMapRenderer.instance().renderPolyRegion(region);

        })
                .setTitle(confirmationMessage)
                .setSender(context.player())
                .setReceiver(context.player())
                .send();

    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

}

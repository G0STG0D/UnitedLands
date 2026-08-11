package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdSettlement.class,
        name = "invite",
        description = "Joins the region's country with a settlement",
        usage = "/settlement joincountry",
        playerOnly = true
)
public class CmdSettlementJoinCountry extends SettlementCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {


        var context = validate(sender, "settlement.joincountry");
        if (context == null)
            return;
        
        if (context.settlement().hasCountry()) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__JOINCOUNTRY__ALREADY_IN_COUNTRY.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }
        if (!context.settlement().hasRegion()) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__JOINCOUNTRY__NO_REGION.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        var region = context.settlement().getRegion();
        if (!region.hasCountry()) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__JOINCOUNTRY__NO_COUNTRY.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        var country = region.getCountry();

        Confirmation join = new Confirmation("country-join");
        join.setRunnable(() -> {

            context.settlement().setCountry(country);
            country.addSettlement(context.settlement());

            UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

            Messenger.sendMessage(Bukkit.getServer(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__JOINCOUNTRY__BROADCAST_MESSAGE.path()),
                    Map.of("settlement", context.settlement().getCleanName(), "country", country.getCleanName()),
                    MessageProvider.instance().get(Message.PREFIX.path()));
        })
                .setTitle(MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__JOINCOUNTRY__CONFIRM.path()))
                .setReplacements(Map.of("country", country.getCleanName()))
                .setSender(context.player())
                .setReceiver(context.player())
                .send();

    }

}

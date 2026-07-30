package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class SettlementJoinCountryCommand extends SettlementCommandHandler {

    public SettlementJoinCountryCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 0)
            // TODO: Usage
            return;

        var context = validate(sender, "settlement.joincountry");
        if (context == null)
            return;
        
        if (context.settlement().hasCountry()) {
            Messenger.sendMessage(context.player(), messageProvider.get("settlement.joincountry.already-in-country"),
                    null, messageProvider.get("prefix"));
            return;
        }
        if (!context.settlement().hasRegion()) {
            Messenger.sendMessage(context.player(), messageProvider.get("settlement.joincountry.no-region"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var region = context.settlement().getRegion();
        if (!region.hasCountry()) {
            Messenger.sendMessage(context.player(), messageProvider.get("settlement.joincountry.no-country"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var country = region.getCountry();

        Confirmation join = new Confirmation("country-join");
        join.setRunnable(() -> {

            context.settlement().setCountry(country);
            country.addSettlement(context.settlement());

            UnitedLandsDataManager.instance().updateSettlementDbData(context.settlement());

            Messenger.sendMessage(Bukkit.getServer(), messageProvider.get("settlement.joincountry.broadcast"),
                    Map.of("settlement", context.settlement().getCleanName(), "country", country.getCleanName()),
                    messageProvider.get("prefix"));
        })
                .setTitle(messageProvider.get("settlement.joincountry.confirm"))
                .setReplacements(Map.of("country", country.getCleanName()))
                .setSender(context.player())
                .setReceiver(context.player())
                .setDiscriminator(country.getName())
                .setAcceptCommand("/approve country-join")
                .setCancelCommand("/cancel country-join")
                .setTimeoutSeconds(60)
                .send();

    }

}

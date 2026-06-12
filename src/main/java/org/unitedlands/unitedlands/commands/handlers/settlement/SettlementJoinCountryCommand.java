package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
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

        var player = (Player) sender;
        var citizen = getCitizen(player);
        if (citizen == null)
            return;
        var settlement = getCitizenSettlement(citizen);
        if (settlement == null)
            return;

        if (!hasPermission("settlement.joincountry", citizen))
            return;

        if (settlement.hasCountry()) {
            Messenger.sendMessage(player, messageProvider.get("settlement.joincountry.already-in-country"),
                    null, messageProvider.get("prefix"));
            return;
        }
        if (!settlement.hasRegion()) {
            Messenger.sendMessage(player, messageProvider.get("settlement.joincountry.no-region"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var region = settlement.getRegion();
        if (!region.hasCountry()) {
            Messenger.sendMessage(player, messageProvider.get("settlement.joincountry.no-country"),
                    null, messageProvider.get("prefix"));
            return;
        }

        var country = region.getCountry();

        Confirmation join = new Confirmation("country-join");
        join.setRunnable(() -> {

            settlement.setCountry(country);
            country.addSettlement(settlement);

            GlobalDataManager.instance().updateSettlementDbData(settlement);

            Pl3xMapRenderer.instance().renderSettlement(settlement);

            Messenger.sendMessage(Bukkit.getServer(), messageProvider.get("settlement.joincountry.broadcast"),
                    Map.of("settlement", settlement.getCleanName(), "country", country.getCleanName()),
                    messageProvider.get("prefix"));
        })
                .setTitle(messageProvider.get("settlement.joincountry.confirm"))
                .setReplacements(Map.of("country", country.getCleanName()))
                .setSender(player)
                .setReceiver(player)
                .setDiscriminator(country.getName())
                .setAcceptCommand("/approve country-join")
                .setCancelCommand("/cancel country-join")
                .setTimeoutSeconds(60)
                .send();

    }

}

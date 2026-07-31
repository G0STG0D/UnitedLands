package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class AdminCountryRemoveSettlementCommand extends CountryAdminCommandHandler {

    public AdminCountryRemoveSettlementCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 2) {
            Messenger.sendMessage(player, messageProvider.get("admin.usage.country.removesettlement"),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        var country = getCountry(player, args[0]);
        if (country == null) {
            return;
        }

        var settlement = getSettlement(player, args[1]);
        if (settlement == null) {
            return;
        }

        if (!settlement.hasCountry() || !country.equals(settlement.getCountry())) {
            Messenger.sendMessage(player,
                    messageProvider.get("admin.country.removesettlement.settlement-not-in-country"),
                    null, messageProvider.get(Message.PREFIX.path()));
            return;
        }

        settlement.removeCountry();
        ;
        country.removeSettlement(settlement);

        UnitedLandsDataManager.instance().updateSettlementDbData(settlement);

        Messenger.sendMessage(player, messageProvider.get("admin.country.removesettlement.success"),
                Map.of("country", country.getName(), "settlement", settlement.getName()),
                messageProvider.get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        switch (args.length) {
            case 1:
                return UnitedLandsDataManager.instance().getCountryNames();
            case 2:
                var country = UnitedLandsDataManager.instance().getCountry(args[0]);
                if (country != null)
                    return country.getSettlements().stream().map(Settlement::getName).collect(Collectors.toList());
        }
        return null;
    }

}

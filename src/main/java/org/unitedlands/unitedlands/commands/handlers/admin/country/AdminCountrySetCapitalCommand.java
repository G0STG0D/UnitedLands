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

public class AdminCountrySetCapitalCommand extends CountryAdminCommandHandler {

    public AdminCountrySetCapitalCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
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

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 2) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__COUNTRY__SETCAPITAL__USAGE.path()),
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
                    messageProvider.get(Message.ADMIN__COUNTRY__SETCAPITAL__SETTLEMENT_NOT_IN_COUNTRY.path()),
                    Map.of("settlement", settlement.getName()), messageProvider.get(Message.PREFIX.path()));
            return;
        }

        country.setCapital(settlement);

        UnitedLandsDataManager.instance().updateCountryDbData(country);

        Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__COUNTRY__SETCAPITAL__SUCCESS.path()),
                Map.of("country", country.getName(), "settlement", settlement.getName()),
                messageProvider.get(Message.PREFIX.path()));
    }


}

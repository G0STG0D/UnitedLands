package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class AdminCountryAddSettlementCommand extends CountryAdminCommandHandler {

    public AdminCountryAddSettlementCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 2) {
            Messenger.sendMessage(player, messageProvider.get("admin.usage.country.addsettlement"),
                    null, messageProvider.get("prefix"));
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

        if (settlement.hasCountry()) {
            Messenger.sendMessage(player, messageProvider.get("admin.country.addsettlement.settlement-has-country"),
                    null, messageProvider.get("prefix"));
            return;
        }

        settlement.setCountry(country);
        country.addSettlement(settlement);

        UnitedLandsDataManager.instance().updateSettlementDbData(settlement);

        Messenger.sendMessage(player, messageProvider.get("admin.country.addsettlement.success"),
                Map.of("country", country.getName(), "settlement", settlement.getName()),
                messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender arg0, String[] args) {
        switch (args.length) {
            case 1:
                return UnitedLandsDataManager.instance().getCountryNames();
            case 2:
                return UnitedLandsDataManager.instance().getSettlementNames();
        }
        return null;
    }

}

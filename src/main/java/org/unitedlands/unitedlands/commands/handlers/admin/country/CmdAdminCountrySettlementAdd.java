package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminCountrySettlement.class,
        name = "add",
        description = "Adds a settlement to a player",
        usage = "/ula country settlement add <settlement_name>",
        catchAll = true
)
public class CmdAdminCountrySettlementAdd extends CountryAdminCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sendUsage(sender);
            return;
        }

        var country = getCountry(sender, args[0]);
        if (country == null) {
            return;
        }

        var settlement = getSettlement(sender, args[1]);
        if (settlement == null) {
            return;
        }

        if (settlement.hasCountry()) {
            Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__COUNTRY__ADDSETTLEMENT__SETTLEMENT_HAS_COUNTRY.path()),
                    Map.of("settlement", settlement.getName()), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        settlement.setCountry(country);
        country.addSettlement(settlement);

        UnitedLandsDataManager.instance().updateSettlementDbData(settlement);

        Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__COUNTRY__ADDSETTLEMENT__SUCCESS.path()),
                Map.of("country", country.getName(), "settlement", settlement.getName()),
                MessageProvider.instance().get(Message.PREFIX.path()));
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

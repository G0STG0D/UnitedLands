package org.unitedlands.unitedlands.commands.handlers.admin.country;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.commandhandlers.CountryAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminCountrySet.class,
        name = "capital",
        description = "Sets a country capital",
        usage = "/ula country set capital <settlement_name>",
        catchAll = true
)
public class CmdAdminCountrySetCapital extends CountryAdminCommandHandler {

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

        if (!settlement.hasCountry() || !country.equals(settlement.getCountry())) {
            Messenger.sendMessage(sender,
                    MessageProvider.instance().get(Message.ADMIN__COUNTRY__SETCAPITAL__SETTLEMENT_NOT_IN_COUNTRY.path()),
                    Map.of("settlement", settlement.getName()), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        country.setCapital(settlement);

        UnitedLandsDataManager.instance().updateCountryDbData(country, true);

        Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__COUNTRY__SETCAPITAL__SUCCESS.path()),
                Map.of("country", country.getName(), "settlement", settlement.getName()),
                MessageProvider.instance().get(Message.PREFIX.path()));
    }

}

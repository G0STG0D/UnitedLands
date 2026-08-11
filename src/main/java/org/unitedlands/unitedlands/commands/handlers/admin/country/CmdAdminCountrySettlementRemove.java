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
        parent = CmdAdminCountrySettlement.class,
        name = "remove",
        description = "Removes a settlement from a country",
        usage = "/ula country settlement remove <settlement_name>",
        catchAll = true
)
public class CmdAdminCountrySettlementRemove extends CountryAdminCommandHandler {

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
                    MessageProvider.instance().get(Message.ADMIN__COUNTRY__REMOVESETTLEMENT__NOT_IN_COUNTRY.path()),
                    Map.of("settlement", settlement.getName()), MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        settlement.removeCountry();
        ;
        country.removeSettlement(settlement);

        UnitedLandsDataManager.instance().updateSettlementDbData(settlement);

        Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__COUNTRY__REMOVESETTLEMENT__SUCCESS.path()),
                Map.of("country", country.getName(), "settlement", settlement.getName()),
                MessageProvider.instance().get(Message.PREFIX.path()));
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

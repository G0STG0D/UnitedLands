package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.classes.events.settlement.SettlementPlayerLeaveEvent;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminSettlementCitizen.class,
        name = "remove",
        description = "Removes a citizen from a settlement",
        usage = "/ula settlement citizen remove <settlement_name> <player>",
        catchAll = true
)
public class CmdAdminSettlementCitizenRemove extends SettlementAdminCommandHandler {

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sendUsage(sender);
            return;
        }

        var settlement = getSettlement(sender, args[0]);
        if (settlement == null) {
            return;
        }

        var citizen = getCitizen(sender, args[1]);
        if (citizen == null) {
            return;
        }

        if (citizen.getSettlement() == null || !citizen.getSettlement().equals(settlement)) {
            Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__SETTLEMENT__CITIZEN_NOT_IN_SETTLEMENT.path()),
                    null, MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        settlement.removeCitizen(citizen);

        citizen.removeCountryRanks();
        citizen.removeSettlementRanks();
        citizen.removeSettlement();

        (new SettlementPlayerLeaveEvent(settlement, citizen.getPlayer().getPlayer())).callEvent();

        UnitedLandsDataManager.instance().updateSettlementDbData(settlement, true);
        UnitedLandsDataManager.instance().updateCitizenDbData(citizen);

        Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__SETTLEMENT__REMOVECITIZEN__SUCCESS.path()),
                Map.of("settlement", args[0], "name", args[1]), MessageProvider.instance().get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return UnitedLandsDataManager.instance().getSettlementNames();
            case 2:
                var settlement = UnitedLandsDataManager.instance().getSettlement(args[0]);
                return settlement.getCitizens().stream().map(Citizen::getName).collect(Collectors.toList());
        }
        return null;
    }

}

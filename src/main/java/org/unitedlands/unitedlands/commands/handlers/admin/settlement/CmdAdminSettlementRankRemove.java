package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
        parent = CmdAdminSettlementRank.class,
        name = "remove",
        description = "Removes a rank from a citizen",
        usage = "/ula settlement rank remove <settlement_name> <player> <rank>",
        catchAll = true
)
public class CmdAdminSettlementRankRemove extends SettlementAdminCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {

        switch (args.length) {
            case 1:
                return UnitedLandsDataManager.instance().getSettlementNames();
            case 2:
                var settlement = UnitedLandsDataManager.instance().getSettlement(args[0]);
                if (settlement != null)
                    return settlement.getCitizens().stream().map(Citizen::getName).collect(Collectors.toList());
            case 3:
                return PermissionManager.instance().getCountryRanks();
        }
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        if (args.length != 3) {
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

        if (!citizen.hasSettlement() || !settlement.equals(citizen.getSettlement())) {
            Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__SETTLEMENT__CITIZEN_NOT_IN_SETTLEMENT.path()),
                    Map.of("citizen", citizen.getName()),
                    MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        if (!PermissionManager.instance().getSettlementRanks().contains(args[2])) {
            Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__SETTLEMENT__UNKNOWN_RANK.path()), Map.of("rank", args[2]),
                    MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        citizen.removeSettlementRank(args[2]);
        UnitedLandsDataManager.instance().updateCitizenDbData(citizen);

        Messenger.sendMessage(sender, MessageProvider.instance().get(Message.ADMIN__SETTLEMENT__REMOVERANK__SUCCESS.path()),
                Map.of("rank", args[2], "citizen", citizen.getName(), "settlement", settlement.getName()),
                MessageProvider.instance().get(Message.PREFIX.path()));

    }

}

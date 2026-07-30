package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.utils.Messenger;

public class AdminSettlementRankAddCommand extends SettlementAdminCommandHandler {

    public AdminSettlementRankAddCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);

    }

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
            Messenger.sendMessage(sender, messageProvider.get("admin.usage.settlement.addrank"), null, messageProvider.get("prefix"));
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
            Messenger.sendMessage(sender, messageProvider.get("admin.settlement.addrank.citizen-not-in-settlement"), Map.of("citizen", citizen.getName()),
                    messageProvider.get("prefix"));
            return;
        }

        if (!PermissionManager.instance().getSettlementRanks().contains(args[2])) {
            Messenger.sendMessage(sender, messageProvider.get("admin.settlement.addrank.unknown-rank"), Map.of("rank", args[2]), messageProvider.get("prefix"));
            return;
        }

        citizen.addSettlementRank(args[2]);
        UnitedLandsDataManager.instance().updateCitizenDbData(citizen);

        Messenger.sendMessage(sender, messageProvider.get("admin.settlement.addrank.success"),
                Map.of("rank", args[2], "citizen", citizen.getName(), "settlement", settlement.getName()), messageProvider.get("prefix"));

    }

}

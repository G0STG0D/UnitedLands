package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Messenger;

public class AdminSettlementRemoveCitizenCommand extends SettlementAdminCommandHandler {

    public AdminSettlementRemoveCitizenCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 2) {
            Messenger.sendMessage(player, messageProvider.get("admin.usage.settlement.removecitizen"),
                    null, messageProvider.get("prefix"));
            return;
        }
        if (!hasPermission(player)) {
            return;
        }

        var settlement = getSettlement(player, args[0]);
        if (settlement == null) {
            return;
        }

        var citizen = getCitizen(player, args[1]);
        if (citizen == null) {
            return;
        }

        if (citizen.getSettlement() == null || !citizen.getSettlement().equals(settlement)) {
            Messenger.sendMessage(player, messageProvider.get("admin.settlement.removecitizen.not-in-settlement"),
                    null, messageProvider.get("prefix"));
            return;
        }

        settlement.removeCitizen(citizen);

        citizen.removeCountryRanks();
        citizen.removeSettlementRanks();
        citizen.removeSettlement();

        GlobalDataManager.instance().updateSettlementDbData(settlement);
        GlobalDataManager.instance().updateCitizenDbData(citizen);

        Messenger.sendMessage(player, messageProvider.get("admin.settlement.removecitizen.success"),
                Map.of("settlement", args[0], "name", args[1]), messageProvider.get("prefix"));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return GlobalDataManager.instance().getSettlementNames();
            case 2:
                var settlement = GlobalDataManager.instance().getSettlement(args[0]);
                return settlement.getCitizens().stream().map(Citizen::getName).collect(Collectors.toList());
        }
        return null;
    }

}

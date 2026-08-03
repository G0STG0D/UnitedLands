package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class AdminSettlementAddCitizenCommand extends SettlementAdminCommandHandler {

    public AdminSettlementAddCitizenCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 2) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__ADDCITIZEN__USAGE.path()),
                    null, messageProvider.get(Message.PREFIX.path()));
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

        if (citizen.getSettlement() != null) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__ADDCITIZEN__ALREADY_IN_SETTLEMENT.path()),
                    Map.of("citizen", citizen.getName(), "settlement", settlement.getName()), messageProvider.get(Message.PREFIX.path()));
            return;
        }

        settlement.addCitizen(citizen);
        citizen.setSettlement(settlement);

        UnitedLandsDataManager.instance().updateSettlementDbData(settlement);
        UnitedLandsDataManager.instance().updateCitizenDbData(citizen);

        Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__ADDCITIZEN__SUCCESS.path()),
                Map.of("citizen", citizen.getName(), "settlement", settlement.getName()), messageProvider.get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        switch (args.length) {
        case 1:
            return UnitedLandsDataManager.instance().getSettlementNames();
        case 2:
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
        return null;
    }

}

package org.unitedlands.unitedlands.commands.handlers.admin.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementAdminCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

public class AdminSettlementDeleteCommand extends SettlementAdminCommandHandler {

    public AdminSettlementDeleteCommand(UnitedLands plugin, IMessageProvider messageProvider) {
        super(plugin, messageProvider);
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var player = (Player) sender;

        if (args.length != 1) {
            Messenger.sendMessage(player, messageProvider.get(Message.ADMIN__SETTLEMENT__DELETE__USAGE.path()),
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

        if (settlement.hasRegion()) {
            var region = settlement.getRegion();
            region.removeSettlement(settlement);
        }

        if (settlement.hasCountry()) {

            var country = settlement.getCountry();

            if (country.getCapital().equals(settlement)) {
                Messenger.sendMessage(Bukkit.getServer(), messageProvider.get(Message.ADMIN__SETTLEMENT__DELETE__IS_CAPITAL.path()),
                        null, messageProvider.get(Message.PREFIX.path()));
                return;
            }

            country.removeSettlement(settlement);
        }

        for (var settlementCitizen : settlement.getCitizens()) {
            settlementCitizen.removeSettlement();
            settlementCitizen.removeSettlementRanks();
            settlementCitizen.removeCountryRanks();
            UnitedLandsDataManager.instance().updateCitizenDbData(settlementCitizen);
        }

        UnitedLandsEconomyManager.instance().deleteAccount(settlement.getUuid());

        UnitedLandsDataManager.instance().removeSettlementDbData(settlement);

        Pl3xMapRenderer.instance().removeSettlement(settlement);

        Messenger.sendMessage(Bukkit.getServer(), messageProvider.get(Message.ADMIN__SETTLEMENT__DELETE__SUCCESS.path()),
                Map.of("settlement", settlement.getCleanName()),
                messageProvider.get(Message.PREFIX.path()));
    }

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        if (args.length == 1)
            return UnitedLandsDataManager.instance().getSettlementNames();
        return null;
    }

}

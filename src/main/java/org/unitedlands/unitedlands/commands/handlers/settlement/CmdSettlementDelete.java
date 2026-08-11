package org.unitedlands.unitedlands.commands.handlers.settlement;

import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.unitedlands.annotations.UnitedSubCommand;
import org.unitedlands.unitedlands.classes.Confirmation;
import org.unitedlands.unitedlands.classes.commandhandlers.SettlementCommandHandler;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Messenger;

@UnitedSubCommand(
    parent          = CmdSettlement.class,
    name            = "delete",
    description     = "Deletes a settlement",
    usage           = "/settlement delete",
    playerOnly      = true
)
public class CmdSettlementDelete extends SettlementCommandHandler {

    @Override
    public List<String> handleTab(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void handleCommand(CommandSender sender, String[] args) {

        var context = validate(sender, "settlement.delete");
        if (context == null)
            return;
        
        if (context.settlement().hasCountry() && context.settlement().getCountry().getCapital().equals(context.settlement())) {
            Messenger.sendMessage(context.player(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__DELETE__IS_CAPITAL.path()), null,
                    MessageProvider.instance().get(Message.PREFIX.path()));
            return;
        }

        Confirmation leave = new Confirmation("settlement-delete");
        leave.setRunnable(() -> {

            if (context.settlement().hasRegion()) {
                var region = context.settlement().getRegion();
                region.removeSettlement(context.settlement());
            }

            if (context.settlement().hasCountry()) {
                var country = context.settlement().getCountry();
                country.removeSettlement(context.settlement());
            }

            for (var settlementCitizen : context.settlement().getCitizens()) {
                settlementCitizen.removeSettlement();
                settlementCitizen.removeSettlementRanks();
                settlementCitizen.removeCountryRanks();
                UnitedLandsDataManager.instance().updateCitizenDbData(settlementCitizen);
            }

            UnitedLandsEconomyManager.instance().deleteAccount(context.settlement().getUuid());

            UnitedLandsDataManager.instance().removeSettlementDbData(context.settlement());

            Pl3xMapRenderer.instance().removeSettlement(context.settlement());

            Messenger.sendMessage(Bukkit.getServer(), MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__DELETE__BROADCAST_MESSAGE.path()),
                    Map.of("settlement", context.settlement().getCleanName()),
                    MessageProvider.instance().get(Message.PREFIX.path()));
        })
                .setTitle(MessageProvider.instance().get(Message.PLAYER__SETTLEMENT__DELETE__CONFIRM.path()))
                .setReplacements(Map.of("settlement", context.settlement().getCleanName()))
                .setSender(context.player())
                .setReceiver(context.player())
                .send();
    }

}

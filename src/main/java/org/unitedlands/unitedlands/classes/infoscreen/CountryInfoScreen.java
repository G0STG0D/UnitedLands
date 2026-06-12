package org.unitedlands.unitedlands.classes.infoscreen;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.utils.Messenger;

public class CountryInfoScreen extends InfoScreen {

    public CountryInfoScreen(UnitedLands plugin, IMessageProvider messageProvider, Country country) {
        super(plugin, messageProvider);

        var configSection = plugin.getMessageConfig().get().getConfigurationSection("info-screens.country");
        if (configSection == null)
            return;

        var header = buildHeader(country.getCleanName());
        addComponent("header", header);

        // var board =
        // Messenger.getMessage(messageProvider.get("info-screens.country.board"),
        // Map.of("board",
        // settlement.getTownBoard() != null ? settlement.getTownBoard()
        // : "/settlement setboard [msg]"));
        // addComponent("board", board);

        var foundingDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(country.getFoundingTimestamp());
        var founder = country.getFounderName() != null ? country.getFounderName() : "-";
        var founded = Messenger.getMessage(messageProvider.get("info-screens.country.founded"),
                Map.of("founded", foundingDate, "founder", founder));
        addComponent("founded", founded);

        var countryLeader = country.getLeader();
        var leader = Messenger.getMessage(messageProvider.get("info-screens.country.leader"),
                Map.of("leader", countryLeader != null ? countryLeader.getName() : "-"));
        addComponent("leader", leader);

        var capitalSettlement = country.getCapital();
        var capital = Messenger.getMessage(messageProvider.get("info-screens.country.capital"),
                Map.of("capital", capitalSettlement != null ? capitalSettlement.getCleanName() : "-"));
        addComponent("capital", capital);

        var balance = Messenger.getMessage(messageProvider.get("info-screens.country.balance"),
                Map.of("balance", EconomyManager.instance()
                        .format(EconomyManager.instance().getBalance(country.getUuid()))));
        addComponent("balance", balance);

        // var citizenCount = country.getCitizens().size();
        // if (country.getCitizens() != null) {
        // citizenCount = settlement.getCitizens().size();
        // citizenNames = String.join(", ",
        // settlement.getCitizens().stream()
        // .map(Citizen::getName)
        // .collect(Collectors
        // .toList()));
        // }
        // var citizens =
        // Messenger.getMessage(messageProvider.get("info-screens.country.citizens"),
        // Map.of("citizens-count", citizenCount + ""))
        // .hoverEvent(
        // HoverEvent.showText(
        // Component.text(citizenNames)));
        // addComponent("citizens", citizens);

        // var sizeupkeep =
        // Messenger.getMessage(messageProvider.get("info-screens.country.sizeupkeep"),
        // Map.of("size", String.valueOf(settlement.getChunks().size()),
        // "upkeep", EconomyManager.instance()
        // .format(CostUtils.getSettlementUpkeep(settlement))));
        // addComponent("sizeupkeep", sizeupkeep);
    }

}

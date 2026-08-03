package org.unitedlands.unitedlands.classes.infoscreen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.classes.metadata.BooleanMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.DoubleMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.FloatMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.IntegerMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.LongMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.StringMetaDataField;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.utils.Formatter;
import org.unitedlands.utils.Messenger;

public class CountryInfoScreen extends InfoScreen {

    public CountryInfoScreen(UnitedLands plugin, IMessageProvider messageProvider, Country country) {
        super(plugin, messageProvider);

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
        var founded = Messenger.getMessage(messageProvider.get(Message.INFO_SCREENS__COUNTRY__FOUNDED.path()),
                Map.of("founded", foundingDate, "founder", founder));
        addComponent("founded", founded);

        var countryLeader = country.getLeader();
        var leader = Messenger.getMessage(messageProvider.get(Message.INFO_SCREENS__COUNTRY__LEADER.path()),
                Map.of("leader", countryLeader != null ? countryLeader.getName() : "-"));
        addComponent("leader", leader);

        var capitalSettlement = country.getCapital();
        var capital = Messenger.getMessage(messageProvider.get(Message.INFO_SCREENS__COUNTRY__CAPITAL.path()),
                Map.of("capital", capitalSettlement != null ? capitalSettlement.getCleanName() : "-"));
        addComponent("capital", capital);

        var balance = Messenger.getMessage(messageProvider.get(Message.INFO_SCREENS__COUNTRY__BALANCE.path()),
                Map.of("balance", UnitedLandsEconomyManager.instance()
                        .format(UnitedLandsEconomyManager.instance()
                                .getBalance(country.getUuid()))));
        addComponent("balance", balance);

        var ongoingClaims = UnitedLandsDataManager.instance().getRegionClaimsOngoing(country);
        if (ongoingClaims.size() > 0) {
            List<String> claimsList = new ArrayList<>();
            for (var claim : ongoingClaims)
                claimsList.add("<red>" + claim.getCleanName() + "</red> (" + Formatter
                        .formatDuration(claim.getClaimEndTime() - System.currentTimeMillis()) + ")");

            var claims = Messenger.getMessage(messageProvider.get(Message.INFO_SCREENS__COUNTRY__CLAIMS.path()),
                    Map.of("claims", String.join(", ", claimsList)));
            addComponent("claims", claims);
        }

        var metadata = country.getMetadata();

        if (metadata != null && !metadata.isEmpty()) {

            var metaDataWrapper = messageProvider.get(Message.INFO_SCREENS__COUNTRY__METADATA.path());

            List<String> fields = new ArrayList<>();
            for (var m : metadata.values()) {
                if (!m.showInScreens())
                    continue;
                if (m.getValue() == null)
                    continue;
                var field = "<bold>" + m.getLabel() + "</bold>: ";
                if (m instanceof StringMetaDataField typedData) {
                    field += typedData.getValue();
                } else if (m instanceof IntegerMetaDataField typedData) {
                    field += typedData.getValue();
                } else if (m instanceof LongMetaDataField typedData) {
                    field += typedData.getValue();
                } else if (m instanceof FloatMetaDataField typedData) {
                    field += String.format("%.2f", typedData.getValue());
                } else if (m instanceof DoubleMetaDataField typedData) {
                    field += String.format("%.2f", typedData.getValue());
                } else if (m instanceof BooleanMetaDataField typedData) {
                    field += typedData.getValue() ? "Yes" : "No";
                }
                fields.add(field);
            }

            if (!fields.isEmpty()) {
                var finalMetaDataString = metaDataWrapper.replace("{metadata}",
                        String.join("<dark_gray> | </dark_gray>", fields));
                var metadataComponent = Messenger.getMessage(finalMetaDataString);
                addComponent("metadata", metadataComponent);
            }
        }
    }

}

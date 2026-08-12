package org.unitedlands.unitedlands.classes.infoscreen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.classes.metadata.BooleanMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.DoubleMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.FloatMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.IntegerMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.LongMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.StringMetaDataField;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Messenger;

public class CitizenInfoScreen extends InfoScreen {

    public CitizenInfoScreen(Citizen citizen) {

        var header = buildHeader(citizen.getName());
        addComponent("header", header);

        var joinDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(citizen.getJoined());
        var logonDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(citizen.getLastLogon());

        addComponent("registered",
                MessageProvider.instance().get(Message.INFO_SCREENS__CITIZEN__REGISTERED.path()),
                Map.of("registered", joinDate, "lastlogon", logonDate));

        addComponent("settlement-country",
                MessageProvider.instance().get(Message.INFO_SCREENS__CITIZEN__COUNTRY.path()),
                Map.of("settlement", citizen.getSettlement() != null ? citizen.getSettlement().getCleanName() : "-",
                        "country", citizen.getCountry() != null ? citizen.getCountry().getCleanName() : "-"));

        addComponent("settlement-ranks",
                MessageProvider.instance().get(Message.INFO_SCREENS__CITIZEN__SETTLEMENT_RANKS.path()),
                Map.of("settlementranks", String.join(", ", citizen.getSettlementRanks())));

        addComponent("country-ranks",
                Messenger.getMessage(MessageProvider.instance().get(Message.INFO_SCREENS__CITIZEN__COUNTRY_RANKS.path()),
                        Map.of("countryranks", String.join(", ", citizen.getCountryRanks()))));

        addComponent("balance",
                Message.INFO_SCREENS__CITIZEN__BALANCE.path(),
                Map.of("balance", UnitedLandsEconomyManager.instance().format(UnitedLandsEconomyManager.instance().getBalance(citizen.getUuid()))));

        var metadata = citizen.getMetadata();

        if (metadata != null && !metadata.isEmpty()) {

            var metaDataWrapper = MessageProvider.instance().get(Message.INFO_SCREENS__CITIZEN__METADATA.path());

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

package org.unitedlands.unitedlands.classes.infoscreen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.metadata.BooleanMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.DoubleMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.FloatMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.IntegerMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.LongMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.StringMetaDataField;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.utils.Messenger;

public class CitizenInfoScreen extends InfoScreen {

        public CitizenInfoScreen(UnitedLands plugin, IMessageProvider messageProvider, Citizen citizen) {
                super(plugin, messageProvider);

                var configSection = plugin.getMessageConfig().get().getConfigurationSection("info-screens.citizen");
                if (configSection == null)
                        return;

                var header = buildHeader(citizen.getName());
                addComponent("header", header);

                var joinDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(citizen.getJoined());
                var logonDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(citizen.getLastLogon());
                var registered = Messenger.getMessage(messageProvider.get("info-screens.citizen.registered"),
                                Map.of("registered", joinDate, "lastlogon", logonDate));
                addComponent("registered", registered);

                var settlement = Messenger.getMessage(messageProvider.get("info-screens.citizen.settlement-country"),
                                Map.of("settlement",
                                                citizen.getSettlement() != null ? citizen.getSettlement().getCleanName()
                                                                : "-",
                                                "country",
                                                citizen.getCountry() != null ? citizen.getCountry().getCleanName()
                                                                : "-"));
                addComponent("settlement-country", settlement);

                var settlementranks = Messenger.getMessage(messageProvider.get("info-screens.citizen.settlement-ranks"),
                                Map.of("settlementranks", String.join(", ", citizen.getSettlementRanks())));
                addComponent("settlement-ranks", settlementranks);

                var countryranks = Messenger.getMessage(messageProvider.get("info-screens.citizen.country-ranks"),
                                Map.of("countryranks", String.join(", ", citizen.getCountryRanks())));
                addComponent("country-ranks", countryranks);

                var balance = Messenger.getMessage(messageProvider.get("info-screens.citizen.balance"),
                                Map.of("balance",
                                                UnitedLandsEconomyManager.instance().format(UnitedLandsEconomyManager.instance()
                                                                .getBalance(citizen.getUuid()))));
                addComponent("balance", balance);

                var metadata = citizen.getMetadata();

                if (metadata != null && !metadata.isEmpty()) {

                        var metaDataWrapper = messageProvider.get("info-screens.citizen.metadata");

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

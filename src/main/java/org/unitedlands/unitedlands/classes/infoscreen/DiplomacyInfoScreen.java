package org.unitedlands.unitedlands.classes.infoscreen;

import java.util.Map;
import java.util.stream.Collectors;

import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.utils.Messenger;

public class DiplomacyInfoScreen extends InfoScreen {

        public DiplomacyInfoScreen(UnitedLands plugin, IMessageProvider messageProvider, Country country) {
                super(plugin, messageProvider);

                var configSection = plugin.getMessageConfig().get().getConfigurationSection("info-screens.diplomacy");
                if (configSection == null)
                        return;

                var header = buildHeader(country.getCleanName());
                addComponent("header", header);

                var countryAllies = country.getAllies();
                var allyCount = 0;
                var allyNames = "-";
                if (countryAllies != null && !countryAllies.isEmpty()) {
                        allyCount = countryAllies.size();
                        allyNames = String.join(", ",
                                        countryAllies.stream().map(Country::getCleanName).collect(Collectors.toList()));
                }
                var allies = Messenger.getMessage(messageProvider.get("info-screens.diplomacy.allies"),
                                Map.of("count", String.valueOf(allyCount), "allies", allyNames));
                addComponent("allies", allies);

                // TODO: treaties
                //var countryAllies = country.getAllies();
                var napCount = 0;
                var napNames = "-";
                // if (countryAllies != null && !countryAllies.isEmpty()) {
                //         allyCount = countryAllies.size();
                //         allyNames = String.join(", ",
                //                         countryAllies.stream().map(Country::getCleanName).collect(Collectors.toList()));
                // }
                var naps = Messenger.getMessage(messageProvider.get("info-screens.diplomacy.naps"),
                                Map.of("count", String.valueOf(napCount), "naps", napNames));
                addComponent("naps", naps);

                //var countryAllies = country.getAllies();
                var tradeTreatyCount = 0;
                var tradeTreatyNames = "-";
                // if (countryAllies != null && !countryAllies.isEmpty()) {
                //         allyCount = countryAllies.size();
                //         allyNames = String.join(", ",
                //                         countryAllies.stream().map(Country::getCleanName).collect(Collectors.toList()));
                // }
                var tradeTreaties = Messenger.getMessage(messageProvider.get("info-screens.diplomacy.trade-treaties"),
                                Map.of("count", String.valueOf(tradeTreatyCount), "trade-treaties", tradeTreatyNames));
                addComponent("trade-treaties", tradeTreaties);


                var countryClaimPermissions = country.getSettlementClaimWhitelist();
                var claimPermissionCount = 0;
                var claimPermissionNames = "-";
                if (countryClaimPermissions != null && !countryClaimPermissions.isEmpty()) {
                        claimPermissionCount = countryClaimPermissions.size();
                        claimPermissionNames = String.join(", ",
                                        countryClaimPermissions.stream().map(Settlement::getCleanName).collect(Collectors.toList()));
                }
                var claimPermissions = Messenger.getMessage(messageProvider.get("info-screens.diplomacy.allies"),
                                Map.of("count", String.valueOf(claimPermissionCount), "claim-permissions", claimPermissionNames));
                addComponent("claim-permissions", claimPermissions);

        }

}

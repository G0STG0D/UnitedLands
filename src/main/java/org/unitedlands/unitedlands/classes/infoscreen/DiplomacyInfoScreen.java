package org.unitedlands.unitedlands.classes.infoscreen;

import java.util.stream.Collectors;

import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.utils.United;

public class DiplomacyInfoScreen extends InfoScreen {

        public DiplomacyInfoScreen(Country country) {

                var configSection = UnitedLands.instance().getMessageConfig().get().getConfigurationSection("info-screens..diplomacy");
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
                addComponent("allies", United.messenger().get("info-screens..diplomacy.allies", String.valueOf(allyCount), allyNames));

                // TODO: treaties
                var napCount = 0;
                var napNames = "-";
                addComponent("naps", United.messenger().get("info-screens.diplomacy.naps", String.valueOf(napCount), "naps", napNames));

                // TODO: trade treaties
                var tradeTreatyCount = 0;
                var tradeTreatyNames = "-";
                addComponent("trade-treaties",
                                United.messenger().get("info-screens.diplomacy.trade-treaties", String.valueOf(tradeTreatyCount), tradeTreatyNames));

                var countryClaimPermissions = country.getSettlementClaimWhitelist();
                var claimPermissionCount = 0;
                var claimPermissionNames = "-";
                if (countryClaimPermissions != null && !countryClaimPermissions.isEmpty()) {
                        claimPermissionCount = countryClaimPermissions.size();
                        claimPermissionNames = String.join(", ",
                                        countryClaimPermissions.stream().map(Settlement::getCleanName).collect(Collectors.toList()));
                }
                addComponent("claim-permissions",
                                United.messenger().get("info-screens.diplomacy.claim-permissions", String.valueOf(claimPermissionCount), claimPermissionNames));

        }

}

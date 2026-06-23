package org.unitedlands.unitedlands.classes.infoscreen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.metadata.BooleanMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.DoubleMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.FloatMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.IntegerMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.LongMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.StringMetaDataField;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.unitedlands.utils.CostUtils;
import org.unitedlands.utils.Messenger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

public class SettlementInfoScreen extends InfoScreen {
        public SettlementInfoScreen(UnitedLands plugin, IMessageProvider messageProvider, Settlement settlement) {
                super(plugin, messageProvider);

                var configSection = plugin.getMessageConfig().get().getConfigurationSection("info-screens.settlement");
                if (configSection == null)
                        return;

                var header = buildHeader(settlement.getCleanName());
                addComponent("header", header);

                var board = Messenger.getMessage(messageProvider.get("info-screens.settlement.board"), Map.of("board",
                                settlement.getTownBoard() != null ? settlement.getTownBoard()
                                                : "/settlement setboard [msg]"));
                addComponent("board", board);

                var region = Messenger.getMessage(messageProvider.get("info-screens.settlement.region"),
                                Map.of("region", settlement.getRegion() != null ? settlement.getRegion().getCleanName()
                                                : "-"));
                addComponent("region", region);

                var foundingDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(settlement.getFoundingTimestamp());
                var founder = settlement.getFounderName() != null ? settlement.getFounderName() : "-";
                var founded = Messenger.getMessage(messageProvider.get("info-screens.settlement.founded"),
                                Map.of("founded", foundingDate, "founder", founder));
                addComponent("founded", founded);

                var mayor = Messenger.getMessage(messageProvider.get("info-screens.settlement.mayor"),
                                Map.of("mayor", settlement.getMayor() != null ? settlement.getMayor().getName() : "-"));
                addComponent("mayor", mayor);

                var isPublic = settlement.isPublic() ? "<green>Public</green>" : "<red>Private</red>";
                var pvp = settlement.allowPvp() ? "<green>PVP</green>" : "<red>PVP</red>";
                var mobs = settlement.allowMonsters() ? "<green>Monsters</green>" : "<red>Monsters</red>";
                var animals = settlement.allowAnimals() ? "<green>Animals</green>" : "<red>Animals</red>";
                var fire = settlement.allowFire() ? "<green>Fire</green>" : "<red>Fire</red>";
                var explosions = settlement.allowExplosions() ? "<green>Explosions</green>" : "<red>Explosions</red>";

                var toggles = Messenger.getMessage(messageProvider.get("info-screens.settlement.toggles"),
                                Map.of("public", isPublic, "pvp", pvp, "mobs", mobs, "animals", animals, "fire", fire,
                                                "explosions", explosions));
                addComponent("toggles", toggles);

                var perm1 = Messenger.getMessage(messageProvider.get("info-screens.settlement.perm-1"),
                                Map.of(
                                                "break",
                                                LocationMembership.toInfoScreenString(settlement.getBreakPermissions()),
                                                "place",
                                                LocationMembership.toInfoScreenString(settlement.getPlacePermissions()),
                                                "open", LocationMembership.toInfoScreenString(
                                                                settlement.getContainerPermissions())));
                var perm2 = Messenger.getMessage(messageProvider.get("info-screens.settlement.perm-2"),
                                Map.of(
                                                "switch",
                                                LocationMembership
                                                                .toInfoScreenString(settlement.getSwitchPermissions()),
                                                "use",
                                                LocationMembership.toInfoScreenString(
                                                                settlement.getBlockUsePermissions()),
                                                "interact", LocationMembership.toInfoScreenString(
                                                                settlement.getInteractPermissions())));
                addComponent("perm1", perm1);
                addComponent("perm2", perm2);

                var balance = Messenger.getMessage(messageProvider.get("info-screens.settlement.balance"),
                                Map.of("balance", EconomyManager.instance()
                                                .format(EconomyManager.instance().getBalance(settlement.getUuid()))));
                addComponent("balance", balance);

                var taxString = settlement.isUseTaxPercent() ? String.format("%.2f%%", settlement.getTax() * 100) : EconomyManager.instance().format((double)settlement.getTax());
                var taxes = Messenger.getMessage(messageProvider.get("info-screens.settlement.taxes"),
                                Map.of("taxes", taxString));
                addComponent("taxes", taxes);

                var citizenNames = "(no citizens)";
                var citizenCount = 0;
                if (settlement.getCitizens() != null) {
                        citizenCount = settlement.getCitizens().size();
                        citizenNames = String.join(", ",
                                        settlement.getCitizens().stream()
                                                        .map(Citizen::getName)
                                                        .collect(Collectors
                                                                        .toList()));
                }
                var citizens = Messenger.getMessage(messageProvider.get("info-screens.settlement.citizens"),
                                Map.of("citizens-count", citizenCount + ""))
                                .hoverEvent(
                                                HoverEvent.showText(
                                                                Component.text(citizenNames)));
                addComponent("citizens", citizens);

                var sizeupkeep = Messenger.getMessage(messageProvider.get("info-screens.settlement.sizeupkeep"),
                                Map.of("size", String.valueOf(settlement.getChunks().size()),
                                                "upkeep", EconomyManager.instance()
                                                                .format(CostUtils.getSettlementUpkeep(settlement))));
                addComponent("sizeupkeep", sizeupkeep);

                var metadata = settlement.getMetadata();

                if (metadata != null && !metadata.isEmpty()) {

                        var metaDataWrapper = messageProvider.get("info-screens.settlement.metadata");

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

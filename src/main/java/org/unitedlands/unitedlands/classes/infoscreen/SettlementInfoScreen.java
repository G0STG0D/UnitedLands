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
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.classes.metadata.BooleanMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.DoubleMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.FloatMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.IntegerMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.LongMetaDataField;
import org.unitedlands.unitedlands.classes.metadata.StringMetaDataField;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.CostUtils;
import org.unitedlands.utils.Messenger;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

public class SettlementInfoScreen extends InfoScreen {
    public SettlementInfoScreen(UnitedLands plugin, IMessageProvider messageProvider, Settlement settlement) {
        super(plugin, messageProvider);

        var header = buildHeader(settlement.getCleanName());
        addComponent("header", header);

        addComponent("board",
                messageProvider.get(Message.INFO_SCREENS__SETTLEMENT__BOARD.path()),
                Map.of(
                        "board", settlement.getTownBoard() != null ? settlement.getTownBoard() : "/settlement setboard [msg]"));

        addComponent("region",
                messageProvider.get(Message.INFO_SCREENS__SETTLEMENT__REGION.path()),
                Map.of(
                        "region", settlement.getRegion() != null ? settlement.getRegion().getCleanName() : "-",
                        "country", settlement.getCountry() != null ? settlement.getCountry().getCleanName() : "-"));

        var foundingDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(settlement.getFoundingTimestamp());
        var founder = settlement.getFounderName() != null ? settlement.getFounderName() : "-";

        addComponent("founded",
                messageProvider.get(Message.INFO_SCREENS__SETTLEMENT__FOUNDED.path()),
                Map.of(
                        "founded", foundingDate, "founder", founder));

        addComponent("mayor",
                messageProvider.get(Message.INFO_SCREENS__SETTLEMENT__MAYOR.path()),
                Map.of(
                        "mayor", settlement.getMayor() != null ? settlement.getMayor().getName() : "-"));

        var isPublic = settlement.isPublic() ? "<green>Public</green>" : "<red>Private</red>";
        var pvp = settlement.allowPvp() ? "<green>PVP</green>" : "<red>PVP</red>";
        var mobs = settlement.allowMonsters() ? "<green>Monsters</green>" : "<red>Monsters</red>";
        var animals = settlement.allowAnimals() ? "<green>Animals</green>" : "<red>Animals</red>";
        var fire = settlement.allowFire() ? "<green>Fire</green>" : "<red>Fire</red>";
        var explosions = settlement.allowExplosions() ? "<green>Explosions</green>" : "<red>Explosions</red>";

        addComponent("toggles",
                messageProvider.get(Message.INFO_SCREENS__SETTLEMENT__TOGGLES.path()),
                Map.of(
                        "public", isPublic,
                        "pvp", pvp,
                        "mobs", mobs,
                        "animals", animals,
                        "fire", fire,
                        "explosions", explosions));

        addComponent("perm1",
                messageProvider.get(Message.INFO_SCREENS__SETTLEMENT__PERM_1.path()),
                Map.of(
                        "break", LocationMembership.toInfoScreenString(settlement.getBreakPermissions()),
                        "place", LocationMembership.toInfoScreenString(settlement.getPlacePermissions()),
                        "open", LocationMembership.toInfoScreenString(settlement.getContainerPermissions())));
        addComponent("perm2", messageProvider.get(Message.INFO_SCREENS__SETTLEMENT__PERM_2.path()),
                Map.of(
                        "switch", LocationMembership.toInfoScreenString(settlement.getSwitchPermissions()),
                        "use", LocationMembership.toInfoScreenString(settlement.getBlockUsePermissions()),
                        "interact", LocationMembership.toInfoScreenString(settlement.getInteractPermissions())));

        addComponent("balance",
                messageProvider.get(Message.INFO_SCREENS__SETTLEMENT__BALANCE.path()),
                Map.of(
                        "balance", UnitedLandsEconomyManager.instance().format(UnitedLandsEconomyManager.instance().getBalance(settlement.getUuid()))));

        var taxString = settlement.useTaxPercent() ? String.format("%.2f%%", settlement.getTax() * 100)
                : UnitedLandsEconomyManager.instance().format((double) settlement.getTax());

        addComponent("taxes",
                messageProvider.get(Message.INFO_SCREENS__SETTLEMENT__TAXES.path()),
                Map.of(
                        "taxes", taxString));


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
        var citizens = Messenger.getMessage(messageProvider.get(Message.INFO_SCREENS__SETTLEMENT__CITIZENS.path()),
                Map.of("citizens-count", citizenCount + ""))
                .hoverEvent(
                        HoverEvent.showText(
                                Component.text(citizenNames)));
        addComponent("citizens", citizens);

        var sizeupkeep = Messenger.getMessage(messageProvider.get(Message.INFO_SCREENS__SETTLEMENT__SIZEUPKEEP.path()),
                Map.of("size", String.valueOf(settlement.getChunks().size()),
                        "upkeep", UnitedLandsEconomyManager.instance()
                                .format(CostUtils.getSettlementUpkeep(settlement))));
        addComponent("sizeupkeep", sizeupkeep);

        var metadata = settlement.getMetadata();

        if (metadata != null && !metadata.isEmpty()) {

            var metaDataWrapper = messageProvider.get(Message.INFO_SCREENS__SETTLEMENT__METADATA.path());

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

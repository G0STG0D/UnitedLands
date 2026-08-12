package org.unitedlands.unitedlands.classes.infoscreen;

import java.text.SimpleDateFormat;
import java.util.Map;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.MessageProvider;

public class SettlementChunkInfoScreen extends InfoScreen {

    public SettlementChunkInfoScreen(SettlementChunk chunk) {

        var header = buildHeader(chunk.getCoordinates().toCleanString());
        addComponent("header", header);

        addComponent("type",
                MessageProvider.instance().get(Message.INFO_SCREENS__SETTLEMENT_CHUNK__TYPE.path()),
                Map.of(
                        "type", chunk.getChunkType() != null ? chunk.getChunkType() : "None"));

        addComponent("owner",
                MessageProvider.instance().get(Message.INFO_SCREENS__SETTLEMENT_CHUNK__OWNER.path()),
                Map.of(
                        "owner", chunk.getOwner() != null ? chunk.getOwner().getName() : "-",
                        "claimed", new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chunk.getClaimTimestamp())));

        var forsale = chunk.isForSale() ? "<green>yes</green>" : "<red>no</red>";
        var price = chunk.isForSale() ? "<white>" + UnitedLandsEconomyManager.instance().format(chunk.getSalePrice()) + "</white>" : "-";

        addComponent("sale",
                MessageProvider.instance().get(Message.INFO_SCREENS__SETTLEMENT_CHUNK__SALE.path()),
                Map.of(
                        "forsale", forsale, "price", price));

        var pvp = chunk.allowPvp() ? "<green>PVP</green>" : "<red>PVP</red>";
        var mobs = chunk.allowMonsters() ? "<green>Monsters</green>" : "<red>Monsters</red>";
        var animals = chunk.allowAnimals() ? "<green>Animals</green>" : "<red>Animals</red>";
        var fire = chunk.allowFire() ? "<green>Fire</green>" : "<red>Fire</red>";
        var explosions = chunk.allowExplosions() ? "<green>Explosions</green>" : "<red>Explosions</red>";

        addComponent("toggles",
                MessageProvider.instance().get(Message.INFO_SCREENS__SETTLEMENT_CHUNK__TOGGLES.path()),
                Map.of("pvp", pvp,
                        "mobs", mobs,
                        "animals", animals,
                        "fire", fire,
                        "explosions", explosions));

        addComponent("perm1",
                MessageProvider.instance().get(Message.INFO_SCREENS__SETTLEMENT_CHUNK__PERM_1.path()),
                Map.of(
                        "break", LocationMembership.toInfoScreenString(chunk.getBreakPermissions()),
                        "place", LocationMembership.toInfoScreenString(chunk.getPlacePermissions()),
                        "open", LocationMembership.toInfoScreenString(chunk.getContainerPermissions())));

        addComponent("perm2",
                MessageProvider.instance().get(Message.INFO_SCREENS__SETTLEMENT_CHUNK__PERM_2.path()),
                Map.of(
                        "switch", LocationMembership.toInfoScreenString(chunk.getSwitchPermissions()),
                        "use", LocationMembership.toInfoScreenString(chunk.getBlockUsePermissions()),
                        "interact", LocationMembership.toInfoScreenString(chunk.getInteractPermissions())));

    }

}

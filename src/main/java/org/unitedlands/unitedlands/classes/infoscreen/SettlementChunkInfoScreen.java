package org.unitedlands.unitedlands.classes.infoscreen;

import java.text.SimpleDateFormat;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.SettlementChunk;

import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;

public class SettlementChunkInfoScreen extends InfoScreen {

    public SettlementChunkInfoScreen(SettlementChunk chunk) {

        var header = buildHeader(chunk.getCoordinates().toCleanString());
        addComponent("header", header);

        addComponent("type",
                "info-screens.settlement-chunk.type",
                chunk.getChunkType() != null ? chunk.getChunkType() : "None");

        addComponent("owner",
                "info-screens.settlement-chunk.owner",
                chunk.getOwner() != null ? chunk.getOwner().getName() : "-",
                new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chunk.getClaimTimestamp()));

        var forsale = chunk.isForSale() ? "<green>yes</green>" : "<red>no</red>";
        var price = chunk.isForSale() ? "<white>" + UnitedLandsEconomyManager.instance().format(chunk.getSalePrice()) + "</white>" : "-";

        addComponent("sale",
                "info-screens.settlement-chunk.sale",
                forsale, price);

        var pvp = chunk.allowPvp() ? "<green>PVP</green>" : "<red>PVP</red>";
        var mobs = chunk.allowMonsters() ? "<green>Monsters</green>" : "<red>Monsters</red>";
        var animals = chunk.allowAnimals() ? "<green>Animals</green>" : "<red>Animals</red>";
        var fire = chunk.allowFire() ? "<green>Fire</green>" : "<red>Fire</red>";
        var explosions = chunk.allowExplosions() ? "<green>Explosions</green>" : "<red>Explosions</red>";

        addComponent("toggles",
                "info-screens.settlement-chunk.toggles",
                pvp, mobs, animals, fire, explosions);

        addComponent("perm1",
                "info-screens.settlement-chunk.perm-1",
                LocationMembership.toInfoScreenString(chunk.getBreakPermissions()),
                LocationMembership.toInfoScreenString(chunk.getPlacePermissions()),
                LocationMembership.toInfoScreenString(chunk.getContainerPermissions()));

        addComponent("perm2",
                "info-screens.settlement-chunk.perm-2",
                LocationMembership.toInfoScreenString(chunk.getSwitchPermissions()),
                LocationMembership.toInfoScreenString(chunk.getBlockUsePermissions()),
                LocationMembership.toInfoScreenString(chunk.getInteractPermissions()));

    }

}

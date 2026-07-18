package org.unitedlands.unitedlands.classes.infoscreen;

import java.text.SimpleDateFormat;
import java.util.Map;
import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.SettlementChunk;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.utils.Messenger;

public class SettlementChunkInfoScreen extends InfoScreen {

        public SettlementChunkInfoScreen(UnitedLands plugin, IMessageProvider messageProvider, SettlementChunk chunk) {
                super(plugin, messageProvider);

                var configSection = plugin.getMessageConfig().get()
                                .getConfigurationSection("info-screens.settlementchunk");
                if (configSection == null)
                        return;

                var header = buildHeader(chunk.getCoordinates().toCleanString());
                addComponent("header", header);

                var type = Messenger.getMessage(messageProvider.get("info-screens.settlementchunk.type"),
                                Map.of("type", chunk.getChunkType() != null ? chunk.getChunkType() : "None"));
                addComponent("type", type);


                var claimedDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(chunk.getClaimTimestamp());

                var owner = Messenger.getMessage(messageProvider.get("info-screens.settlementchunk.owner"),
                                Map.of("owner", chunk.getOwner() != null ? chunk.getOwner().getName() : "-",
                                                "claimed", claimedDate));
                addComponent("owner", owner);

                var forsale = chunk.isForSale() ? "<green>yes</green>" : "<red>no</red>";
                var price = chunk.isForSale()
                                ? "<white>" + UnitedLandsEconomyManager.instance().format(chunk.getSalePrice()) + "</white>"
                                : "-";
                var sale = Messenger.getMessage(messageProvider.get("info-screens.settlementchunk.sale"),
                                Map.of("forsale", forsale, "price", price));
                addComponent("sale", sale);

                var pvp = chunk.allowPvp() ? "<green>PVP</green>" : "<red>PVP</red>";
                var mobs = chunk.allowMonsters() ? "<green>Monsters</green>" : "<red>Monsters</red>";
                var animals = chunk.allowAnimals() ? "<green>Animals</green>" : "<red>Animals</red>";
                var fire = chunk.allowFire() ? "<green>Fire</green>" : "<red>Fire</red>";
                var explosions = chunk.allowExplosions() ? "<green>Explosions</green>" : "<red>Explosions</red>";

                var toggles = Messenger.getMessage(messageProvider.get("info-screens.settlementchunk.toggles"),
                                Map.of("pvp", pvp, "mobs", mobs, "animals", animals, "fire", fire, "explosions",
                                                explosions));
                addComponent("toggles", toggles);

                var perm1 = Messenger.getMessage(messageProvider.get("info-screens.settlementchunk.perm-1"),
                                Map.of(
                                                "break",
                                                LocationMembership.toInfoScreenString(chunk.getBreakPermissions()),
                                                "place",
                                                LocationMembership.toInfoScreenString(chunk.getPlacePermissions()),
                                                "open", LocationMembership
                                                                .toInfoScreenString(chunk.getContainerPermissions())));
                var perm2 = Messenger.getMessage(messageProvider.get("info-screens.settlementchunk.perm-2"),
                                Map.of(
                                                "switch",
                                                LocationMembership.toInfoScreenString(chunk.getSwitchPermissions()),
                                                "use",
                                                LocationMembership.toInfoScreenString(chunk.getBlockUsePermissions()),
                                                "interact",
                                                LocationMembership.toInfoScreenString(chunk.getInteractPermissions())));
                addComponent("perm1", perm1);
                addComponent("perm2", perm2);

        }

}

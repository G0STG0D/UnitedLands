package org.unitedlands.unitedlands.classes.infoscreen;

import java.text.SimpleDateFormat;
import java.util.Map;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.utils.MessageProvider;

public class RegionInfoScreen extends InfoScreen {

        public RegionInfoScreen(Region region) {

                var header = buildHeader(region.getCleanName());
                addComponent("header", header);

                addComponent("owner", MessageProvider.instance().get(Message.INFO_SCREENS__REGION__OWNER.path()), Map.of("country",
                                region.getCountry() != null ? region.getCountry().getCleanName() : "-", "claimed",
                                region.getClaimedTime() != null ? new SimpleDateFormat("dd-MM-yyyy HH:mm").format(region.getClaimedTime()) : "-"));

                addComponent("administrator", MessageProvider.instance().get(Message.INFO_SCREENS__REGION__ADMINISTRATOR.path()),
                                Map.of("citizen", region.getAdministrator() != null ? region.getAdministrator().getName() : "-"));

                addComponent("area", MessageProvider.instance().get(Message.INFO_SCREENS__REGION__AREA.path()),
                                Map.of("area", String.format("%,.0f", region.getArea())));

                var pvp = region.allowPvp() ? "<green>PVP</green>" : "<red>PVP</red>";
                var mobs = region.allowMonsters() ? "<green>Monsters</green>" : "<red>Monsters</red>";
                var animals = region.allowAnimals() ? "<green>Animals</green>" : "<red>Animals</red>";
                var fire = region.allowFire() ? "<green>Fire</green>" : "<red>Fire</red>";
                var explosions = region.allowExplosions() ? "<green>Explosions</green>" : "<red>Explosions</red>";

                addComponent("toggles", MessageProvider.instance().get(Message.INFO_SCREENS__REGION__TOGGLES.path()),
                                Map.of("pvp", pvp, "mobs", mobs, "animals", animals, "fire", fire, "explosions", explosions));

                addComponent("perm1", MessageProvider.instance().get(Message.INFO_SCREENS__REGION__PERM_1.path()),
                                Map.of("break", LocationMembership.toInfoScreenString(region.getBreakPermissions()), "place",
                                                LocationMembership.toInfoScreenString(region.getPlacePermissions()), "open",
                                                LocationMembership.toInfoScreenString(region.getContainerPermissions())));

                addComponent("perm2", MessageProvider.instance().get(Message.INFO_SCREENS__REGION__PERM_2.path()),
                                Map.of("switch", LocationMembership.toInfoScreenString(region.getSwitchPermissions()), "use",
                                                LocationMembership.toInfoScreenString(region.getBlockUsePermissions()), "interact",
                                                LocationMembership.toInfoScreenString(region.getInteractPermissions())));

        }

}

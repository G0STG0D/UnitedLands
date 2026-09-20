package org.unitedlands.unitedlands.classes.infoscreen;

import java.text.SimpleDateFormat;
import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.Region;

import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.utils.CostUtils;

public class RegionInfoScreen extends InfoScreen {

    public RegionInfoScreen(Region region, Player player) {

        var header = buildHeader(region.getCleanName());
        addComponent("header", header);

        addComponent("owner", "info-screens.region.owner", 
                region.getCountry() != null ? region.getCountry().getCleanName() : "-", 
                region.getClaimedTime() != null ? new SimpleDateFormat("dd-MM-yyyy HH:mm").format(region.getClaimedTime()) : "-");

        if (region.hasCountry()) {
            addComponent("upkeep", "info-screens.region.costs",
                    UnitedLandsEconomyManager.instance().format(CostUtils.getRegionUpkeep(region, region.getCountry())));
        } else {
            var citizen = UnitedLandsDataManager.instance().getCitizen(player);
            if (citizen != null && citizen.hasCountry()) {
                addComponent("upkeep", "info-screens.region.costs-projected",
                        UnitedLandsEconomyManager.instance().format(CostUtils.getRegionUpkeep(region, citizen.getCountry())),
                        UnitedLandsEconomyManager.instance().format(CostUtils.getRegionClaimCosts(citizen.getCountry(), region)));
            }
        }

        addComponent("administrator", "info-screens.region.administrator",
                region.getAdministrator() != null ? region.getAdministrator().getName() : "-");

        addComponent("area", "info-screens.region.area",
                String.format("%,.0f", region.getArea()));

        var pvp = region.allowPvp() ? "<green>PVP</green>" : "<red>PVP</red>";
        var mobs = region.allowMonsters() ? "<green>Monsters</green>" : "<red>Monsters</red>";
        var animals = region.allowAnimals() ? "<green>Animals</green>" : "<red>Animals</red>";
        var fire = region.allowFire() ? "<green>Fire</green>" : "<red>Fire</red>";
        var explosions = region.allowExplosions() ? "<green>Explosions</green>" : "<red>Explosions</red>";

        addComponent("toggles", "info-screens.region.toggles",
                pvp, mobs, animals, fire, explosions);

        addComponent("perm1", "info-screens.region.perm-1",
                LocationMembership.toInfoScreenString(region.getBreakPermissions()),
                LocationMembership.toInfoScreenString(region.getPlacePermissions()),
                LocationMembership.toInfoScreenString(region.getContainerPermissions()));

        addComponent("perm2", "info-screens.region.perm-2",
                LocationMembership.toInfoScreenString(region.getSwitchPermissions()), 
                LocationMembership.toInfoScreenString(region.getBlockUsePermissions()), 
                LocationMembership.toInfoScreenString(region.getInteractPermissions()));

    }

}

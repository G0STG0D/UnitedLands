package org.unitedlands.unitedlands.classes.infoscreen;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.stream.Collectors;

import org.unitedlands.interfaces.IMessageProvider;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.LocationMembership;
import org.unitedlands.unitedlands.classes.Settlement;
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
                settlement.getTownBoard() != null ? settlement.getTownBoard() : "/settlement setboard [msg]"));
        addComponent("board", board);

        var region = Messenger.getMessage(messageProvider.get("info-screens.settlement.region"),
                Map.of("region", settlement.getRegion() != null ? settlement.getRegion().getCleanName() : "-"));
        addComponent("region", region);

        var foundingDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(settlement.getFoundingTimestamp());
        var founded = Messenger.getMessage(messageProvider.get("info-screens.settlement.founded"),
                Map.of("founded", foundingDate, "founder", settlement.getFounderName()));
        addComponent("founded", founded);

        var mayor = Messenger.getMessage(messageProvider.get("info-screens.settlement.mayor"),
                Map.of("mayor", settlement.getMayor() != null ? settlement.getMayor().getName() : "-"));
        addComponent("mayor", mayor);

        var pvp = settlement.allowPvp() ? "<green>PVP</green>" : "<red>PVP</red>";
        var mobs = settlement.allowMonsters() ? "<green>Monsters</green>" : "<red>Monsters</red>";
        var animals = settlement.allowAnimals() ? "<green>Animals</green>" : "<red>Animals</red>";
        var fire = settlement.allowFire() ? "<green>Fire</green>" : "<red>Fire</red>";
        var explosions = settlement.allowExplosions() ? "<green>Explosions</green>" : "<red>Explosions</red>";

        var toggles = Messenger.getMessage(messageProvider.get("info-screens.settlement.toggles"),
                Map.of("pvp", pvp, "mobs", mobs, "animals", animals, "fire", fire, "explosions", explosions));
        addComponent("toggles", toggles);

        var perm1 = Messenger.getMessage(messageProvider.get("info-screens.settlement.perm-1"),
                Map.of(
                        "break", LocationMembership.toInfoScreenString(settlement.getBreakPermissions()),
                        "place", LocationMembership.toInfoScreenString(settlement.getPlacePermissions()),
                        "open", LocationMembership.toInfoScreenString(settlement.getContainerPermissions())));
        var perm2 = Messenger.getMessage(messageProvider.get("info-screens.settlement.perm-2"),
                Map.of(
                        "switch", LocationMembership.toInfoScreenString(settlement.getSwitchPermissions()),
                        "use", LocationMembership.toInfoScreenString(settlement.getBlockUsePermissions()),
                        "interact", LocationMembership.toInfoScreenString(settlement.getInteractPermissions())));
        addComponent("perm1", perm1);
        addComponent("perm2", perm2);

        var citizens = Messenger.getMessage(messageProvider.get("info-screens.settlement.citizens"),
                Map.of("citizens-count", settlement.getCitizens().size() + ""))
                .hoverEvent(
                    HoverEvent.showText(
                        Component.text(String.join(", ",
                        settlement.getCitizens().stream().map(Citizen::getName).collect(Collectors.toList())
                    )
                )
            ));
        addComponent("citizens", citizens);
    }

}

package org.unitedlands.unitedlands.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.Settlement;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.bossbar.BossBar.Color;
import net.kyori.adventure.bossbar.BossBar.Overlay;
import net.kyori.adventure.text.Component;

public class DisplayManager {

    @SuppressWarnings("unused")
    private final UnitedLands plugin;

    private Map<Settlement, BossBar> settlementNameDisplays = new HashMap<>();
    private Map<Settlement, Set<Player>> settlementNameDisplayViewers = new HashMap<>();

    public DisplayManager(UnitedLands plugin) {
        this.plugin = plugin;
    }

    public void showSettlementDisplay(Settlement settlement, Player player) {
        var display = settlementNameDisplays.computeIfAbsent(settlement,
                k -> BossBar.bossBar(Component.text(settlement.getCleanName()), 1f, Color.WHITE, Overlay.PROGRESS));
        var viewers = settlementNameDisplayViewers.computeIfAbsent(settlement, k -> new HashSet<>());
        if (!viewers.contains(player)) {
            display.addViewer(player);
            viewers.add(player);
        }
    }

    public void hideSettlementDisplay(Settlement settlement, Player player) {
        var display = settlementNameDisplays.get(settlement);
        var viewers = settlementNameDisplayViewers.get(settlement);

        if (display != null && viewers != null && viewers.contains(player))
        {
            display.removeViewer(player);
            viewers.remove(player);

            if (viewers.size() == 0)
            {
                settlementNameDisplays.remove(settlement);
                settlementNameDisplayViewers.remove(settlement);
            }
        }
    }

}

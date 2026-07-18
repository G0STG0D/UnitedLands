package org.unitedlands.unitedlands.classes.events.player;

import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.events.base.RegionPlayerEvent;

public class PlayerEnterRegionEvent extends RegionPlayerEvent {

    private String additionalDisplay;

    public PlayerEnterRegionEvent(Region region, Player player) {
        super(region, player);
    }

    public String getAdditionalDisplay() {
        return additionalDisplay;
    }

    public void setAdditionalDisplay(String displayInformation) {
        this.additionalDisplay = displayInformation;
    }

}

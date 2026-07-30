package org.unitedlands.unitedlands.classes.events.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.events.base.RegionPlayerEvent;

public class PlayerEnterRegionEvent extends RegionPlayerEvent {

    private List<String> additionalDisplay = new ArrayList<>();

    public PlayerEnterRegionEvent(Region region, Player player) {
        super(region, player);
    }

    public List<String> getAdditionalDisplay() {
        return additionalDisplay;
    }

    public void setAdditionalDisplay(List<String> displayInformation) {
        this.additionalDisplay = displayInformation;
    }

    public void addAdditionalDisplay(String displayInformation) {
        this.additionalDisplay.add(displayInformation);
    }

}

package org.unitedlands.unitedlands.classes.events.region;

import org.bukkit.entity.Player;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.events.base.RegionEvent;


public class RegionDoubleClaimEvent extends RegionEvent {

    private final Player player;
    private final Country country;
    private final Country claimingCountry;

    private String confirmationMessage; 

    public RegionDoubleClaimEvent(Player player, Region region, Country country, Country claimingCountry) {
        super(region);
        this.player = player;
        this.country = country;
        this.claimingCountry = claimingCountry;
    }

    public Player getPlayer() {
        return player;
    }

    public Country getCountry() {
        return country;
    }

    public Country getClaimingCountry() {
        return claimingCountry;
    }

    public String getConfirmationMessage() {
        return confirmationMessage;
    }

    public void setConfirmationMessage(String confirmationMessage) {
        this.confirmationMessage = confirmationMessage;
    }

}

package org.unitedlands.unitedlands.classes.events.region;

import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.Region;
import org.unitedlands.unitedlands.classes.events.base.RegionEvent;

public class RegionClaimedEvent extends RegionEvent {

    private final Country country;

    public RegionClaimedEvent(Region region, Country country) {
        super(region);
        this.country = country;
    }

    public Country getCountry() {
        return country;
    }


}

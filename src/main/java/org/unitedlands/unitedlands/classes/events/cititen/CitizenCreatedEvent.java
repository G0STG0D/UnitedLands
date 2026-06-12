package org.unitedlands.unitedlands.classes.events.cititen;

import org.unitedlands.unitedlands.classes.Citizen;
import org.unitedlands.unitedlands.classes.events.base.CitizenEvent;

public class CitizenCreatedEvent extends CitizenEvent {

    public CitizenCreatedEvent(Citizen citizen) {
        super(citizen);
    }

}

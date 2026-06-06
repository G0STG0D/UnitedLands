package org.unitedlands.unitedlands.classes.events.base;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CostEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final double originalCosts;
    private double finalCosts;

    public CostEvent(double originalCosts) {
        this.originalCosts = originalCosts;
        this.finalCosts = originalCosts;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public double getOriginalCosts() {
        return originalCosts;
    }

    public double getFinalCosts() {
        return finalCosts;
    }

    public void setFinalCosts(double finalCosts) {
        this.finalCosts = finalCosts;
    }

}

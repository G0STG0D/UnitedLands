package org.unitedlands.unitedlands.classes;

public class GeopolAttribute {

    private double currentValue;
    private double minValue;
    private double maxValue;
    private double dailyChange;

    public GeopolAttribute() {
    }

    public GeopolAttribute(double currentValue, double minValue, double maxValue, double dailyChange) {
        this.currentValue = currentValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.dailyChange = dailyChange;
    }

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    public double getDailyChange() {
        return dailyChange;
    }

    public void setDailyChange(double dailaChange) {
        this.dailyChange = dailaChange;
    }
    
    public GeopolAttribute clone() {
        return new GeopolAttribute(currentValue, minValue, maxValue, dailyChange);
    }

}

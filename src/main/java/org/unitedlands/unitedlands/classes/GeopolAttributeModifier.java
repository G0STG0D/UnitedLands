package org.unitedlands.unitedlands.classes;

public class GeopolAttributeModifier {

    private GeopolAttributeType type;
    private String key;
    private double valueModifier;
    private double minValueModifier;
    private double maxValueModifier;
    private double dailyChangeModifier;

    public GeopolAttributeModifier() {
    }

    public GeopolAttributeModifier(GeopolAttributeType type, String key, double valueModifier, double minValueModifier, double maxValueModifier,
            double dailyChangeModifier) {
        this.type = type;
        this.key = key;
        this.valueModifier = valueModifier;
        this.minValueModifier = minValueModifier;
        this.maxValueModifier = maxValueModifier;
        this.dailyChangeModifier = dailyChangeModifier;
    }

    public GeopolAttributeType getType() {
        return type;
    }

    public void setType(GeopolAttributeType type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getValueModifier() {
        return valueModifier;
    }

    public void setValueModifier(double valueModifier) {
        this.valueModifier = valueModifier;
    }

    public double getMinValueModifier() {
        return minValueModifier;
    }

    public void setMinValueModifier(double minValueModifier) {
        this.minValueModifier = minValueModifier;
    }

    public double getMaxValueModifier() {
        return maxValueModifier;
    }

    public void setMaxValueModifier(double maxValueModifier) {
        this.maxValueModifier = maxValueModifier;
    }

    public double getDailyChangeModifier() {
        return dailyChangeModifier;
    }

    public void setDailyChangeModifier(double dailaChangeModifier) {
        this.dailyChangeModifier = dailaChangeModifier;
    }

}

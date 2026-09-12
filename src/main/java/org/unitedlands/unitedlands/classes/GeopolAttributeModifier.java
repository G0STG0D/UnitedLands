package org.unitedlands.unitedlands.classes;

public class GeopolAttributeModifier {

    public static enum Mode {
        ADD, MULTIPLY
    }

    private String attributeKey;
    private String modifierKey;
    private Mode mode;
    private double valueModifier;
    private double minValueModifier;
    private double maxValueModifier;
    private double dailyChangeModifier;

    public GeopolAttributeModifier() {
    }

    public GeopolAttributeModifier(String attributeKey, String modifierKey, Mode mode, double valueModifier, double minValueModifier, double maxValueModifier,
            double dailyChangeModifier) {
        this.attributeKey = attributeKey;
        this.modifierKey = modifierKey;
        this.mode = mode;
        this.valueModifier = valueModifier;
        this.minValueModifier = minValueModifier;
        this.maxValueModifier = maxValueModifier;
        this.dailyChangeModifier = dailyChangeModifier;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public void setAttributeKey(String key) {
        this.attributeKey = key;
    }

    public String getModifierKey() {
        return modifierKey;
    }

    public void setModifierKey(String modifierKey) {
        this.modifierKey = modifierKey;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
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

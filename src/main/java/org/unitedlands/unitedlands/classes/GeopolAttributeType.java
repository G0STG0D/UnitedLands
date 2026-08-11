package org.unitedlands.unitedlands.classes;

public enum GeopolAttributeType {

    MOBILISATION("Mobilistion", "A measure of your military readyness"),
    DIPLOMACY("Diplomatic Capacity", "A measure of your diplomatic capacity"),
    MAX_REGIONS("Maximum Regions", "The maximum number of regions you may own"),
    MAX_CLAIM("Maximum Claims", "The maximum number of regions you may claim in parallel"),
    MAX_SUBJECTS("Maximum Subjects", "The maximum number of subject nations (vassals, colonies...) you can have");

    private final String displayName;
    private final String description;

    private GeopolAttributeType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

}

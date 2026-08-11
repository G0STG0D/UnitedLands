package org.unitedlands.unitedlands.classes;

public enum ChatChannel {

    GLOBAL("white", "[<white><bold>G</bold></white>] %luckperms_prefix% ", false),
    SETTLEMENT("blue", "[<blue><bold>S</bold></blue>] %luckperms_prefix% ", true),
    COUNTRY("green", "[<green><bold>C</bold></green>] %luckperms_prefix% " , true),
    STAFF("dark_green", "[<dark_green><bold>STAFF</bold></dark_green>] %luckperms_prefix% ", false),
    LOCAL("gold", "[<gold><bold>L</bold></gold>] %luckperms_prefix% ", false);

    private final String color;
    private final String prefix;
    private final boolean removeEmpty;

    private ChatChannel(String color, String prefix, boolean removeEmpty) {
        this.color = color;
        this.prefix = prefix;
        this.removeEmpty = removeEmpty;
    }

    public String getColor() {
        return color;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean removeEmpty() {
        return removeEmpty;
    }
}

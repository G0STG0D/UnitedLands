package org.unitedlands.unitedlands.classes.chat;

public enum ChatChannelType {

    GLOBAL("white", "[<white><bold>G</bold></white>] ", false),
    SETTLEMENT("aqua", "[<blue><bold>S</bold></blue>] ", true),
    COUNTRY("green", "[<green><bold>C</bold></green>] ", true),
    STAFF("dark_green", "[<dark_green><bold>STAFF</bold></dark_green>] ", false),
    LOCAL("gold", "[<gold><bold>L</bold></gold>] ", false);

    private final String color;
    private final String prefix;
    private final boolean removeEmpty;

    private ChatChannelType(String color, String prefix, boolean removeEmpty) {
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

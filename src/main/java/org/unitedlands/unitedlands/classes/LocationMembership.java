package org.unitedlands.unitedlands.classes;

import java.util.ArrayList;
import java.util.List;

public class LocationMembership {
    public static final int UNSET = 0;
    public static final int FOREIGNER = 1 << 0;
    public static final int OWNER = 1 << 1;
    public static final int SETTLEMENT_RESIDENT = 1 << 2;
    public static final int REGION_RESIDENT = 1 << 3;
    public static final int NATION_RESIDENT = 1 << 4;
    public static final int TRUSTED = 1 << 5;
    public static final int ALLY = 1 << 6;
    public static final int ENEMY = 1 << 7;
    public static final int OUTLAW = 1 << 8;

    public static String toString(int membership) {
        if (membership == UNSET)
            return "UNSET";

        List<String> flags = new ArrayList<>();
        if ((membership & FOREIGNER) != 0)
            flags.add("FOREIGNER");
        if ((membership & OWNER) != 0)
            flags.add("OWNER");
        if ((membership & SETTLEMENT_RESIDENT) != 0)
            flags.add("TOWN_RESIDENT");
        if ((membership & REGION_RESIDENT) != 0)
            flags.add("REGION_RESIDENT");
        if ((membership & NATION_RESIDENT) != 0)
            flags.add("NATION_RESIDENT");
        if ((membership & TRUSTED) != 0)
            flags.add("TRUSTED");
        if ((membership & ALLY) != 0)
            flags.add("ALLY");
        if ((membership & ENEMY) != 0)
            flags.add("ENEMY");
        if ((membership & OUTLAW) != 0)
            flags.add("OUTLAW");

        return String.join(" | ", flags);
    }

    public static String toInfoScreenString(int membership) {
        if (membership == UNSET)
            return "<gray>-</gray>";

        List<String> flags = new ArrayList<>();

        if ((membership & SETTLEMENT_RESIDENT) != 0)
            flags.add("<blue>T</blue>");
        else
            flags.add("-");
        if ((membership & REGION_RESIDENT) != 0)
            flags.add("<red>R</red>");
        else
            flags.add("-");
        if ((membership & NATION_RESIDENT) != 0)
            flags.add("<green>N</green>");
        else
            flags.add("-");
        if ((membership & FOREIGNER) != 0)
            flags.add("<gray>F</gray>");
        else
            flags.add("-");

        return String.join("", flags);
    }

}

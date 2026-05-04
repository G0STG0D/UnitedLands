package org.unitedlands.unitedlands.utils;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {
    public static Color hexToColor(String hex) {
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        return switch (hex.length()) {
            case 6 -> new Color(
                    Integer.valueOf(hex.substring(0, 2), 16),
                    Integer.valueOf(hex.substring(2, 4), 16),
                    Integer.valueOf(hex.substring(4, 6), 16));
            case 8 -> new Color(
                    Integer.valueOf(hex.substring(0, 2), 16),
                    Integer.valueOf(hex.substring(2, 4), 16),
                    Integer.valueOf(hex.substring(4, 6), 16),
                    Integer.valueOf(hex.substring(6, 8), 16));
            default -> throw new IllegalArgumentException("Invalid hex color: " + hex);
        };
    }

    public static String argbToHex(int argb) {
        return String.format("#%02X%02X%02X",
                (argb >> 16) & 0xFF,
                (argb >> 8) & 0xFF,
                argb & 0xFF);
    }

    private static final String HEX_WEBCOLOR_PATTERN = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$";
    private static final Pattern pattern = Pattern.compile(HEX_WEBCOLOR_PATTERN);

    public static boolean isValidHexColor(final String colorCode) {
        Matcher matcher = pattern.matcher(colorCode);
        return matcher.matches();
    }
}

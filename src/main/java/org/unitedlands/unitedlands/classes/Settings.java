package org.unitedlands.unitedlands.classes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.file.FileConfiguration;
import org.unitedlands.utils.Logger;

public class Settings {

    private static int regionChunkSize = 8;

    private static int defaultCountryFillColour;
    private static int defaultCountryStrokeColour;
    private static int defaultCountryStrokeWidth;

    private static int defaultRegionFillColour;
    private static int defaultRegionStrokeColour;
    private static int defaultRegionStrokeWidth;
    private static String defaultRegionDash;

    private static int countryRegionStrokeWidth;
    private static String countryRegionDash;

    private static int defaultSettlementFillColour;
    private static int defaultSettlementStrokeColour;
    private static int defaultSettlementStrokeWidth;
    private static String defaultSettlementDash;

    private static int countrySettlementFillColour;
    private static int countrySettlementStrokeColour;
    private static int countrySettlementStrokeWidth;
    private static String countrySettlementDash;

    private static boolean protectUnclaimedLand;
    private static boolean allowTownClaimsOutsideHomeRegion;

    private static List<String> protectedContainers = new ArrayList<>();
    private static List<String> protectedUseBlocks = new ArrayList<>();
    private static List<String> protectedSwitchBlocks = new ArrayList<>();
    private static List<Tag<Material>> protectedSwitchTags = new ArrayList<>();
    private static List<String> protectedInteractEntities = new ArrayList<>();
    private static List<String> blacklistedMonsters = new ArrayList<>();
    private static List<String> blacklistedAnimals = new ArrayList<>();

    public static void loadSettings(FileConfiguration config) {

        regionChunkSize = config.getInt("general.region-chunk-size", 8);

        defaultCountryStrokeColour = config.getInt("general.country-default-stroke-colour", 540787967);
        defaultCountryFillColour = config.getInt("general.country-default-fill-colour", -13399809);
        defaultCountryStrokeWidth = config.getInt("general.country-default-stroke-width", 5);

        defaultRegionStrokeColour = config.getInt("general.region-default-stroke-colour", -2147483648);
        defaultRegionFillColour = config.getInt("general.region-default-fill-colour", 0);
        defaultRegionStrokeWidth = config.getInt("general.region-default-stroke-width", 2);
        defaultRegionDash = config.getString("general.region-default-dash", "1");

        countryRegionStrokeWidth = config.getInt("general.region-country-stroke-width", 1);
        countryRegionDash = config.getString("general.region-country-dash", "4,4");

        defaultSettlementFillColour = config.getInt("general.settlement-default-fill-colour", 540787967);
        defaultSettlementStrokeColour = config.getInt("general.settlement-default-stroke-colour", -12860161);
        defaultSettlementStrokeWidth = config.getInt("general.settlement-default-stroke-width", 2);
        defaultSettlementDash = config.getString("general.settlement-default-dash", "3");

        countrySettlementFillColour = config.getInt("general.settlement-country-fill-colour", 268435456);
        countrySettlementStrokeColour = config.getInt("general.settlement-country-stroke-colour", -1073741824);
        countrySettlementStrokeWidth = config.getInt("general.settlement-country-stroke-width", 2);
        countrySettlementDash = config.getString("general.settlement-country-dash", "3");

        allowTownClaimsOutsideHomeRegion = config.getBoolean("general.allow-town-claims-outside-home-region", true);
        protectUnclaimedLand = config.getBoolean("general.protect-unclaimed-land", true);

        protectedContainers = config.getStringList("protection.containers");
        protectedUseBlocks = config.getStringList("protection.use-blocks");
        protectedSwitchBlocks = config.getStringList("protection.switch-blocks");
        for (var strTag : config.getStringList("protection.switch-tags")) {
            NamespacedKey key = NamespacedKey.minecraft(strTag);
            Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material.class);
            if (tag != null) {
                protectedSwitchTags.add(tag);
            } else {
                Logger.logWarning("Unknown block tag: " + strTag, "UnitedLands");
            }
        }
        protectedInteractEntities = config.getStringList("protection.interact");
        blacklistedMonsters = config.getStringList("protection.monsters");
        blacklistedAnimals = config.getStringList("protection.animals");

    }

    public static int getRegionChunkSize() {
        return regionChunkSize;
    }

    public static int getDefaultCountryFillColour() {
        return defaultCountryFillColour;
    }

    public static int getDefaultCountryStrokeColour() {
        return defaultCountryStrokeColour;
    }

    public static int getDefaultCountryStrokeWidth() {
        return defaultCountryStrokeWidth;
    }

    public static int getDefaultRegionFillColour() {
        return defaultRegionFillColour;
    }

    public static int getDefaultRegionStrokeColour() {
        return defaultRegionStrokeColour;
    }

    public static int getDefaultRegionStrokeWidth() {
        return defaultRegionStrokeWidth;
    }

    public static int getCountryRegionStrokeWidth() {
        return countryRegionStrokeWidth;
    }

    public static String getDefaultRegionDash() {
        return defaultRegionDash;
    }

    public static String getCountryRegionDash() {
        return countryRegionDash;
    }

    public static int getDefaultSettlementFillColour() {
        return defaultSettlementFillColour;
    }

    public static int getDefaultSettlementStrokeColour() {
        return defaultSettlementStrokeColour;
    }

    public static int getDefaultSettlementStrokeWidth() {
        return defaultSettlementStrokeWidth;
    }

    public static String getDefaultSettlementDash() {
        return defaultSettlementDash;
    }

    public static int getCountrySettlementFillColour() {
        return countrySettlementFillColour;
    }

    public static int getCountrySettlementStrokeColour() {
        return countrySettlementStrokeColour;
    }

    public static int getCountrySettlementStrokeWidth() {
        return countrySettlementStrokeWidth;
    }

    public static String getCountrySettlementDash() {
        return countrySettlementDash;
    }

    public static boolean protectUnclaimedLand() {
        return protectUnclaimedLand;
    }

    public static boolean allowTownClaimsOutsideHomeRegion() {
        return allowTownClaimsOutsideHomeRegion;
    }

    public static List<String> getProtectedContainers() {
        return protectedContainers;
    }

    public static List<String> getProtectedUseBlocks() {
        return protectedUseBlocks;
    }

    public static List<String> getProtectedSwitchBlocks() {
        return protectedSwitchBlocks;
    }

    public static List<Tag<Material>> getProtectedSwitchTags() {
        return protectedSwitchTags;
    }

    public static List<String> getProtectedInteractEntities() {
        return protectedInteractEntities;
    }

    public static List<String> getBlacklistedMonsters() {
        return blacklistedMonsters;
    }

    public static List<String> getBlacklistedAnimals() {
        return blacklistedAnimals;
    }

}

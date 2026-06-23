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

    public static int regionChunkSize = 8;

    public static int defaultCountryFillColour;
    public static int defaultCountryStrokeColour;
    public static int defaultCountryStrokeWidth;

    public static int defaultRegionFillColour;
    public static int defaultRegionStrokeColour;
    public static int defaultRegionStrokeWidth;
    public static String defaultRegionDash;

    public static int countryRegionStrokeWidth;
    public static String countryRegionDash;

    public static int defaultSettlementFillColour;
    public static int defaultSettlementStrokeColour;
    public static int defaultSettlementStrokeWidth;
    public static String defaultSettlementDash;

    public static int countrySettlementFillColour;
    public static int countrySettlementStrokeColour;
    public static int countrySettlementStrokeWidth;
    public static String countrySettlementDash;

    public static boolean protectUnclaimedLand;
    public static boolean allowTownClaimsOutsideHomeRegion;

    public static int settlementCreateCosts;
    public static int settlementClaimBaseCosts;
    public static String settlementClaimCostProgression;
    public static int settlementBaseUpkeepPerPlot;
    public static String settlementUpkeepPerPlotFormula;
    public static float settlementMinTaxPercent;
    public static float settlementMaxTaxPercent;
    public static double settlementMinTaxAmount;
    public static double settlementMaxTaxAmount;

    public static int countryCreateCosts;
    public static int regionClaimBaseCosts;
    public static String regionClaimCostModifier;

    public static List<String> protectedContainers = new ArrayList<>();
    public static List<String> protectedUseBlocks = new ArrayList<>();
    public static List<String> protectedSwitchBlocks = new ArrayList<>();
    public static List<Tag<Material>> protectedSwitchTags = new ArrayList<>();
    public static List<String> protectedInteractEntities = new ArrayList<>();
    public static List<String> blacklistedMonsters = new ArrayList<>();
    public static List<String> blacklistedAnimals = new ArrayList<>();

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

        settlementCreateCosts = config.getInt("economy.new-settlement-cost", 100);
        settlementClaimBaseCosts = config.getInt("economy.settlement-claim-base-cost", 48);
        settlementClaimCostProgression = config.getString("economy.settlement-claim-cost-progression", "base + ((claims - 1)^2 * 0.005)");
        settlementBaseUpkeepPerPlot = config.getInt("economy.settlement-base-upkeep-per-plot", 6);
        settlementUpkeepPerPlotFormula = config.getString("economy.settlement-upkeep-per-plot-formula", "base * (0.1 * (claims / 25) + 1.0) / (0.4 * (residents / 2.0) + 1.0)");
        settlementMinTaxPercent = (float)config.getDouble("economy.settlement-tax-min-percent", 0.0);
        settlementMaxTaxPercent = (float)config.getDouble("economy.settlement-tax-max-percent", 20.0);
        settlementMinTaxAmount = config.getDouble("economy.settlement-tax-min-amount", 0.0);
        settlementMaxTaxAmount = config.getDouble("economy.settlement-tax-max-amount", 1500.0);


        countryCreateCosts = config.getInt("economy.new-country-cost", 80000);
        regionClaimBaseCosts = config.getInt("economy.region-claim-base-cost", 80000);
        regionClaimCostModifier = config.getString("economy.region-claim-cost-modifier", "base + (((regions + 1) / 2) * (distance / 1000))");

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

}

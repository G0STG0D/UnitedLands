package org.unitedlands.unitedlands.managers;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.util.InternalException;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.integrations.economy.IEconomyProvider;
import org.unitedlands.unitedlands.integrations.economy.VaultEconomyProvider;
import org.unitedlands.utils.Logger;

public class UnitedLandsEconomyManager {

    private static UnitedLandsEconomyManager instance;

    public static UnitedLandsEconomyManager instance() {
        return instance;
    }

    private final UnitedLands plugin;

    private IEconomyProvider economyProvider;

    public UnitedLandsEconomyManager(UnitedLands plugin) {
        this.plugin = plugin;
        instance = this;
    }

    public boolean hasEconomy() {
        return economyProvider != null;
    }

    public void loadEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            try {
                economyProvider = new VaultEconomyProvider(plugin);
                Logger.log("Found Vault, enabling economy...", "UnitedLands");
            } catch (InternalException ex) {
                economyProvider = null;
                Logger.logError("Error creating Vault economy provider.", "UnitedLands");
            }
        }
    }

    public String format(int amount) {
        if (economyProvider == null)
            return String.valueOf(amount);
        return economyProvider.format(new BigDecimal(amount));
    }

    public String format(double amount) {
        if (economyProvider == null)
            return String.valueOf(amount);
        return economyProvider.format(new BigDecimal(amount));
    }

    public String format(BigDecimal amount) {
        if (economyProvider == null)
            return String.valueOf(amount);
        return economyProvider.format(amount);
    }

    public void createAccount(UUID uuid, String name) {
        if (economyProvider == null)
            return;
        economyProvider.createEconomyAccount(uuid, name);
    }

    public void deleteAccount(UUID uuid) {
        if (economyProvider == null)
            return;
        economyProvider.deleteEconomyAccount(uuid);
    }

    public BigDecimal getBalance(UUID uuid) {
        if (economyProvider == null)
            return new BigDecimal(Double.MAX_VALUE);
        return economyProvider.getBalance(uuid);
    }

    public boolean has(UUID uuid, double amount) {
        return has(uuid, new BigDecimal(amount));
    }

    public boolean has(UUID uuid, BigDecimal amount) {
        if (economyProvider == null)
            return true;
        return economyProvider.has(uuid, amount);
    }

    public boolean deposit(UUID uuid, int amount, String reason) {
        return deposit(uuid, new BigDecimal(amount), reason);
    }

    public boolean deposit(UUID uuid, double amount, String reason) {
        return deposit(uuid, new BigDecimal(amount), reason);
    }

    public boolean deposit(UUID uuid, BigDecimal amount, String reason) {
        if (economyProvider == null)
            return true;
        if (economyProvider.deposit(uuid, amount)) {
            String message = "Received " + format(amount);
            if (reason != null)
                message += " (" + reason + ")";
            writeToLog(uuid, message);
            return true;
        }
        return false;
    }

    public boolean withdraw(UUID uuid, int amount, String reason) {
        return withdraw(uuid, new BigDecimal(amount), reason);
    }

    public boolean withdraw(UUID uuid, double amount, String reason) {
        return withdraw(uuid, new BigDecimal(amount), reason);
    }

    public boolean withdraw(UUID uuid, BigDecimal amount, String reason) {
        if (economyProvider == null)
            return true;
        if (economyProvider.withdraw(uuid, amount)) {
            String message = "Lost " + format(amount);
            if (reason != null)
                message += " (" + reason + ")";
            writeToLog(uuid, message);
            return true;
        }
        return false;
    }

    // Logging

    private void writeToLog(UUID objectId, String message) {

        var logDirectory = plugin.getDataFolder().toPath().resolve("logs");
        if (logDirectory == null || Files.notExists(logDirectory)) {
            try {
                Logger.log("Creating UnitedLands economy logs directory.");
                Files.createDirectories(logDirectory);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create log directory: " + logDirectory, e);
            }
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = "[" + timestamp + "] " + message + System.lineSeparator();

        Path logFile = logDirectory.resolve(objectId.toString() + ".log");

        try {
            Files.writeString(logFile, line,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            Logger.logError("Failed to write to log file: " + e.getMessage(), "UnitedLands");
        }
    }

    public List<String> getLogLines(UUID objectId, int startIndex, int count) {

        var logDirectory = plugin.getDataFolder().toPath().resolve("logs");
        if (logDirectory == null || Files.notExists(logDirectory)) {
            Logger.logError("Failed to load log directory: " + logDirectory);
            return new ArrayList<>();
        }

        Path logFile = logDirectory.resolve(objectId.toString() + ".log");

        try {
            var lines = new LinkedList<>(Files.readAllLines(logFile));
            return lines.stream().skip(Math.max(lines.size() - startIndex, 0)).limit(count).toList();
        } catch (IOException e) {
            Logger.logError("Failed to load from log file: " + e.getMessage(), "UnitedLands");
        }

        return new ArrayList<>();
    }

}

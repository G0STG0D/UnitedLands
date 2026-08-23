package org.unitedlands.unitedlands.managers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.util.InternalException;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.classes.BankRecord;
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
            saveBankRecord(uuid, amount, reason);
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
            saveBankRecord(uuid, amount.multiply(BigDecimal.valueOf(-1)), reason);
            return true;
        }
        return false;
    }

    // Logging

    private void saveBankRecord(UUID objectId, BigDecimal amount, String message) {
        var record = new BankRecord(objectId, amount.doubleValue(), message);
        try {
            UnitedLandsDataManager.instance().createBankRecordDbData(record);
        } catch (Exception ex) {
            Logger.logError("Failed to create bank record: " + ex.getMessage(), "UnitedLands");
        }
    }

    public List<BankRecord> getBankRecords(UUID objectId, int startIndex, int count) {
        try {
            return UnitedLandsDataManager.instance().getBankRecords(objectId, startIndex, count);
        } catch (Exception ex) {
            Logger.logError("Failed to bank records: " + ex.getMessage(), "UnitedLands");
        }
        return new ArrayList<>();
    }

}

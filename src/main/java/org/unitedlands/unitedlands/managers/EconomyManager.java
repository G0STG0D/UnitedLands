package org.unitedlands.unitedlands.managers;

import java.math.BigDecimal;
import java.util.UUID;

import org.apache.logging.log4j.util.InternalException;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.integrations.economy.IEconomyProvider;
import org.unitedlands.unitedlands.integrations.economy.VaultEconomyProvider;
import org.unitedlands.utils.Logger;

public class EconomyManager {
    protected static EconomyManager instance;

    public static EconomyManager instance() {
        return instance;
    }

    private final UnitedLands plugin;

    private IEconomyProvider economyProvider;

    public EconomyManager(UnitedLands plugin) {
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

    public boolean deposit(UUID uuid, int amount) {
        return deposit(uuid, new BigDecimal(amount));
    }

    public boolean deposit(UUID uuid, double amount) {
        return deposit(uuid, new BigDecimal(amount));
    }

    public boolean deposit(UUID uuid, BigDecimal amount) {
        if (economyProvider == null)
            return true;
        return economyProvider.deposit(uuid, amount);
    }

    public boolean withdraw(UUID uuid, int amount) {
        return withdraw(uuid, new BigDecimal(amount));
    }

    public boolean withdraw(UUID uuid, double amount) {
        return withdraw(uuid, new BigDecimal(amount));
    }

    public boolean withdraw(UUID uuid, BigDecimal amount) {
        if (economyProvider == null)
            return true;
        return economyProvider.withdraw(uuid, amount);
    }

}

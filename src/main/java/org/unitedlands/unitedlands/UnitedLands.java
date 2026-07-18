package org.unitedlands.unitedlands;

import java.util.Objects;
import org.bukkit.plugin.java.JavaPlugin;
import org.unitedlands.classes.ConfigFile;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.commands.AdminCommands;
import org.unitedlands.unitedlands.commands.ApprovalCommand;
import org.unitedlands.unitedlands.commands.CitizenCommands;
import org.unitedlands.unitedlands.commands.CountryCommands;
import org.unitedlands.unitedlands.commands.RegionCommands;
import org.unitedlands.unitedlands.commands.SettlementChunkCommands;
import org.unitedlands.unitedlands.commands.SettlementCommands;
import org.unitedlands.unitedlands.commands.WebCommands;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.integrations.Towny.TownyProvider;
import org.unitedlands.unitedlands.listeners.BlockListener;
import org.unitedlands.unitedlands.listeners.ExplosionListener;
import org.unitedlands.unitedlands.listeners.MobListener;
import org.unitedlands.unitedlands.listeners.PlayerBukkitListener;
import org.unitedlands.unitedlands.listeners.PlayerListener;
import org.unitedlands.unitedlands.listeners.ServerEventListener;
import org.unitedlands.unitedlands.managers.ConfirmationManager;
import org.unitedlands.unitedlands.managers.DisplayManager;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.unitedlands.managers.PlayerCacheManager;
import org.unitedlands.unitedlands.utils.MessageProvider;
import org.unitedlands.utils.Logger;

import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.logger.NullLogBackend;

public class UnitedLands extends JavaPlugin {

    private static UnitedLands instance;
    private static Settings settings;

    private ConfigFile messageConfig;
    private ConfigFile permissionConfig;

    private MessageProvider messageProvider;

    UnitedLandsDataManager globalDataManager;
    UnitedLandsEconomyManager economyManager;
    DisplayManager displayManager;
    ConfirmationManager confirmationManager;
    PermissionManager permissionManager;
    PlayerCacheManager playerCacheManager;

    private Pl3xMapRenderer mapRenderer;
    private TownyProvider townyProvider;

    private UnitedLandsWebServices webServices;

    @Override
    public void onEnable() {

        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        instance = this;

        saveDefaultConfig();

        messageConfig = new ConfigFile(this, "messages/en_GB.yml");
        permissionConfig = new ConfigFile(this, "permissions.yml");

        messageProvider = new MessageProvider(messageConfig.get());

        Settings.loadSettings(getConfig());

        loadManagers();
        loadIntegrations();
        registerCommands();
        registerListeners();

        webServices = new UnitedLandsWebServices(this);

        getLogger().info("UnitedLands initialized.");
    }

    @Override
    public void onDisable() {
        webServices.stopWebServices();
    }

    private void loadManagers() {

        mapRenderer = new Pl3xMapRenderer();
        permissionManager = new PermissionManager(this);
        globalDataManager = new UnitedLandsDataManager(this, mapRenderer);
        displayManager = new DisplayManager(this);
        confirmationManager = new ConfirmationManager(this);
        playerCacheManager = new PlayerCacheManager(this);
        economyManager = new UnitedLandsEconomyManager(this);
    }

    private void loadIntegrations() {
        var towny = getServer().getPluginManager().getPlugin("Towny");
        if (towny != null && towny.isEnabled()) {
            townyProvider = new TownyProvider(this);
            Logger.log("Found Towny, enabling integration...", "UnitedLands");
        }
    }

    private void registerCommands() {

        var countryCommands = new CountryCommands(this, messageProvider);
        Objects.requireNonNull(getCommand("country")).setExecutor(countryCommands);
        Objects.requireNonNull(getCommand("country")).setTabCompleter(countryCommands);

        var regionCommands = new RegionCommands(this, messageProvider);
        Objects.requireNonNull(getCommand("region")).setExecutor(regionCommands);
        Objects.requireNonNull(getCommand("region")).setTabCompleter(regionCommands);

        var settlementCommands = new SettlementCommands(this, messageProvider);
        Objects.requireNonNull(getCommand("settlement")).setExecutor(settlementCommands);
        Objects.requireNonNull(getCommand("settlement")).setTabCompleter(settlementCommands);

        var settlementChunkCommands = new SettlementChunkCommands(this, messageProvider);
        Objects.requireNonNull(getCommand("settlementchunk")).setExecutor(settlementChunkCommands);
        Objects.requireNonNull(getCommand("settlementchunk")).setTabCompleter(settlementChunkCommands);

        var adminCommands = new AdminCommands(this, messageProvider);
        Objects.requireNonNull(getCommand("uladmin")).setExecutor(adminCommands);
        Objects.requireNonNull(getCommand("uladmin")).setTabCompleter(adminCommands);

        var approvalCommand = new ApprovalCommand(this, messageProvider);
        Objects.requireNonNull(getCommand("approve")).setExecutor(approvalCommand);
        Objects.requireNonNull(getCommand("approve")).setTabCompleter(approvalCommand);

        var citizenCommand = new CitizenCommands(this, messageProvider);
        Objects.requireNonNull(getCommand("citizen")).setExecutor(citizenCommand);
        Objects.requireNonNull(getCommand("citizen")).setTabCompleter(citizenCommand);

        var webCommand = new WebCommands(this, messageProvider);
        Objects.requireNonNull(getCommand("ulweb")).setExecutor(webCommand);
        Objects.requireNonNull(getCommand("ulweb")).setTabCompleter(webCommand);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerBukkitListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ServerEventListener(this), this);
        getServer().getPluginManager().registerEvents(new MobListener(this), this);
        getServer().getPluginManager().registerEvents(new ExplosionListener(this), this);
    }

    public static UnitedLands getInstance() {
        return instance;
    }

    public MessageProvider getMessageProvider() {
        return messageProvider;
    }

    public static Settings getSettings() {
        return settings;
    }

    public ConfigFile getMessageConfig() {
        return messageConfig;
    }

    public ConfigFile getPermissionConfig() {
        return permissionConfig;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public TownyProvider getTownyProvider() {
        return townyProvider;
    }

    public UnitedLandsWebServices getWebServices() {
        return webServices;
    }

}

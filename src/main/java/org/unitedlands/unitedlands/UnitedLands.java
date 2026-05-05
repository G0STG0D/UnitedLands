package org.unitedlands.unitedlands;

import java.util.Objects;
import org.bukkit.plugin.java.JavaPlugin;
import org.unitedlands.classes.ConfigFile;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.commands.AdminCommands;
import org.unitedlands.unitedlands.commands.ApprovalCommand;
import org.unitedlands.unitedlands.commands.CountryCommands;
import org.unitedlands.unitedlands.commands.RegionChunkCommands;
import org.unitedlands.unitedlands.commands.RegionCommands;
import org.unitedlands.unitedlands.commands.SettlementChunkCommands;
import org.unitedlands.unitedlands.commands.SettlementCommands;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.listeners.BlockListener;
import org.unitedlands.unitedlands.listeners.ExplosionListener;
import org.unitedlands.unitedlands.listeners.MobListener;
import org.unitedlands.unitedlands.listeners.PlayerBukkitListener;
import org.unitedlands.unitedlands.listeners.PlayerListener;
import org.unitedlands.unitedlands.listeners.ServerEventListener;
import org.unitedlands.unitedlands.managers.ConfirmationManager;
import org.unitedlands.unitedlands.managers.DatabaseManager;
import org.unitedlands.unitedlands.managers.DisplayManager;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.unitedlands.managers.PermissionManager;
import org.unitedlands.unitedlands.managers.PlayerCacheManager;
import org.unitedlands.unitedlands.utils.MessageProvider;

import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.logger.NullLogBackend;

public class UnitedLands extends JavaPlugin {

    private static UnitedLands instance;
    private static Settings settings;

    private ConfigFile messageConfig;
    private ConfigFile permissionConfig;

    private MessageProvider messageProvider;

    DatabaseManager databaseManager;
    GlobalDataManager globalDataManager;
    DisplayManager displayManager;
    ConfirmationManager confirmationManager;
    PermissionManager permissionManager;
    PlayerCacheManager playerCacheManager;

    private Pl3xMapRenderer mapRenderer;

    @Override
    public void onEnable() {

        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        // LoggerFactory.setLogBackendFactory(
        //         LoggerFactory.getLogBackendFactory() // keep existing backend
        // );
        // com.j256.ormlite.logger.Logger.setGlobalLogLevel(Level.WARNING);
        
        instance = this;

        saveDefaultConfig();

        messageConfig = new ConfigFile(this, "messages/en_GB.yml");
        permissionConfig = new ConfigFile(this, "permissions.yml");

        messageProvider = new MessageProvider(messageConfig.get());

        Settings.loadSettings(getConfig());

        loadManagers();

        registerCommands();
        registerListeners();

        databaseManager.initialize();

        getLogger().info("UnitedRegions initialized.");
    }

    private void loadManagers() {

        mapRenderer = new Pl3xMapRenderer();
        permissionManager = new PermissionManager(this);
        databaseManager = new DatabaseManager(this);
        globalDataManager = new GlobalDataManager(databaseManager, mapRenderer);
        displayManager = new DisplayManager(this);
        confirmationManager = new ConfirmationManager(this);
        playerCacheManager = new PlayerCacheManager(this);
    }

    private void registerCommands() {

        var countryCommands = new CountryCommands(this, messageProvider);
        Objects.requireNonNull(getCommand("country")).setExecutor(countryCommands);
        Objects.requireNonNull(getCommand("country")).setTabCompleter(countryCommands);

        var regionCommands = new RegionCommands(this, messageProvider);
        Objects.requireNonNull(getCommand("region")).setExecutor(regionCommands);
        Objects.requireNonNull(getCommand("region")).setTabCompleter(regionCommands);

        var regionChunkCommands = new RegionChunkCommands(this, messageProvider);
        Objects.requireNonNull(getCommand("regionchunk")).setExecutor(regionChunkCommands);
        Objects.requireNonNull(getCommand("regionchunk")).setTabCompleter(regionChunkCommands);

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

    public DisplayManager getDisplayManager() {
        return displayManager;
    }

    public PermissionManager getPermissionManager() {
        return permissionManager;
    }

    public Pl3xMapRenderer getMapRenderer() {
        return mapRenderer;
    }

}

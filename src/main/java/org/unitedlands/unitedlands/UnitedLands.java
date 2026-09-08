package org.unitedlands.unitedlands;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.unitedlands.classes.ConfigFile;
import org.unitedlands.unitedlands.classes.Settings;
import org.unitedlands.unitedlands.classes.message.Message;
import org.unitedlands.unitedlands.classes.message.MessageRegistry;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.integrations.Towny.TownyProvider;
import org.unitedlands.unitedlands.integrations.papi.PlaceholderAPIIntegration;
import org.unitedlands.unitedlands.listeners.BlockListener;
import org.unitedlands.unitedlands.listeners.ChatListener;
import org.unitedlands.unitedlands.listeners.ExplosionListener;
import org.unitedlands.unitedlands.listeners.MobListener;
import org.unitedlands.unitedlands.listeners.PlayerDamageListener;
import org.unitedlands.unitedlands.listeners.PlayerMovementListener;
import org.unitedlands.unitedlands.listeners.PlayerListener;
import org.unitedlands.unitedlands.listeners.RegionListener;
import org.unitedlands.unitedlands.listeners.ServerEventListener;
import org.unitedlands.unitedlands.managers.ChatChannelManager;
import org.unitedlands.unitedlands.managers.ConfirmationManager;
import org.unitedlands.unitedlands.managers.DisplayManager;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.schedulers.NewDayScheduler;
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
    private MessageRegistry messageRegistry;

    UnitedLandsDataManager globalDataManager;
    UnitedLandsEconomyManager economyManager;
    DisplayManager displayManager;
    ConfirmationManager confirmationManager;
    PermissionManager permissionManager;
    PlayerCacheManager playerCacheManager;
    ChatChannelManager chatChannelManager;

    private Pl3xMapRenderer mapRenderer;
    private TownyProvider townyProvider;

    private UnitedLandsWebServices webServices;

    private NewDayScheduler newDayScheduler;

    private boolean useFloodgate;
    private boolean usePAPI;

    @Override
    public void onEnable() {

        LoggerFactory.setLogBackendFactory(new NullLogBackend.NullLogBackendFactory());

        instance = this;

        saveDefaultConfig();

        messageRegistry = new MessageRegistry(this, "messages/en.yml");
        messageRegistry.sync(Message.class);

        messageConfig = new ConfigFile(this, messageRegistry.getFilePath());
        permissionConfig = new ConfigFile(this, "permissions.yml");

        messageProvider = new MessageProvider(messageConfig.get());

        Settings.loadSettings(getConfig());

        loadManagers();
        loadIntegrations();
        registerListeners();

        webServices = new UnitedLandsWebServices(this);

        getLogger().info("UnitedLands initialized.");
    }

    @Override
    public void onDisable() {
        webServices.stopWebServices();
        newDayScheduler.stopScheduler();
        Pl3xMapRenderer.instance().shutdown();
    }

    private void loadManagers() {

        mapRenderer = new Pl3xMapRenderer();
        permissionManager = new PermissionManager(this);
        globalDataManager = new UnitedLandsDataManager(this, mapRenderer);
        displayManager = new DisplayManager(this);
        confirmationManager = new ConfirmationManager(this);
        playerCacheManager = new PlayerCacheManager(this);
        economyManager = new UnitedLandsEconomyManager(this);
        chatChannelManager = new ChatChannelManager(this, messageProvider);

        newDayScheduler = new NewDayScheduler();
    }

    private void loadIntegrations() {
        var towny = getServer().getPluginManager().getPlugin("Towny");
        if (towny != null && towny.isEnabled()) {
            townyProvider = new TownyProvider(this);
            Logger.log("Found Towny, enabling integration...", "UnitedLands");
        }
        Plugin floodgate = Bukkit.getPluginManager().getPlugin("floodgate");
        if (floodgate != null && floodgate.isEnabled()) {
            Logger.log("Enabling floodgate integrations.", "UnitedLands");
            useFloodgate = true;
        }
        Plugin papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (papi != null && papi.isEnabled()) {
            Logger.log("Enabling floodgate integrations.", "UnitedLands");
            new PlaceholderAPIIntegration();
            usePAPI = true;
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMovementListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerDamageListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new ServerEventListener(), this);
        getServer().getPluginManager().registerEvents(new MobListener(), this);
        getServer().getPluginManager().registerEvents(new ExplosionListener(), this);
        getServer().getPluginManager().registerEvents(new RegionListener(messageProvider), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }

    public static UnitedLands instance() {
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

    public boolean useFloodgate() {
        return useFloodgate;
    }

    public boolean usePAPI() {
        return usePAPI;
    }

    public NewDayScheduler getNewDayScheduler() {
        return newDayScheduler;
    }

}

package org.unitedlands.unitedlands.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.EconomyManager;
import org.unitedlands.unitedlands.managers.GlobalDataManager;
import org.unitedlands.utils.Logger;

public class ServerEventListener implements Listener {

    @SuppressWarnings("unused")
    private final UnitedLands plugin;

    public ServerEventListener(UnitedLands plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {

        Pl3xMapRenderer.instance().initialize();
        GlobalDataManager.instance().loadDataFromDatabase();

        EconomyManager.instance().loadEconomy();
        if (!EconomyManager.instance().hasEconomy()) {
            Logger.logWarning(
                    "No valid economy provider detected, falling back to mockup economy. All economic transactions will be executed as if all economic actors had unlimited funds.", "UnitedLands");
        }
    }

}

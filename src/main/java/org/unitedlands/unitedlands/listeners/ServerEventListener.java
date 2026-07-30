package org.unitedlands.unitedlands.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.unitedlands.unitedlands.integrations.Pl3xMap.Pl3xMapRenderer;
import org.unitedlands.unitedlands.managers.UnitedLandsEconomyManager;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;
import org.unitedlands.utils.Logger;

public class ServerEventListener implements Listener {

    public ServerEventListener() {

    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {

        Pl3xMapRenderer.instance().initialize();
        UnitedLandsDataManager.instance().loadDataFromDatabase();

        UnitedLandsEconomyManager.instance().loadEconomy();
        if (!UnitedLandsEconomyManager.instance().hasEconomy()) {
            Logger.logWarning(
                    "No valid economy provider detected, falling back to mockup economy. All economic transactions will be executed as if all economic actors had unlimited funds.", "UnitedLands");
        }
    }

}

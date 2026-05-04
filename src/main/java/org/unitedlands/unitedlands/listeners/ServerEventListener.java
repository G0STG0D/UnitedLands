package org.unitedlands.unitedlands.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.unitedlands.unitedlands.UnitedLands;
import org.unitedlands.unitedlands.managers.GlobalDataManager;

public class ServerEventListener implements Listener {

    @SuppressWarnings("unused")
    private final UnitedLands plugin;

    public ServerEventListener(UnitedLands plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        GlobalDataManager.instance().loadDataFromDatabase();
    }

}

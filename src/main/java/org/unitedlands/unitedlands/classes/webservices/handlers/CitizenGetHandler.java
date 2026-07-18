package org.unitedlands.unitedlands.classes.webservices.handlers;

import java.util.UUID;
import java.util.concurrent.Future;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.unitedlands.unitedlands.classes.webservices.ApiResponse;
import org.unitedlands.unitedlands.classes.webservices.dto.CititzenDTO;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;

import io.undertow.server.HttpServerExchange;

public class CitizenGetHandler extends BaseGetHandler {

    private final Plugin plugin;

    public CitizenGetHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        var idParam = getPathParam(exchange, "id");
        if (idParam == null) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        UUID id = null;
        try {
            id = UUID.fromString(idParam);
        } catch (Exception ex) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        var citizen = UnitedLandsDataManager.instance().getCitizen(id);
        if (citizen == null) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        Future<CititzenDTO> future = Bukkit.getScheduler().callSyncMethod(plugin,
                () -> {
                    return new CititzenDTO(citizen);
                });
                
        ApiResponse.send(exchange, 200, future.get());
    }

}

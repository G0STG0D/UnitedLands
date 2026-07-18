package org.unitedlands.unitedlands.classes.webservices.handlers;

import java.util.concurrent.Future;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.unitedlands.unitedlands.classes.webservices.ApiResponse;
import org.unitedlands.unitedlands.classes.webservices.dto.SettlementDTO;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;

import io.undertow.server.HttpServerExchange;

public class SettlementsGetHandler extends BaseGetHandler {

    private final Plugin plugin;

    public SettlementsGetHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        var slug = getPathParam(exchange, "slug");
        if (slug == null) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        var settlement = UnitedLandsDataManager.instance().getSettlement(slug);
        if (settlement == null) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        Future<SettlementDTO> future = Bukkit.getScheduler().callSyncMethod(plugin,
                () -> {
                    return new SettlementDTO(settlement);
                });
                
        ApiResponse.send(exchange, 200, future.get());
    }

}

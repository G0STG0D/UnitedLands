package org.unitedlands.unitedlands.classes.webservices.handlers;

import java.util.concurrent.Future;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.unitedlands.unitedlands.classes.webservices.ApiResponse;
import org.unitedlands.unitedlands.classes.webservices.dto.CountryDTO;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;

import io.undertow.server.HttpServerExchange;

public class CountriesGetHandler extends BaseGetHandler {

    private final Plugin plugin;

    public CountriesGetHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {

        var slug = getPathParam(exchange, "slug");
        if (slug == null) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        var country = UnitedLandsDataManager.instance().getCountry(slug);
        if (country == null) {
            ApiResponse.send(exchange, 404, null);
            return;
        }

        Future<CountryDTO> future = Bukkit.getScheduler().callSyncMethod(plugin,
                () -> {
                    return new CountryDTO(country);
                });
                
        ApiResponse.send(exchange, 200, future.get());
    }

}

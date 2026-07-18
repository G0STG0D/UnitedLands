package org.unitedlands.unitedlands.classes.webservices.handlers;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.unitedlands.unitedlands.classes.webservices.ApiResponse;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class PlayersHandler implements HttpHandler {

    private final Plugin plugin;

    public PlayersHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Bukkit API must be called on the main thread
        Future<List<Map<String, Object>>> future =
            Bukkit.getScheduler().callSyncMethod(plugin, () ->
                Bukkit.getOnlinePlayers().stream()
                    .map(p -> {
                        Map<String, Object> data = new LinkedHashMap<>();
                        data.put("name", p.getName());
                        data.put("uuid", p.getUniqueId().toString());
                        data.put("world", p.getWorld().getName());
                        data.put("health", p.getHealth());
                        return data;
                    })
                    .collect(Collectors.toList())
            );

        ApiResponse.send(exchange, 200, future.get());
    }
}

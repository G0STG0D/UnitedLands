package org.unitedlands.unitedlands.classes.webservices.handlers;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.unitedlands.unitedlands.classes.Settlement;
import org.unitedlands.unitedlands.classes.webservices.ApiResponse;
import org.unitedlands.unitedlands.classes.webservices.dto.PaginatedListDTO;
import org.unitedlands.unitedlands.classes.webservices.dto.SettlementListDTO;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;

import io.undertow.server.HttpServerExchange;

public class SettlementsListHandler extends BaseGetHandler {

    private final Plugin plugin;

    public SettlementsListHandler(Plugin plugin) {
        this.plugin = plugin;
    }

    public void handleRequest(HttpServerExchange exchange) throws Exception {

        var params = exchange.getQueryParameters();

        String order = "NAME";
        String dir = "ASC";
        int pageSize = 15;
        int page = 0;

        var orderParam = params.getOrDefault("order", null);
        if (orderParam != null) {
            order = orderParam.getFirst();
        }
        var dirParam = params.getOrDefault("dir", null);
        if (dirParam != null) {
            dir = dirParam.getFirst();
        }
        var pageSizeParam = params.getOrDefault("pageSize", null);
        if (pageSizeParam != null) {
            pageSize = Integer.valueOf(pageSizeParam.getFirst());
        }
        if (pageSize <= 0)
            ApiResponse.send(exchange, 200, null);

        var pageParam = params.getOrDefault("page", null);
        if (pageParam != null) {
            page = Integer.valueOf(pageParam.getFirst());
        }

        var finalOrder = order;
        var finalDir = dir;
        var finalPageSize = pageSize;
        var finalPage = page;

        Future<PaginatedListDTO> future = Bukkit.getScheduler().callSyncMethod(plugin,
                () -> {
                    var settlements = UnitedLandsDataManager.instance().getSettlements();

                    var numPages = (int) Math.ceil((double) settlements.size() / (double) finalPageSize);

                    LinkedHashSet<Settlement> sorted = new LinkedHashSet<>();
                    var desc = "DESC".equals(finalDir);

                    Comparator<Settlement> comparator;
                    switch (finalOrder) {
                        case "NAME":
                            comparator = Comparator.comparing(Settlement::getName);
                            break;
                        case "SIZE":
                            comparator = Comparator.comparing(Settlement::getSize);
                            break;
                        case "CITIZENS":
                            comparator = Comparator.comparing(Settlement::getCitizenCount);
                            break;
                        case "BALANCE":
                            comparator = Comparator.comparing(Settlement::getBalance);
                            break;
                        case "UPKEEP":
                            comparator = Comparator.comparing(Settlement::getUpkeep);
                            break;
                        default:
                            comparator = Comparator.comparing(Settlement::getName);
                            break;
                    }

                    if (desc) {
                        comparator = comparator.reversed();
                    }

                    sorted = settlements.stream()
                            .sorted(comparator)
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    var selection = sorted.stream().skip(finalPage * finalPageSize).limit(finalPageSize)
                            .map(s -> new SettlementListDTO(s))
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    return new PaginatedListDTO(finalPageSize, finalPage, finalOrder, finalDir, numPages, selection);
                });

        ApiResponse.send(exchange, 200, future.get());
    }
}

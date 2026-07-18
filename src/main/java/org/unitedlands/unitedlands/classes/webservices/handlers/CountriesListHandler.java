package org.unitedlands.unitedlands.classes.webservices.handlers;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.unitedlands.unitedlands.classes.Country;
import org.unitedlands.unitedlands.classes.webservices.ApiResponse;
import org.unitedlands.unitedlands.classes.webservices.dto.CountryDTO;
import org.unitedlands.unitedlands.classes.webservices.dto.PaginatedListDTO;
import org.unitedlands.unitedlands.managers.UnitedLandsDataManager;

import io.undertow.server.HttpServerExchange;

public class CountriesListHandler extends BaseGetHandler {

    private final Plugin plugin;

    public CountriesListHandler(Plugin plugin) {
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
                    var countries = UnitedLandsDataManager.instance().getCountries();

                    var numPages = (int) Math.ceil((double) countries.size() / (double) finalPageSize);

                    LinkedHashSet<Country> sorted = new LinkedHashSet<>();
                    var desc = "DESC".equals(finalDir);

                    Comparator<Country> comparator;
                    switch (finalOrder) {
                        case "NAME":
                            comparator = Comparator.comparing(Country::getName);
                            break;
                        case "REGIONS":
                            comparator = Comparator.comparing(Country::getRegionCount);
                            break;
                        case "SETTLEMENTS":
                            comparator = Comparator.comparing(Country::getSettlementCount);
                            break;
                        case "CITIZENS":
                            comparator = Comparator.comparing(Country::getCitizenCount);
                            break;
                        case "BALANCE":
                            comparator = Comparator.comparing(Country::getBalance);
                            break;
                        case "UPKEEP":
                            comparator = Comparator.comparing(Country::getUpkeep);
                            break;
                        default:
                            comparator = Comparator.comparing(Country::getName);
                            break;
                    }

                    if (desc) {
                        comparator = comparator.reversed();
                    }

                    sorted = countries.stream()
                            .sorted(comparator)
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    var selection = sorted.stream().skip(finalPage * finalPageSize).limit(finalPageSize)
                            .map(s -> new CountryDTO(s))
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    return new PaginatedListDTO(finalPageSize, finalPage, finalOrder, finalDir, numPages, selection);
                });

        ApiResponse.send(exchange, 200, future.get());
    }
}
